package com.zorvyn.assignment.dtos;

import com.zorvyn.assignment.enums.RecordType;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecordDTO {

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private RecordType type;

    private String category;

    private LocalDate date;

    private String notes;
}
