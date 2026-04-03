package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.dtos.FinancialRecordDTO;
import com.zorvyn.assignment.services.DashboardServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class DashboardController {

    private final DashboardServiceInterface dashboardService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public Map<String, Object> getOverview() {
        return dashboardService.getOverview();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public List<Map<String, Object>> getCategorySummary() {
        return dashboardService.getCategorySummary();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public List<FinancialRecordDTO> getRecentActivity(@Argument Integer limit) {
        int actualLimit = (limit != null) ? limit : 10;
        return dashboardService.getRecentActivity(actualLimit);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public List<Map<String, Object>> getMonthlyTrends() {
        return dashboardService.getMonthlyTrends();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public List<Map<String, Object>> getWeeklyTrends(@Argument Integer year, @Argument Integer month) {
        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();
        return dashboardService.getWeeklyTrends(y, m);
    }
}
