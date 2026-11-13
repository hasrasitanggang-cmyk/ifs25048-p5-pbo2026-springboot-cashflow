package org.delcom.ifs25048springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;
import javax.sql.DataSource;

import org.delcom.ifs25048springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25048springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class JdbcTransactionRepositoryTest {

    private DataSource createDataSource() {
        return new DriverManagerDataSource(
            "jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
            "sa",
            ""
        );
    }

    @Test
    void parseTypeHandlesAllBranches() {

        // Menguji kondisi raw == null
        assertEquals(
            TransactionType.INCOME,
            JdbcTransactionRepository.parseType(null)
        );

        // Menguji kondisi raw tidak null
        assertEquals(
            TransactionType.INCOME,
            JdbcTransactionRepository.parseType("INCOME")
        );

        // Menguji konversi huruf kecil
        assertEquals(
            TransactionType.EXPENSE,
            JdbcTransactionRepository.parseType(" expense ")
        );

        // Menguji catch IllegalArgumentException
        assertEquals(
            TransactionType.INCOME,
            JdbcTransactionRepository.parseType("INVALID_TYPE")
        );
    }

    @Test
    void findByIdHandlesNullCreatedAt() throws Exception {

        DataSource ds = createDataSource();

        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {

            st.execute("""
                CREATE TABLE transactions (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    amount BIGINT NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    proof_path VARCHAR(500),
                    created_at TIMESTAMP NULL
                )
                """);
        }

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                INSERT INTO transactions
                (id, user_id, description, amount, type, proof_path, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {

            ps.setInt(1, 1);
            ps.setInt(2, 10);
            ps.setString(3, "Transaksi Pengujian");
            ps.setLong(4, 50000);
            ps.setString(5, "INCOME");
            ps.setString(6, null);
            ps.setNull(7, Types.TIMESTAMP);

            assertEquals(1, ps.executeUpdate());
        }

        JdbcTransactionRepository repository =
            new JdbcTransactionRepository(ds);

        Transaction result = repository.findById(1).orElseThrow();

        assertEquals(1, result.id());
        assertEquals(10, result.userId());
        assertEquals("Transaksi Pengujian", result.description());
        assertEquals(50000, result.amount());
        assertEquals(TransactionType.INCOME, result.type());
        assertNull(result.createdAt());
    }
}