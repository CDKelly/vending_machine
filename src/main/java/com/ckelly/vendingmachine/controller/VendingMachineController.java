package com.ckelly.vendingmachine.controller;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.text.NumberFormat;

@RestController
@Validated
@RequestMapping("/vendingmachine")
class VendingMachineController {

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
        return slotRepository.findAll();
    }

    @PutMapping("/slots/{id}")
    public ResponseEntity<String> makeSelection(@PathVariable("id") @NotBlank 
    		@Size(min = 1, max = 100) Long slotId, @RequestBody BigDecimal usersMoney) {
        ResponseEntity<String> result = null;

        // check for valid user money on the RequestBody BigDecimal

        /*
          try-catches don't scale well as APIs grow,
          but they'll work for the time being
         */
        try {
            BigDecimal change = service.makeSelection(usersMoney, slotId);
            String changeStr = NumberFormat.getCurrencyInstance().format(change);
            String productName = slotRepository.findById(slotId).get().getProductName();
            String message = String.format("Thank you for purchasing %s. "
            		+ "Please take any change (%s) in the change chute", productName, changeStr);
            result = ResponseEntity.ok().body(message);
        } catch (NoSuchItemException | NoItemInventoryException |
                 InsufficientFundsException e) {
            result = ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            if (result == null) {
                result = new ResponseEntity<String>("This vending machine is temporarily "
                		+ "out of order. We apologize for the inconvenience", HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
        
        return result;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body("invalid request: " + e.getMessage());
    }
}
