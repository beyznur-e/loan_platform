package com.example.loan_platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTriggerJob() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/batch/run-loan-job"))
                .andExpect(status().isOk());
    }
}