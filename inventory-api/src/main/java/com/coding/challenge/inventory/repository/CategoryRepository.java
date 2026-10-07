package com.coding.challenge.inventory.repository;

import com.coding.challenge.inventory.domain.Category;
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
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE name = ?", Integer.class, name);
        return count != null && count > 0;
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public Optional<Category> findById(Long id) {
        List<Category> result = jdbcTemplate.query(
                "SELECT id, name, super_category_id, create_user, create_timestamp FROM categories WHERE id = ?",
                new CategoryRowMapper(), id);
        return result.stream().findFirst();
    }

    public List<Category> findAllRoots() {
        return jdbcTemplate.query(
                "SELECT id, name, super_category_id, create_user, create_timestamp FROM categories WHERE super_category_id IS NULL ORDER BY id",
                new CategoryRowMapper());
    }

    public List<Category> findSubsByParentId(Long superCategoryId) {
        return jdbcTemplate.query(
                "SELECT id, name, super_category_id, create_user, create_timestamp FROM categories WHERE super_category_id = ? ORDER BY id",
                new CategoryRowMapper(), superCategoryId);
    }

    public boolean update(Long id, String name) {
        int affected = jdbcTemplate.update(
                "UPDATE categories SET name = ? WHERE id = ?", name, id);
        return affected > 0;
    }

    public boolean hasChildren(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE super_category_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean hasInventoriesAsCategory(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventories WHERE category_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean hasInventoriesAsSubCategory(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventories WHERE sub_category_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
        return affected > 0;
    }

    public Long insert(String name, Long superCategoryId, String createUser, LocalDateTime createTimestamp) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO categories (name, super_category_id, create_user, create_timestamp) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setObject(2, superCategoryId);
                    ps.setString(3, createUser);
                    ps.setString(4, createTimestamp.toString());
                    return ps;
                },
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    private static final class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            long superCategoryIdValue = rs.getLong("super_category_id");
            Long superCategoryId = rs.wasNull() ? null : superCategoryIdValue;
            return new Category(
                    rs.getLong("id"),
                    rs.getString("name"),
                    superCategoryId,
                    rs.getString("create_user"),
                    LocalDateTime.parse(rs.getString("create_timestamp")));
        }
    }
}