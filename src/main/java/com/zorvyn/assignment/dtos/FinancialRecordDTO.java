package com.zorvyn.assignment.dtos;

import com.zorvyn.assignment.enums.RecordType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordDTO {
    private Long id;
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdByEmail;
    private String createdByName;
    private Instant createdAt;
    private Instant updatedAt;
}
