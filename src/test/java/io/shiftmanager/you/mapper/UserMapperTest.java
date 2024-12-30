package io.shiftmanager.you.mapper;

import io.shiftmanager.you.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // テストデータのクリーンアップ
        cleanupTestData();
    }

    private void cleanupTestData() {
        try {
            // 全てのユーザーを取得
            List<User> users = userMapper.getAllUsers();
            // 1件ずつ削除
            for (User user : users) {
                userMapper.delete(user.getUserId());
            }
        } catch (Exception e) {
            System.err.println("Failed to cleanup test data: " + e.getMessage());
        }
    }

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("testPassword"));
        user.setEmail(email);
        user.setIsActive(true);
        user.setIsAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    @Test
    @Transactional
    public void testInsertAndGetUser() {
        User user = createTestUser("testUser", "test@example.com");
        userMapper.insert(user);
        User retrievedUser = userMapper.getUserById(user.getUserId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("testUser");
        assertThat(retrievedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(passwordEncoder.matches("testPassword", retrievedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    public void testDuplicateUsername() {
        User user1 = createTestUser("sameUser", "test1@example.com");
        User user2 = createTestUser("sameUser", "test2@example.com");
        userMapper.insert(user1);

        assertThatThrownBy(() -> userMapper.insert(user2))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    public void testInvalidData() {
        User invalidUser = new User();
        // 必須フィールドを設定しない
        assertThatThrownBy(() -> userMapper.insert(invalidUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Transactional
    public void testGetAllUsers() {
        // 複数のテストユーザーを作成
        User user1 = createTestUser("user1", "user1@example.com");
        User user2 = createTestUser("user2", "user2@example.com");
        user2.setIsAdmin(true);

        // ユーザーの挿入
        userMapper.insert(user1);
        userMapper.insert(user2);

        // 全ユーザーの取得
        List<User> users = userMapper.getAllUsers();

        // 検証
        assertThat(users).isNotEmpty();
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);

        // 取得したユーザーの内容を検証
        User foundUser1 = users.stream()
                .filter(u -> u.getEmail().equals("user1@example.com"))
                .findFirst()
                .orElse(null);
        User foundUser2 = users.stream()
                .filter(u -> u.getEmail().equals("user2@example.com"))
                .findFirst()
                .orElse(null);

        assertThat(foundUser1).isNotNull();
        assertThat(foundUser2).isNotNull();
        assertThat(foundUser2.isAdmin()).isTrue();
    }

    @Test
    @Transactional
    public void testUpdateUser() {
        User user = createTestUser("originalUser", "original@example.com");
        userMapper.insert(user);

        user.setUsername("updatedUser");
        user.setEmail("updated@example.com");
        userMapper.update(user);

        User updatedUser = userMapper.getUserById(user.getUserId());
        assertThat(updatedUser.getUsername()).isEqualTo("updatedUser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        User user = createTestUser("deleteUser", "delete@example.com");
        userMapper.insert(user);

        userMapper.delete(user.getUserId());

        User deletedUser = userMapper.getUserById(user.getUserId());
        assertThat(deletedUser).isNull();
    }
}
