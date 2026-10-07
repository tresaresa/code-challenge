package com.coding.challenge.inventory;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseInitializerTest {

    @Test
    void createsDatabaseAndTables() throws Exception {
        Path dbDir = Path.of("../db");
        Files.createDirectories(dbDir);

        String dbUrl = "jdbc:sqlite:../db/inventory.db";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            String schema = Files.readString(Path.of("src/main/resources/schema.sql"));
            for (String sql : schema.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("PRAGMA")) {
                    stmt.execute(trimmed);
                }
            }

            assertTrue(tableExists(stmt, "categories"));
            assertTrue(tableExists(stmt, "inventories"));
            assertTrue(tableExists(stmt, "users"));
        }

        assertTrue(Files.exists(Path.of("../db/inventory.db")));
    }

    private boolean tableExists(Statement stmt, String tableName) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "'")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
}