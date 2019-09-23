package com.ckelly.vendingmachine.controller;

import java.math.BigDecimal;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ckelly.vendingmachine.exception.InsufficientFundsException;
import com.ckelly.vendingmachine.exception.NoItemInventoryException;
import com.ckelly.vendingmachine.exception.NoSuchItemException;
import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;
import com.ckelly.vendingmachine.service.VendingMachineService;

@RestController
@Validated
@RequestMapping("/vendingmachine")
class VendingMachineController {
	
	private final Logger log = LoggerFactory.getLogger(VendingMachineController.class);

    @Autowired
    private VendingMachineService service;
    @Autowired
    private SlotRepository slotRepository;

    public VendingMachineController(VendingMachineService service, SlotRepository slotRepository) {
        this.service = service;
        this.slotRepository = slotRepository;
    }

    @GetMapping("/slots")
    public Iterable<Slot> getAllSlots() {
    	log.info("Retrieving all vending machine slots");
        return service.getAllSlots();
    }

    @PutMapping("/slots/{id}/{usersMoney}")
    public ResponseEntity<String> makeSelection(@PathVariable("id") @NotNull(message = "Please select a valid slot") Long slotId, 
		@PathVariable("usersMoney") @NotNull(message = "Please include change for purchase") @DecimalMin("00.00") @DecimalMax("99.99") 
    	BigDecimal usersMoney) {
    	log.info("Making selection >>> Slot ID: {}, Change inserted: {}", slotId, usersMoney);
    	
        ResponseEntity<String> result = null;

        try {
            BigDecimal change = service.makeSelection(usersMoney, slotId);
            String changeStr = service.formatBigDecimal(change);
            String productName = slotRepository.findById(slotId).get().getProductName();
            String message = String.format("Thank you for purchasing %s. "
            		+ "Please take any change (%s) in the change chute", productName, changeStr);
            result = ResponseEntity.ok().body(message);
        } catch (NoSuchItemException | NoItemInventoryException |
                 InsufficientFundsException e) {
        	log.info("Service responded with exception : {}", getClassName(e));
            result = ResponseEntity.badRequest().body(e.getMessage());
        }
        
        return result;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    	log.info("ConstraintViolationException in {}: " + e.getMessage(), getClassName(this));
    	log.info("Stack trace : " + e.getStackTrace().toString());
        return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<String> handleException(Exception e) {
    	log.warn("Uncaught exception in {}. Responding with Service Unavailable", getClassName(this));
    	log.info("Stack trace : " + e.getStackTrace().toString());
    	return new ResponseEntity<String>("This vending machine is temporarily "
        		+ "out of order. We apologize for the inconvenience", HttpStatus.SERVICE_UNAVAILABLE);
    	
    }
    
    private String getClassName(Object obj) {
    	return obj.getClass().getSimpleName();
    }
    
}
