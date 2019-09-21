package com.ckelly.vendingmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;

import java.math.BigDecimal;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final SlotRepository repository;

    @Autowired
    public DatabaseLoader(SlotRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... strings) {
        repository.save(new Slot("Coke", new BigDecimal("1.50"), 4, 10));
        repository.save(new Slot("Pepsi", new BigDecimal("1.50"), 6, 10));
        repository.save(new Slot("Dr. Pepper", new BigDecimal("1.25"), 3, 10));
        
        System.out.println("SEEDING DATABASE WITH SLOTS:");
        repository.findAll().forEach(System.out::println);
    }
}
