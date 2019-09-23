package com.ckelly.vendingmachine.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ckelly.vendingmachine.exception.InsufficientFundsException;
import com.ckelly.vendingmachine.exception.NoItemInventoryException;
import com.ckelly.vendingmachine.exception.NoSuchItemException;
import com.ckelly.vendingmachine.model.Slot;
import com.ckelly.vendingmachine.repository.SlotRepository;
import com.ckelly.vendingmachine.service.VendingMachineService;

@RunWith(SpringRunner.class)
@WebMvcTest(VendingMachineController.class)
public class VendingMachineControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
    private VendingMachineService service;
	@MockBean
	private SlotRepository slotRepository;
	
	private ArrayList<Slot> allSlots;
	
	@Before
	public void createTestData() {
		ArrayList<Slot> result = new ArrayList<Slot>();
		
		Slot testSlot0 = new Slot(new Long(0), "testSlot0", new BigDecimal("00.50"), 0, 10);
		Slot testSlot1 = new Slot(new Long(1), "testSlot1", new BigDecimal("1.50"), 1, 10);
		Slot testSlot2 = new Slot(new Long(2), "testSlot2", new BigDecimal("2.50"), 2, 10);
		Slot testSlot3 = new Slot(new Long(3), "testSlot3", new BigDecimal("3.50"), 3, 10);
		result.add(testSlot0);
		result.add(testSlot1);
		result.add(testSlot2);
		result.add(testSlot3);
		
		this.allSlots = result;
	}
	
	@After
	public void deleteTestData() {
		this.allSlots.clear();
	}
	
	@Test
	public void getAllSlots_ShouldReturnSlotsFromService() throws Exception {
		when(service.getAllSlots()).thenReturn(allSlots);
		this.mvc.perform(get("/vendingmachine/slots"))
	  		.andDo(print())
	  		.andExpect(status().isOk())
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[0].productName", is("testSlot0")))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[0].price", is(00.50)))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[0].currentQuantity", is(0)))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[0].maxQuantity", is(10)))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[1].productName", is("testSlot1")))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[1].price", is(1.50)))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[1].currentQuantity", is(1)))
	  		.andExpect(MockMvcResultMatchers.jsonPath("$[1].maxQuantity", is(10)));
	}
	
	@Test
	public void makeSelection_ShouldReturnOkStatus() throws Exception {
		BigDecimal usersMoney = new BigDecimal("5.00");
		Long slotId = new Long(1);
		
		BigDecimal expectedChange = usersMoney.subtract(allSlots.get(1).getPrice()).setScale(2, RoundingMode.HALF_UP);
		when(service.makeSelection(usersMoney, slotId)).thenReturn(expectedChange);
		
		String expectedFormattedChange = NumberFormat.getCurrencyInstance().format(expectedChange);
		when(service.formatBigDecimal(expectedChange)).thenReturn(expectedFormattedChange);
		
		Optional<Slot> testSlot1 = Optional.of(allSlots.get(1));
		when(slotRepository.findById(slotId)).thenReturn(testSlot1);

		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
	  		.andDo(print())
	  		.andExpect(status().isOk())
	  		.andExpect(content().string(String.format("Thank you for purchasing %s. Please take any change (%s) in "
	  				+ "the change chute", testSlot1.get().getProductName(), expectedFormattedChange)));
	}
	
	@Test
	public void makeSelection_UsersMoneyBelowMinShouldReturnBadRequest() throws Exception {
		BigDecimal usersMoney = new BigDecimal("-0.01");
		Long slotId = new Long(1);
		
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
  			.andDo(print())
  			.andExpect(status().isBadRequest())
  			.andExpect(content().string(containsString("Invalid request: ")));
	}
	
	@Test
	public void makeSelection_UsersMoneyAboveMaxShouldReturnBadRequest() throws Exception {
		BigDecimal usersMoney = new BigDecimal("100.00");
		Long slotId = new Long(1);
		
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
  			.andDo(print())
  			.andExpect(status().isBadRequest())
  			.andExpect(content().string(containsString("Invalid request: ")));
	}
	
	@Test
	public void makeSelection_CatchNoSuchItemExceptionAndReturnBadRequest() throws Exception {
		BigDecimal usersMoney = new BigDecimal("5.00");
		Long slotId = new Long(999999);
		
		when(service.makeSelection(usersMoney, slotId)).thenThrow(new NoSuchItemException());
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().string("We do not carry the requested item. We apologize for any inconvenience this may "
					+ "have caused."));
	}
	
	@Test
	public void makeSelection_CatchNoItemInventoryExceptionAndReturnBadRequest() throws Exception {
		BigDecimal usersMoney = new BigDecimal("5.00");
		Long slotId = new Long(0);
		
		when(service.makeSelection(usersMoney, slotId)).thenThrow(new NoItemInventoryException());
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().string("SOLD OUT! Please select another item."));
	}
	
	@Test
	public void makeSelection_CatchInsufficientFundsExceptionAndReturnBadRequest() throws Exception {
		BigDecimal usersMoney = new BigDecimal("5.00");
		Long slotId = new Long(1);
		
		BigDecimal expectedDeficit = allSlots.get(1).getPrice().subtract(usersMoney).setScale(2, RoundingMode.HALF_UP);
		String expectedFormattedDeficit = NumberFormat.getCurrencyInstance().format(expectedDeficit);
		
		when(service.makeSelection(usersMoney, slotId)).thenThrow(new InsufficientFundsException(expectedFormattedDeficit));
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(content().string("Insufficient Funds! Please deposit: " + expectedFormattedDeficit));
	}
	
	@Test
	public void makeSelection_UnexpectedExceptionAndReturnServiceUnavailable() throws Exception {
		BigDecimal usersMoney = new BigDecimal("5.00");
		Long slotId = new Long(1);
		
		when(slotRepository.findById(slotId)).thenThrow(new IllegalArgumentException("unexpected exception"));
		this.mvc.perform(put("/vendingmachine/slots/{id}/{usersMoney}", slotId, usersMoney))
			.andDo(print())
			.andExpect(status().isServiceUnavailable())
			.andExpect(content().string("This vending machine is temporarily out of order. We apologize for "
					+ "the inconvenience"));
	}
}
