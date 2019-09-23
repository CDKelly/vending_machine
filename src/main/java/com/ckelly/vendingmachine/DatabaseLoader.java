package com.ckelly.vendingmachine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SlotRepository repository;
    @Autowired
    private DataSource dataSource;
    private Connection connection;

    @Autowired
    public DatabaseLoader(SlotRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
    	//clearTables();
        seedData();
    }

    /* any/all of the clearTables stuff was taken from:
     * https://brightinventions.pl/blog/clear-database-in-spring-boot-tests/
     * However, none of it was actually used b/c it put my db in a bad state
     */
    public void clearTables() {
        try {
            connection = dataSource.getConnection();
            tryToClearTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToClearTables() throws SQLException {
        List<String> tableNames = getTableNames();
        clear(tableNames);
    }

    private List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getTables(
                connection.getCatalog(), null, null, new String[]{"TABLE"});

        while (rs.next()) {
            tableNames.add(rs.getString("TABLE_NAME"));
        }

        return tableNames;
    }

    private void clear(List<String> tableNames) throws SQLException {
        Statement statement = buildSqlStatement(tableNames);

        logger.debug("Executing SQL");
        statement.executeBatch();
    }

    private Statement buildSqlStatement(List<String> tableNames) throws SQLException {
        Statement statement = connection.createStatement();

        statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 0"));
        addDeleteSatements(tableNames, statement);
        statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 1"));

        return statement;
    }

    private void addDeleteSatements(List<String> tableNames, Statement statement) {
        tableNames.forEach(tableName -> {
            try {
                statement.addBatch(sql("DELETE FROM " + tableName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String sql(String sql) {
        logger.debug("Adding SQL: {}", sql);
        return sql;
    }
    
    private void seedData() {
    	repository.save(new Slot(new Long(1), "Coke", new BigDecimal("1.50"), 1, 10));
        repository.save(new Slot(new Long(2), "Pepsi", new BigDecimal("1.50"), 2, 10));
        repository.save(new Slot(new Long(3), "Dr. Pepper", new BigDecimal("1.25"), 3, 10));
        repository.save(new Slot(new Long(4), "Sprite", new BigDecimal("1.50"), 4, 10));
        repository.save(new Slot(new Long(5), "Mountain Dew", new BigDecimal("1.50"), 5, 10));
        repository.save(new Slot(new Long(6), "Cherry Coke", new BigDecimal("1.25"), 6, 10));
        repository.save(new Slot(new Long(7), "Diet Coke", new BigDecimal("1.50"), 7, 10));
        repository.save(new Slot(new Long(8), "Dr. Pepper Berries & Cream", new BigDecimal("1.50"), 8, 10));
        repository.save(new Slot(new Long(9), "Root Beer", new BigDecimal("1.25"), 9, 10));
        repository.save(new Slot(new Long(10), "Mountain Dew Code Red", new BigDecimal("1.25"), 9, 10));
        
        System.out.println("SEEDING DATABASE WITH SLOTS:");
        repository.findAll().forEach(System.out::println);
    }
    
}
