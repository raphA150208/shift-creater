package io.shiftmanager.you.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 認証APIのエンドポイントをテストするクラス
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Test@123");
        testUser.setActive(true);
        testUser.setAdmin(false);
    }

    // 正常なログイン処理と成功時のレスポンスをテスト
    @Test
    void loginSuccess() throws Exception {
        // 認証成功のモック設定
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), testUser.getPassword());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);

        // リクエストボディの作成
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", testUser.getEmail());
        loginRequest.put("password", testUser.getPassword());

        // リクエストの実行と検証
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.active").value(testUser.isActive()))
                .andExpect(jsonPath("$.admin").value(testUser.isAdmin()))
                .andExpect(jsonPath("$.message").value("ログインに成功しました"));
    }

    // 不正な認証情報でのログイン失敗時の処理をテスト
    @Test
    void loginFailure() throws Exception {
        // 認証失敗のモック設定
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // リクエストボディの作成
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "wrong@example.com");
        loginRequest.put("password", "wrongpassword");

        // リクエストの実行と検証
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("認証に失敗しました"))
                .andExpect(jsonPath("$.message").value("メールアドレスまたはパスワードが間違っています"));
    }
} 