package com.example.loan_platform.Config;

import com.example.loan_platform.Entity.Enum.Decision;
import com.example.loan_platform.Entity.LoanApplications;
import com.example.loan_platform.Entity.LoanPayments;
import com.example.loan_platform.Processor.LoanPaymentProcessor;
import com.example.loan_platform.Writer.LoanPaymentWriter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final LoanPaymentProcessor loanPaymentProcessor;
    private final LoanPaymentWriter loanPaymentWriter;
    private final EntityManagerFactory entityManagerFactory;

    public BatchConfig(JobRepository jobRepository,
                       LoanPaymentProcessor loanPaymentProcessor,
                       LoanPaymentWriter loanPaymentWriter,
                       EntityManagerFactory entityManagerFactory) {
        this.jobRepository = jobRepository;
        this.loanPaymentProcessor = loanPaymentProcessor;
        this.loanPaymentWriter = loanPaymentWriter;
        this.entityManagerFactory = entityManagerFactory;
    }

    // LoanApplications nesnelerini veritabanından JPA kullanarak sayfalı şekilde çeker.
    // Yalnızca kredi skoru ONAYLANMIŞ başvurular filtrelenir.
    @Bean
    @StepScope
    public JpaPagingItemReader<LoanApplications> reader() {
        JpaPagingItemReader<LoanApplications> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString(
                "SELECT la FROM LoanApplications la " +
                        "JOIN la.loanScoring ls " +
                        "WHERE ls.decision = :decision"
        );
        reader.setParameterValues(Collections.singletonMap("decision", Decision.APPROVED));
        reader.setPageSize(10); // Her sayfada 10 kayıt okunacak
        return reader;
    }

    @Bean
    public Step processLoanPaymentsStep(PlatformTransactionManager transactionManager,
                                        ItemReader<LoanApplications> reader,
                                        ItemProcessor<LoanApplications, List<LoanPayments>> loanPaymentProcessor,
                                        ItemWriter<List<LoanPayments>> loanPaymentWriter) {

        return new StepBuilder("processLoanPaymentsStep", jobRepository)
                .<LoanApplications, List<LoanPayments>>chunk(10, transactionManager)
                .reader(reader)
                .processor(loanPaymentProcessor) // Kredi başvurularını ödemelere dönüştür
                .writer(loanPaymentWriter) // Dönüştürülen ödemeleri veritabanına yaz
                .build();
    }

    // Tüm kredi başvurularını işleyerek ödeme planlarını oluşturan batch job.
    @Bean
    public Job loanPaymentJob(Step processLoanPaymentsStep) {
        return new JobBuilder("loanPaymentJob", jobRepository)
                .start(processLoanPaymentsStep)
                .build();
    }

}