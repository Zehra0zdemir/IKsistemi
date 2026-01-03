package com.hrms.controller;

import com.hrms.service.ReportService;

public class ReportController {

    private static final ReportService reportService = new ReportService();

    public static ReportService.DashboardStats getStats() {
        return reportService.loadDashboardStats();
    }
}
