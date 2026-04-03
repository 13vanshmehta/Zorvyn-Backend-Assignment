package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.dtos.CreateRecordDTO;
import com.zorvyn.assignment.dtos.FinancialRecordDTO;
import com.zorvyn.assignment.dtos.UpdateRecordDTO;
import com.zorvyn.assignment.enums.RecordType;
import com.zorvyn.assignment.responses.ApiResponse;
import com.zorvyn.assignment.services.FinancialRecordServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${basePath}/records")
@Tag(name = "Financial Records", description = "CRUD and filtering for financial records")
public class FinancialRecordController {

    private final FinancialRecordServiceInterface recordService;

    // ── Create (ADMIN only) ──

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a financial record", description = "Admin only — create a new income or expense record")
    public ResponseEntity<ApiResponse> createRecord(
            @Valid @RequestBody CreateRecordDTO dto,
            Authentication authentication
    ) {
        FinancialRecordDTO record = recordService.createRecord(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .message("Financial record created successfully")
                        .statusCode(HttpStatus.CREATED)
                        .data(record)
                        .build());
    }

    // ── Read (VIEWER, ANALYST, ADMIN) ──

    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "List financial records",
            description = "Accessible to all roles. Supports optional filtering by type, category, date range, and pagination.")
    public ResponseEntity<ApiResponse> getAllRecords(
            @Parameter(description = "Filter by type: INCOME or EXPENSE")
            @RequestParam(required = false) RecordType type,

            @Parameter(description = "Filter by category name")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter start date (yyyy-MM-dd)")
            @RequestParam(required = false) LocalDate startDate,

            @Parameter(description = "Filter end date (yyyy-MM-dd)")
            @RequestParam(required = false) LocalDate endDate,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort by field (e.g. date, amount)")
            @RequestParam(defaultValue = "date") String sortBy,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FinancialRecordDTO> records = recordService.getAllRecords(type, category, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.builder()
                .message("Financial records retrieved")
                .statusCode(HttpStatus.OK)
                .data(Map.of(
                        "records", records.getContent(),
                        "currentPage", records.getNumber(),
                        "totalPages", records.getTotalPages(),
                        "totalRecords", records.getTotalElements(),
                        "pageSize", records.getSize()
                ))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    @Operation(summary = "Get a financial record by ID", description = "Accessible to all roles")
    public ResponseEntity<ApiResponse> getRecordById(@PathVariable Long id) {
        FinancialRecordDTO record = recordService.getRecordById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Financial record retrieved")
                .statusCode(HttpStatus.OK)
                .data(record)
                .build());
    }

    // ── Update (ADMIN only) ──

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a financial record", description = "Admin only — partial update of a record")
    public ResponseEntity<ApiResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordDTO dto
    ) {
        FinancialRecordDTO record = recordService.updateRecord(id, dto);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Financial record updated successfully")
                .statusCode(HttpStatus.OK)
                .data(record)
                .build());
    }

    // ── Delete (ADMIN only) ──

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a financial record", description = "Admin only — permanently delete a record")
    public ResponseEntity<ApiResponse> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Financial record deleted successfully")
                .statusCode(HttpStatus.OK)
                .data(null)
                .build());
    }

    // ── Summary / Analytics (ANALYST, ADMIN) ──

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get financial summary",
            description = "Analyst & Admin — totals for income, expense, net balance, and category breakdown")
    public ResponseEntity<ApiResponse> getRecordsSummary() {
        Map<String, Object> summary = recordService.getRecordsSummary();
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Financial summary retrieved")
                .statusCode(HttpStatus.OK)
                .data(summary)
                .build());
    }
}
