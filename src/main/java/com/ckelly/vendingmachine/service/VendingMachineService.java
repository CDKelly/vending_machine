package com.ckelly.vendingmachine.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ckelly.vendingmachine.exception.InsufficientFundsException;
import com.ckelly.vendingmachine.exception.NoItemInventoryException;
import com.ckelly.vendingmachine.exception.NoSuchItemException;
import com.ckelly.vendingmachine.model.Slot;

@Service
public interface VendingMachineService {
	public Iterable<Slot> getAllSlots();
	
    public BigDecimal makeSelection(BigDecimal usersMoney, Long slotId) 
    		throws NoSuchItemException, NoItemInventoryException, InsufficientFundsException;

    public boolean isProductInStock(Slot slot);

    public boolean hasInsufficientFunds(BigDecimal usersMoney, BigDecimal slotPrice);

    public void decrementSlotQuantityByAmt(Slot slot, Integer amtToDecrement);

    public BigDecimal getChangeAmt(BigDecimal usersMoney, BigDecimal slotPrice);
    
    public String formatBigDecimal(BigDecimal bigDecimal);
}
