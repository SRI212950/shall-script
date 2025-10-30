package com.srharvester.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillServiceTest {

    @Test
    public void testCalculateHoursAndBill() {
        BillService s = new BillService(null, 3000.0, 100);
        double hrs = s.calculateHours("09:30 AM", "11:00 AM");
        assertEquals(1.5, hrs, 0.01);
        double bill = s.calculateBill(hrs, 3000.0);
        assertEquals(4500.0, bill, 0.01);
    }

    @Test
    public void testOvernight() {
        BillService s = new BillService(null, 3000.0, 100);
        double hrs = s.calculateHours("11:00 PM", "01:00 AM");
        assertEquals(2.0, hrs, 0.01);
    }
}
