package com.srharvester.controller;

import com.srharvester.model.Bill;
import com.srharvester.service.BillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BillController {

    private final BillService service;

    public BillController(BillService service) {
        this.service = service;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        Bill b = new Bill();
        b.setDate(LocalDate.now());
        b.setHourlyRate(3000.0);
        model.addAttribute("bill", b);
        return "index";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Bill bill) {
        service.saveBill(bill);
        return "redirect:/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<Bill> all = service.findAll();
        model.addAttribute("bills", all);
        return "list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Bill bill = service.findById(id).orElse(new Bill());
        model.addAttribute("bill", bill);
        return "index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/list";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Bill bill = service.findById(id).orElse(null);
        model.addAttribute("bill", bill);
        return "view";
    }
}
