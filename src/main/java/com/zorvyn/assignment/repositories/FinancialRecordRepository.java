package com.zorvyn.assignment.repositories;

import com.zorvyn.assignment.entities.FinancialRecord;
import com.zorvyn.assignment.enums.RecordType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FinancialRecordRepository extends
        JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    // ── Aggregation Queries for Dashboard ──

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = :type")
    BigDecimal sumByType(@Param("type") RecordType type);

    @Query("SELECT COUNT(f) FROM FinancialRecord f WHERE f.type = :type")
    long countByType(@Param("type") RecordType type);

    // Category-wise breakdown: returns [category, type, total]
    @Query("SELECT f.category, f.type, COALESCE(SUM(f.amount), 0) " +
            "FROM FinancialRecord f GROUP BY f.category, f.type ORDER BY f.category")
    List<Object[]> getCategorySummary();

    // Recent activity — uses Pageable so caller controls limit
    @Query("SELECT f FROM FinancialRecord f ORDER BY f.date DESC, f.createdAt DESC")
    List<FinancialRecord> findRecentRecords(Pageable pageable);

    // Monthly trends (native query for MySQL YEAR/MONTH functions)
    // Returns [year, month, type, total]
    @Query(value = "SELECT YEAR(date) AS yr, MONTH(date) AS mn, type, COALESCE(SUM(amount), 0) AS total " +
            "FROM financial_records GROUP BY YEAR(date), MONTH(date), type " +
            "ORDER BY yr DESC, mn DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyTrends();

    // Weekly trends for a given year and month (native query)
    // Returns [week_number, type, total]
    @Query(value = "SELECT WEEK(date, 1) AS wk, type, COALESCE(SUM(amount), 0) AS total " +
            "FROM financial_records " +
            "WHERE YEAR(date) = :year AND MONTH(date) = :month " +
            "GROUP BY WEEK(date, 1), type ORDER BY wk",
            nativeQuery = true)
    List<Object[]> getWeeklyTrends(@Param("year") int year, @Param("month") int month);
}
