package com.example.loan_platform.Config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    // EntityManagerFactory bean'i, JPA/Hibernate ile veri erişimini sağlar.
    // DataSource üzerinden bağlantıyı alır ve Entity sınıflarını tarar.
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        // DDL otomatik oluşturulmayacak (örneğin prod ortamda elle yönetilen veritabanı varsa)
        vendorAdapter.setGenerateDdl(false);

        // Hibernate SQL sorgularını konsola yazdır
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.loan_platform.Entity");
        factory.setDataSource(dataSource);
        return factory;
    }

    // Spring TransactionManager bean'i.
    // @Transactional anotasyonunun çalışmasını sağlar ve Spring Batch gibi yapılarla da entegre çalışır.
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}