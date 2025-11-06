package com.srharvester.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate billDate;
    private String customerName;
    private String mobileNumber;
    private LocalTime startTime;
    private String startAmPm;
    private LocalTime stopTime;
    private String stopAmPm;
    private double hourlyRate;
    private double totalBill;
    private boolean paid;

    public Bill() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public String getStartAmPm() { return startAmPm; }
    public void setStartAmPm(String startAmPm) { this.startAmPm = startAmPm; }
    public LocalTime getStopTime() { return stopTime; }
    public void setStopTime(LocalTime stopTime) { this.stopTime = stopTime; }
    public String getStopAmPm() { return stopAmPm; }
    public void setStopAmPm(String stopAmPm) { this.stopAmPm = stopAmPm; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public double getTotalBill() { return totalBill; }
    public void setTotalBill(double totalBill) { this.totalBill = totalBill; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
