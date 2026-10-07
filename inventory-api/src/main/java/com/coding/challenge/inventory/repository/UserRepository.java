package com.coding.challenge.inventory.repository;

import com.coding.challenge.inventory.domain.User;
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
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUserId(String userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = ?", Integer.class, userId);
        return count != null && count > 0;
    }

    public Optional<User> findByUserId(String userId) {
        List<User> result = jdbcTemplate.query(
                "SELECT id, user_id, display_name, role, create_timestamp FROM users WHERE user_id = ?",
                new UserRowMapper(), userId);
        return result.stream().findFirst();
    }

    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT id, user_id, display_name, role, create_timestamp FROM users ORDER BY id",
                new UserRowMapper());
    }

    public Long insert(String userId, String displayName, String role, LocalDateTime createTimestamp) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO users (user_id, display_name, role, create_timestamp) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, userId);
                    ps.setString(2, displayName);
                    ps.setString(3, role);
                    ps.setString(4, createTimestamp.toString());
                    return ps;
                },
                keyHolder);
        return keyHolder.getKey().longValue();
    }

    public boolean update(Long id, String displayName) {
        int affected = jdbcTemplate.update(
                "UPDATE users SET display_name = ? WHERE id = ?", displayName, id);
        return affected > 0;
    }

    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        return affected > 0;
    }

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getLong("id"),
                    rs.getString("user_id"),
                    rs.getString("display_name"),
                    rs.getString("role"),
                    LocalDateTime.parse(rs.getString("create_timestamp")));
        }
    }
}