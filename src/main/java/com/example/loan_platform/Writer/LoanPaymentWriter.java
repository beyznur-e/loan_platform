package com.example.loan_platform.Writer;

import com.example.loan_platform.Entity.LoanPayments;
import com.example.loan_platform.Repository.LoanPaymentsRepository;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import java.util.ArrayList;
import java.util.List;


@Component
public class LoanPaymentWriter implements ItemWriter<List<LoanPayments>> {

    private final LoanPaymentsRepository loanPaymentsRepository;

    public LoanPaymentWriter(LoanPaymentsRepository loanPaymentsRepository) {
        this.loanPaymentsRepository = loanPaymentsRepository;
    }

    @Override
    public void write(Chunk<? extends List<LoanPayments>> chunk) {
        List<LoanPayments> allPayments = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();
        loanPaymentsRepository.saveAll(allPayments);
    }
}
