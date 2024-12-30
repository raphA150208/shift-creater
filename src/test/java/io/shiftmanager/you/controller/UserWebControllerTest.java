package io.shiftmanager.you.controller;

import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserWebControllerTest.class)
public class UserWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void showRegistrationForm_Success() throws Exception {
        //登録フォームの表示テスト
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void register_Success() throws Exception {
        //正常な登録処理テスト
        when(userService.existsByUsername(any())).thenReturn(false);
        when(userService.existsByEmail(any())).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/register")
               .param("username", testUser.getUsername())
               .param("email", testUser.getEmail())
               .param("password", testUser.getPassword()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"))
               .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void register_DuplicateUsername() throws Exception {
        // ユーザー名重複時のテスト
        when(userService.existsByUsername("testUser")).thenReturn(true);

        mockMvc.perform(post("/register")
               .param("username", testUser.getUsername())
               .param("email", testUser.getEmail())
               .param("password", testUser.getPassword()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"))
               .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void register_ValidationError() throws Exception {
        //バリデーションエラーのテスト
        mockMvc.perform(post("/register")
               .param("username", "t") // 短すぎるユーザー名
               .param("email", "invalid-email") // 不正なメール
               .param("password", "short")) // 短すぎるパスワード
               .andExpect(status().isOk())
               .andExpect(view().name("register"))
               .andExpect(model().hasErrors());
    }
}
