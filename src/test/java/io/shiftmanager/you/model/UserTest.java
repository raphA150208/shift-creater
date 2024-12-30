package io.shiftmanager.you.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        user = new User();
        now = LocalDateTime.now();
    }

    @Test
    void testUserProperties() {
        // 基本データの設定
        user.setUserId(1L);
        user.setUsername("test");
        user.setPassword("pass123");
        user.setEmail("test@example.com");
        user.setIsActive(true);
        user.setIsAdmin(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // 検証
        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getPassword()).isEqualTo("pass123");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.isActive()).isTrue();
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testActiveFlag() {
        assertThat(user.isActive()).isFalse();

        user.setIsActive(true);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void testAdminFlag() {
        assertThat(user.isAdmin()).isFalse();

        user.setIsAdmin(true);
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void testEquality() {
        // 同じ値を持つ2つのユーザー
        User user1 = createUser(1L, "test");
        User user2 = createUser(1L, "test");

        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isEqualTo(user1);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testToString() {
        user.setUserId(1L);
        user.setUsername("test");
        user.setEmail("test@example.com");

        String result = user.toString();

        assertThat(result)
                .contains("userId=1")
                .contains("username=test")
                .contains("email=test@example.com");
    }

    @Test
    void testDates() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = created.plusHours(1);

        user.setCreatedAt(created);
        user.setUpdatedAt(updated);

        assertThat(user.getUpdatedAt()).isAfter(user.getCreatedAt());
    }

    // テストヘルパーメソッド
    private User createUser(Long id, String name) {
        User testUser = new User();
        testUser.setUserId(id);
        testUser.setUsername(name);
        testUser.setEmail(name + "@example.com");
        return testUser;
    }
}