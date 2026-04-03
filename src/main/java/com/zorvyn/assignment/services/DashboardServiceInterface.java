package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.FinancialRecordDTO;

import java.util.List;
import java.util.Map;

public interface DashboardServiceInterface {

    Map<String, Object> getOverview();

    List<Map<String, Object>> getCategorySummary();

    List<FinancialRecordDTO> getRecentActivity(int limit);

    List<Map<String, Object>> getMonthlyTrends();

    List<Map<String, Object>> getWeeklyTrends(int year, int month);
}
