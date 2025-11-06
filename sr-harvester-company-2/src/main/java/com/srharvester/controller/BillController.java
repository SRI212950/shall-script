package com.srharvester.controller;

import com.srharvester.model.Bill;
import com.srharvester.service.BillService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/sr-harvester-company")
public class BillController {
    private final BillService service;

    public BillController(BillService service) {
        this.service = service;
    }

    @GetMapping({"/", ""})
    public String index(Model model) {
        model.addAttribute("bill", new Bill());
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billDate,
                            @RequestParam String customerName,
                            @RequestParam String mobileNumber,
                            @RequestParam String startTime,
                            @RequestParam String startAmPm,
                            @RequestParam String stopTime,
                            @RequestParam String stopAmPm,
                            @RequestParam double hourlyRate,
                            Model model) {
        Bill bill = new Bill();
        bill.setBillDate(billDate);
        bill.setCustomerName(customerName);
        bill.setMobileNumber(mobileNumber);
        DateTimeFormatter tfmt = DateTimeFormatter.ofPattern("hh:mm");
        // parse times and apply AM/PM
        LocalTime s = LocalTime.parse(startTime);
        if ("PM".equalsIgnoreCase(startAmPm) && s.getHour() < 12) s = s.plusHours(12);
        if ("AM".equalsIgnoreCase(startAmPm) && s.getHour() == 12) s = s.minusHours(12);
        LocalTime e = LocalTime.parse(stopTime);
        if ("PM".equalsIgnoreCase(stopAmPm) && e.getHour() < 12) e = e.plusHours(12);
        if ("AM".equalsIgnoreCase(stopAmPm) && e.getHour() == 12) e = e.minusHours(12);

        bill.setStartTime(s);
        bill.setStartAmPm(startAmPm);
        bill.setStopTime(e);
        bill.setStopAmPm(stopAmPm);
        bill.setHourlyRate(hourlyRate);

        service.calculate(bill);
        model.addAttribute("bill", bill);
        return "processed";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long id,
                       @RequestParam(required = false) boolean paid,
                       Model model) {
        Bill bill = service.find(id);
        if (bill == null) {
            model.addAttribute("error", "Bill not found");
            return "processed";
        }
        bill.setPaid(paid);
        try {
            service.save(bill);
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("bill", bill);
            return "processed";
        }
        return "redirect:/sr-harvester-company/bills";
    }

    @GetMapping("/bills")
    public String bills(Model model) {
        List<Bill> all = service.listAll();
        model.addAttribute("bills", all);
        return "bills";
    }
}
