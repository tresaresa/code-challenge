package com.coding.challenge.inventory.repository;

import com.coding.challenge.inventory.domain.Inventory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public InventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventories WHERE name = ?", Integer.class, name);
        return count != null && count > 0;
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventories WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public Optional<Inventory> findById(Long id) {
        List<Inventory> result = jdbcTemplate.query(
                "SELECT id, name, description, category_id, quantity, create_user, create_timestamp FROM inventories WHERE id = ?",
                new InventoryRowMapper(), id);
        return result.stream().findFirst();
    }

    public List<Inventory> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, description, category_id, quantity, create_user, create_timestamp FROM inventories ORDER BY id",
                new InventoryRowMapper());
    }

    public Long insert(String name, String description, Long categoryId, Long subCategoryId,
                       Integer quantity, String createUser, LocalDateTime createTimestamp) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO inventories (name, description, category_id, quantity, create_user, create_timestamp) VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setString(2, description);
                    ps.setLong(3, categoryId);
                    ps.setInt(4, quantity);
                    ps.setString(5, createUser);
                    ps.setString(6, createTimestamp.toString());
                    return ps;
                },
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean updateQuantity(Long id, Integer quantity) {
        int affected = jdbcTemplate.update(
                "UPDATE inventories SET quantity = ? WHERE id = ?", quantity, id);
        return affected > 0;
    }

    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM inventories WHERE id = ?", id);
        return affected > 0;
    }

    private static final class InventoryRowMapper implements RowMapper<Inventory> {
        @Override
        public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Inventory(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("category_id"),
                    rs.getInt("quantity"),
                    rs.getString("create_user"),
                    LocalDateTime.parse(rs.getString("create_timestamp")));
        }
    }
}