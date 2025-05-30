package com.example.loan_platform.Controller;

import com.example.loan_platform.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private static final Logger log = LoggerFactory.getLogger(BatchJobController.class);

    private final JobLauncher jobLauncher;
    private final Job loanPaymentJob;
    private final DataSource dataSource;

    public BatchJobController(JobLauncher jobLauncher, Job loanPaymentJob, DataSource dataSource) {
        this.jobLauncher = jobLauncher;
        this.loanPaymentJob = loanPaymentJob;
        this.dataSource = dataSource;
    }

    @PostMapping("/run-loan-reader")
    public ResponseEntity<String> runLoanApplicationsReaderJob() {
        try {
            checkBatchSequences();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(loanPaymentJob, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                return ResponseEntity.ok("Batch işlemi başarıyla tamamlandı!");
            } else {
                log.warn("Batch job tamamlanamadı. Durum: {}", jobExecution.getStatus());
                throw new CustomException("Batch job başarısız oldu. Durum: " + jobExecution.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Batch job başlatılamadı", e);
            throw new CustomException("Batch job başlatılamadı: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkBatchSequences() {
        try (Connection connection = dataSource.getConnection()) {
            checkSequenceExists(connection, "batch_job_seq");
            checkSequenceExists(connection, "batch_job_execution_seq");
            checkSequenceExists(connection, "batch_step_execution_seq");
        } catch (SQLException e) {
            log.error("Sequence kontrolü sırasında hata oluştu", e);
            throw new CustomException("Sequence kontrolü başarısız: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkSequenceExists(Connection connection, String sequenceName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = ?")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Sequence bulunamadı: " + sequenceName);
                }
            }
        }
    }
}
