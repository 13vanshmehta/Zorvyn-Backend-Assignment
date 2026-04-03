package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.FinancialRecordDTO;
import com.zorvyn.assignment.entities.FinancialRecord;
import com.zorvyn.assignment.enums.RecordType;
import com.zorvyn.assignment.repositories.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardService implements DashboardServiceInterface {

    private final FinancialRecordRepository recordRepository;

    /**
     * High-level overview: total income, expenses, net balance, counts.
     */
    @Override
    public Map<String, Object> getOverview() {
        BigDecimal totalIncome = recordRepository.sumByType(RecordType.INCOME);
        BigDecimal totalExpense = recordRepository.sumByType(RecordType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        long incomeCount = recordRepository.countByType(RecordType.INCOME);
        long expenseCount = recordRepository.countByType(RecordType.EXPENSE);
        long totalRecords = recordRepository.count();

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalIncome", totalIncome);
        overview.put("totalExpense", totalExpense);
        overview.put("netBalance", netBalance);
        overview.put("totalRecords", totalRecords);
        overview.put("incomeCount", incomeCount);
        overview.put("expenseCount", expenseCount);

        return overview;
    }

    /**
     * Category-wise totals: income, expense, and net per category.
     */
    @Override
    public List<Map<String, Object>> getCategorySummary() {
        List<Object[]> rows = recordRepository.getCategorySummary();

        // Group by category, then merge INCOME/EXPENSE totals
        Map<String, Map<String, Object>> categoryMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            String category = (String) row[0];
            RecordType type = (RecordType) row[1];
            BigDecimal total = (BigDecimal) row[2];

            categoryMap.computeIfAbsent(category, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("category", k);
                entry.put("totalIncome", BigDecimal.ZERO);
                entry.put("totalExpense", BigDecimal.ZERO);
                entry.put("net", BigDecimal.ZERO);
                return entry;
            });

            Map<String, Object> entry = categoryMap.get(category);
            if (type == RecordType.INCOME) {
                entry.put("totalIncome", total);
            } else {
                entry.put("totalExpense", total);
            }
            entry.put("net", ((BigDecimal) entry.get("totalIncome"))
                    .subtract((BigDecimal) entry.get("totalExpense")));
        }

        return new ArrayList<>(categoryMap.values());
    }

    /**
     * Recent activity: latest N financial records.
     */
    @Override
    public List<FinancialRecordDTO> getRecentActivity(int limit) {
        List<FinancialRecord> records = recordRepository.findRecentRecords(PageRequest.of(0, limit));
        return records.stream().map(this::mapToDTO).toList();
    }

    /**
     * Monthly trends: income/expense totals per month.
     */
    @Override
    public List<Map<String, Object>> getMonthlyTrends() {
        List<Object[]> rows = recordRepository.getMonthlyTrends();

        // Group by year-month, then merge INCOME/EXPENSE
        Map<String, Map<String, Object>> trendMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            String type = (String) row[2];
            BigDecimal total = (BigDecimal) row[3];

            String key = year + "-" + String.format("%02d", month);
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            trendMap.computeIfAbsent(key, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("year", year);
                entry.put("month", month);
                entry.put("monthName", monthName);
                entry.put("totalIncome", BigDecimal.ZERO);
                entry.put("totalExpense", BigDecimal.ZERO);
                entry.put("net", BigDecimal.ZERO);
                return entry;
            });

            Map<String, Object> entry = trendMap.get(key);
            if ("INCOME".equals(type)) {
                entry.put("totalIncome", total);
            } else {
                entry.put("totalExpense", total);
            }
            entry.put("net", ((BigDecimal) entry.get("totalIncome"))
                    .subtract((BigDecimal) entry.get("totalExpense")));
        }

        return new ArrayList<>(trendMap.values());
    }

    /**
     * Weekly trends: income/expense per week for a specific month.
     */
    @Override
    public List<Map<String, Object>> getWeeklyTrends(int year, int month) {
        List<Object[]> rows = recordRepository.getWeeklyTrends(year, month);

        Map<Integer, Map<String, Object>> weekMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            int week = ((Number) row[0]).intValue();
            String type = (String) row[1];
            BigDecimal total = (BigDecimal) row[2];

            weekMap.computeIfAbsent(week, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("weekNumber", k);
                entry.put("totalIncome", BigDecimal.ZERO);
                entry.put("totalExpense", BigDecimal.ZERO);
                entry.put("net", BigDecimal.ZERO);
                return entry;
            });

            Map<String, Object> entry = weekMap.get(week);
            if ("INCOME".equals(type)) {
                entry.put("totalIncome", total);
            } else {
                entry.put("totalExpense", total);
            }
            entry.put("net", ((BigDecimal) entry.get("totalIncome"))
                    .subtract((BigDecimal) entry.get("totalExpense")));
        }

        return new ArrayList<>(weekMap.values());
    }

    // ── Mapper ──

    private FinancialRecordDTO mapToDTO(FinancialRecord record) {
        return FinancialRecordDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdByEmail(record.getCreatedBy().getEmailId())
                .createdByName(record.getCreatedBy().getFirstName() + " " + record.getCreatedBy().getLastName())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
