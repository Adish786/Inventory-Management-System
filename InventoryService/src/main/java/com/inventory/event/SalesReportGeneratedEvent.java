package com.inventory.event;

import com.inventory.model.SalesReport;
import org.springframework.context.ApplicationEvent;

public class SalesReportGeneratedEvent extends ApplicationEvent {
    private final SalesReport report;

    public SalesReportGeneratedEvent(Object source, SalesReport report) {
        super(source);
        this.report = report;
    }

    public SalesReport getReport() {
        return report;
    }
}

