package com.srharvester;

import com.srharvester.model.Bill;
import com.srharvester.service.BillService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.srharvester.repository.BillRepository;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class BillServiceTest {
    @Test
    public void calculateTest() {
        BillRepository repo = Mockito.mock(BillRepository.class);
        BillService svc = new BillService(repo);
        Bill b = new Bill();
        b.setStartTime(LocalTime.of(8,0));
        b.setStopTime(LocalTime.of(10,30));
        b.setHourlyRate(200);
        svc.calculate(b);
        assertEquals(500.0, b.getTotalBill());
    }
}
