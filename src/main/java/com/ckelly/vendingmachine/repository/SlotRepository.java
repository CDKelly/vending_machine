package com.ckelly.vendingmachine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ckelly.vendingmachine.model.Slot;

@Repository
public interface SlotRepository extends CrudRepository<Slot, Long> {

}
