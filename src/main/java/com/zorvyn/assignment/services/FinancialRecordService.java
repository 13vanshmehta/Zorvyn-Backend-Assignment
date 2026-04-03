package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.CreateRecordDTO;
import com.zorvyn.assignment.dtos.FinancialRecordDTO;
import com.zorvyn.assignment.dtos.UpdateRecordDTO;
import com.zorvyn.assignment.entities.FinancialRecord;
import com.zorvyn.assignment.entities.Users;
import com.zorvyn.assignment.enums.RecordType;
import com.zorvyn.assignment.exceptions.ResourceNotFoundException;
import com.zorvyn.assignment.repositories.FinancialRecordRepository;
import com.zorvyn.assignment.repositories.UsersRepository;
import com.zorvyn.assignment.specifications.FinancialRecordSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FinancialRecordService implements FinancialRecordServiceInterface {

    private final FinancialRecordRepository recordRepository;
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public FinancialRecordDTO createRecord(CreateRecordDTO dto, String creatorEmail) {
        Users creator = usersRepository.findByEmailId(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + creatorEmail));

        FinancialRecord record = FinancialRecord.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .category(dto.getCategory())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .createdBy(creator)
                .build();

        FinancialRecord saved = recordRepository.save(record);
        return mapToDTO(saved);
    }

    @Override
    public FinancialRecordDTO getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        return mapToDTO(record);
    }

    @Override
    public Page<FinancialRecordDTO> getAllRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<FinancialRecord> spec = FinancialRecordSpecification.withFilters(
                type, category, startDate, endDate
        );
        return recordRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public FinancialRecordDTO updateRecord(Long id, UpdateRecordDTO dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));

        if (dto.getAmount() != null) {
            record.setAmount(dto.getAmount());
        }
        if (dto.getType() != null) {
            record.setType(dto.getType());
        }
        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            record.setCategory(dto.getCategory());
        }
        if (dto.getDate() != null) {
            record.setDate(dto.getDate());
        }
        if (dto.getNotes() != null) {
            record.setNotes(dto.getNotes());
        }

        FinancialRecord saved = recordRepository.save(record);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        recordRepository.delete(record);
    }

    @Override
    public Map<String, Object> getRecordsSummary() {
        List<FinancialRecord> allRecords = recordRepository.findAll();

        BigDecimal totalIncome = allRecords.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = allRecords.stream()
                .filter(r -> r.getType() == RecordType.EXPENSE)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        long incomeCount = allRecords.stream()
                .filter(r -> r.getType() == RecordType.INCOME).count();

        long expenseCount = allRecords.stream()
                .filter(r -> r.getType() == RecordType.EXPENSE).count();

        // Category breakdown
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        allRecords.forEach(r -> categoryTotals.merge(
                r.getCategory(),
                r.getType() == RecordType.INCOME ? r.getAmount() : r.getAmount().negate(),
                BigDecimal::add
        ));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRecords", allRecords.size());
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netBalance", netBalance);
        summary.put("incomeCount", incomeCount);
        summary.put("expenseCount", expenseCount);
        summary.put("categoryBreakdown", categoryTotals);

        return summary;
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
