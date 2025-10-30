package com.srharvester.service;

import com.srharvester.model.Bill;
import com.srharvester.repository.BillRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository repo;
    private final double defaultRate;
    private final int maxRecords;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");

    public BillService(BillRepository repo, @Value("${app.defaultHourlyRate}") double defaultRate,
                       @Value("${app.maxRecords}") int maxRecords) {
        this.repo = repo;
        this.defaultRate = defaultRate;
        this.maxRecords = maxRecords;
    }

    private LocalTime parseTime(String t) {
        try {
            return LocalTime.parse(t, fmt);
        } catch (Exception ex) {
            String s = t.trim().toUpperCase();
            if (!s.contains(":")) s = s.replace(" ", ":");
            return LocalTime.parse(s, fmt);
        }
    }

    public double calculateHours(String start, String stop) {
        LocalTime s = parseTime(start);
        LocalTime e = parseTime(stop);

        Duration d = Duration.between(s, e);
        if (d.isNegative()) {
            d = Duration.between(s, LocalTime.MAX).plus(Duration.between(LocalTime.MIN, e)).plusSeconds(1);
        }
        double hours = d.toMinutes() / 60.0;
        return Math.round(hours * 100.0) / 100.0;
    }

    public double calculateBill(double hours, double rate) {
        double bill = hours * rate;
        return Math.round(bill * 100.0) / 100.0;
    }

    public Bill saveBill(Bill b) {
        if (b.getDate() == null) b.setDate(LocalDate.now());
        if (b.getHourlyRate() <= 0) b.setHourlyRate(defaultRate);

        if (b.getTotalHours() <= 0 && b.getStartTime() != null && b.getStopTime() != null) {
            b.setTotalHours(calculateHours(b.getStartTime(), b.getStopTime()));
        }
        if (b.getTotalBill() <= 0) {
            b.setTotalBill(calculateBill(b.getTotalHours(), b.getHourlyRate()));
        }

        long count = repo.count();
        if (count >= maxRecords) {
            List<Bill> all = repo.findAll();
            all.sort((x,y) -> x.getId().compareTo(y.getId()));
            if (!all.isEmpty()) {
                repo.delete(all.get(0));
            }
        }

        return repo.save(b);
    }

    public List<Bill> findAll() {
        return repo.findAll();
    }

    public Optional<Bill> findById(Long id) {
        return repo.findById(id);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
