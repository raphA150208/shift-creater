package io.shiftmanager.you.controller;

import io.shiftmanager.you.config.TestConfig;
import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ユーザー登録画面のコントローラーをテストするクラス
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class UserWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // 登録フォーム表示の正常系をテスト
    @Test
    public void showRegistrationForm_Success() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    // ユーザー登録の正常系をテスト
    @Test
    public void register_Success() throws Exception {
        when(userService.existsByEmail(any())).thenReturn(false);
        when(userService.createUser(any())).thenReturn(new User());

        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("email", "test@example.com")
                        .param("password", "Test@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // バリデーションエラー時の処理をテスト
    @Test
    public void register_ValidationError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("email", "invalid-email")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    // メールアドレス重複時の処理をテスト
    @Test
    public void register_DuplicateUsername() throws Exception {
        when(userService.existsByEmail(any())).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("username", "existinguser")
                        .param("email", "existing@example.com")
                        .param("password", "Test@123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));
    }
}
