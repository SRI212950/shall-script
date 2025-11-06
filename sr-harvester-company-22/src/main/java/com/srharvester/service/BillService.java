package com.srharvester.service;

import com.srharvester.model.Bill;
import com.srharvester.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BillService {
    private final BillRepository repo;
    private static final int MAX_BILLS = 150;

    public BillService(BillRepository repo) {
        this.repo = repo;
    }

    public Bill calculate(Bill bill) {
        // Apply AM/PM to times (if provided as part of front-end strings it's converted there)
        LocalTime start = bill.getStartTime();
        LocalTime stop = bill.getStopTime();
        if (start == null || stop == null) {
            bill.setTotalBill(0);
            return bill;
        }
        Duration dur = Duration.between(start, stop);
        if (dur.isNegative()) {
            // assume stop next day
            dur = dur.plusDays(1);
        }
        double hours = dur.toMinutes() / 60.0;
        double total = Math.round(hours * bill.getHourlyRate() * 100.0) / 100.0;
        bill.setTotalBill(total);
        return bill;
    }

    public Bill save(Bill bill) {
        long count = repo.count();
        if (count >= MAX_BILLS) {
            throw new IllegalStateException("Max bills saved (" + MAX_BILLS + ")");
        }
        if (!bill.isPaid()) {
            throw new IllegalStateException("Cannot save unpaid bill");
        }
        return repo.save(bill);
    }

    public List<Bill> listAll() {
        return repo.findAll();
    }

    public Bill find(Long id) {
        return repo.findById(id).orElse(null);
    }
}
