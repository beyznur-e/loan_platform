package com.example.loan_platform.Processor;

import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.Enum.StatusPayments;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Spring Batch için bir işlemci (processor) sınıfı.
@Component
public class LoanPaymentProcessor implements ItemProcessor<LoanApplications, List<LoanPayments>> {

    // Her bir kredi başvurusu için aylık ödeme planları oluşturur.
    @Override
    public List<LoanPayments> process(LoanApplications application) throws Exception {
        if (application == null ||
                application.getLoanScoring() == null ||
                !Decision.APPROVED.equals(application.getLoanScoring().getDecision())) {
            return Collections.emptyList(); // Eğer koşullar sağlanmazsa boş liste dön
        }

        List<LoanPayments> payments = new ArrayList<>();
        double amountPerMonth = application.getAmount() / application.getTerm();
        LocalDate firstDueDate = application.getCreatedAt().toLocalDate();

        for (int i = 0; i < application.getTerm(); i++) {
            LoanPayments payment = new LoanPayments();
            payment.setApplication(application);
            payment.setAmount(amountPerMonth);
            payment.setDueDate(firstDueDate.plusMonths(i));
            payment.setStatus(StatusPayments.PENDING);
            payments.add(payment);
        }

        return payments;
    }
}