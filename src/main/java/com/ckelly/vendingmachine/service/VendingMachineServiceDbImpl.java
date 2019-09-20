package com.ckelly.vendingmachine.service;

import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;
import com.ckelly.vendingmachine.exception.NoSuchItemException;
import com.ckelly.vendingmachine.exception.NoItemInventoryException;
import com.ckelly.vendingmachine.exception.InsufficientFundsException;

import java.math.RoundingMode;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VendingMachineServiceDbImpl implements VendingMachineService {

    @Autowired
    private SlotRepository slotRepository;

    public VendingMachineServiceDbImpl(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public BigDecimal makeSelection(BigDecimal usersMoney, Long slotId) 
    		throws NoSuchItemException, NoItemInventoryException, InsufficientFundsException {
        Optional<Slot> slot = slotRepository.findById(slotId);
        
        if (!slot.isPresent()) {
          throw new NoSuchItemException("We do not carry the requested item. We apologize "
          		+ "for any inconvenience this may have caused.");
        }

        Slot usersSlot = slot.get();
        
        if (!isProductInStock(usersSlot)) {
            throw new NoItemInventoryException("SOLD OUT! Please select another item.");
        }
        
        BigDecimal slotPrice = usersSlot.getPrice();
        if (hasInsufficientFunds(usersMoney, slotPrice)) {
            BigDecimal deficit = slotPrice.subtract(usersMoney).setScale(2, RoundingMode.HALF_UP);
            throw new InsufficientFundsException("Insufficient Funds! Please deposit: " 
            		+ deficit.toString());
        }
        
        decrementSlotQuantityByAmt(usersSlot, new Integer(1));
        return getChangeAmt(usersMoney, slotPrice);
    }

    public boolean isProductInStock(Slot slot) {
        return slot.getCurrentQuantity() > 0;
    }

    public boolean hasInsufficientFunds(BigDecimal usersMoney, BigDecimal slotPrice) {
        return usersMoney.compareTo(slotPrice) < 0;
    }

    public void decrementSlotQuantityByAmt(Slot slot, Integer amtToDecrement) {
        slot.setCurrentQuantity(slot.getCurrentQuantity() - amtToDecrement);
        slotRepository.save(slot);
    }

    public BigDecimal getChangeAmt(BigDecimal usersMoney, BigDecimal slotPrice) {
        return usersMoney.subtract(slotPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
