package com.example.attendance.dao;

import com.example.attendance.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. 新增用户
    public int addUser(User user) {
        String sql = "INSERT INTO user(username, password, role, name) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getName());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    // 2. 根据ID查询用户
    public User getUserById(Integer userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setName(rs.getString("name"));
            user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return user;
        }, userId);
    }

    // 3. 查询所有用户
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            user.setName(rs.getString("name"));
            user.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return user;
        });
    }

    // 4. 更新用户
    public int updateUser(User user) {
        String sql = "UPDATE user SET username=?, password=?, role=?, name=? WHERE user_id=?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getName(),
                user.getUserId());
    }

    // 5. 删除用户
    public int deleteUser(Integer userId) {
        String sql = "DELETE FROM user WHERE user_id=?";
        return jdbcTemplate.update(sql, userId);
    }
}