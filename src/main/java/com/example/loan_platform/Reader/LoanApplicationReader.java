package com.example.loan_platform.Reader;

import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Repository.LoanScoringRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// Onaylanmış kredi başvurularını okur ve işlenmek üzere sırayla döner.
@Component
public class LoanApplicationReader implements ItemReader<LoanApplications> {

    private final LoanScoringRepository loanScoringRepository;
    private Iterator<LoanApplications> applicationsIterator;

    @Autowired
    public LoanApplicationReader(LoanScoringRepository loanScoringRepository) {
        this.loanScoringRepository = loanScoringRepository;
    }

    @PostConstruct
    public void init() {
        try {
            List<LoanApplications> approvedApplications = loanScoringRepository.findApprovedApplications();
            if (approvedApplications == null) {
                approvedApplications = Collections.emptyList();
            }
            applicationsIterator = approvedApplications.iterator();
        } catch (Exception e) {
            applicationsIterator = Collections.emptyIterator();
        }
    }

    @Override
    public LoanApplications read() {
        if (applicationsIterator != null && applicationsIterator.hasNext()) {
            return applicationsIterator.next();
        }
        return null;
    }
}