package com.ckelly.vendingmachine.controller;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;
import com.ckelly.vendingmachine.service.VendingMachineService;
import com.ckelly.vendingmachine.exception.InsufficientFundsException;
import com.ckelly.vendingmachine.exception.NoItemInventoryException;
import com.ckelly.vendingmachine.exception.NoSuchItemException;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RestController
@Validated
@RequestMapping("/vendingmachine")
class VendingMachineController {
	
	private final Logger log = LoggerFactory.getLogger(VendingMachineController.class);

    @Autowired
    private SlotRepository slotRepository;
    @Autowired
    private VendingMachineService service;

    public VendingMachineController(SlotRepository slotRepository, VendingMachineService service) {
        this.slotRepository = slotRepository;
        this.service = service;
    }

    @GetMapping("/slots")
    public Iterable<Slot> getAllSlots() {
    	log.info("Retrieving all vending machine slots");
        return slotRepository.findAll();
    }

    @PutMapping("/slots/{id}/{usersMoney}")
    public ResponseEntity<String> makeSelection(@PathVariable("id") @NotNull(message = "Please select a valid slot") Long slotId, 
		@PathVariable("usersMoney") @NotNull(message = "Please include change for purchase") @DecimalMin("00.00") @DecimalMax("100.00") 
    	BigDecimal usersMoney) {
    	log.info("Making selection >>> Slot ID: {}, Change inserted: {}", slotId, usersMoney);
    	
        ResponseEntity<String> result = null;

        /*
          try-catches don't scale well as APIs grow,
          but they'll work for the time being
         */
        try {
            BigDecimal change = service.makeSelection(usersMoney, slotId);
            String changeStr = service.formatBigDecimal(change);
            String productName = slotRepository.findById(slotId).get().getProductName();
            String message = String.format("Thank you for purchasing %s. "
            		+ "Please take any change (%s) in the change chute", productName, changeStr);
            result = ResponseEntity.ok().body(message);
        } catch (NoSuchItemException | NoItemInventoryException |
                 InsufficientFundsException e) {
        	log.info("Service responded with exception : {}", e.getClass().getSimpleName());
            result = ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            if (result == null) {
                result = new ResponseEntity<String>("This vending machine is temporarily "
                		+ "out of order. We apologize for the inconvenience", HttpStatus.SERVICE_UNAVAILABLE);
            	log.warn("Uncaught error in {}. Responding with Service Unavailable", this.getClass().getSimpleName());
            }
        }
        
        return result;
    }

    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body("invalid request: " + e.getMessage());
    }
    
}
