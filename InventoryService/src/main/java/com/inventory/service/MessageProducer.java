package com.inventory.service;

import com.inventory.model.SalesReport;

public interface MessageProducer {
    void sendSalesReport(SalesReport report);
}

