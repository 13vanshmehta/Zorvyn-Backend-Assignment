package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.CreateRecordDTO;
import com.zorvyn.assignment.dtos.FinancialRecordDTO;
import com.zorvyn.assignment.dtos.UpdateRecordDTO;
import com.zorvyn.assignment.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface FinancialRecordServiceInterface {

    FinancialRecordDTO createRecord(CreateRecordDTO dto, String creatorEmail);

    FinancialRecordDTO getRecordById(Long id);

    Page<FinancialRecordDTO> getAllRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    FinancialRecordDTO updateRecord(Long id, UpdateRecordDTO dto);

    void deleteRecord(Long id);

    Map<String, Object> getRecordsSummary();
}
