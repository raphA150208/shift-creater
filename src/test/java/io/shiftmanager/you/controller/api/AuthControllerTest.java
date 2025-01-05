package io.shiftmanager.you.controller.api;

import io.shiftmanager.you.config.TestConfig;
import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * フォームベース認証のテストクラス
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        String encodedPassword = passwordEncoder.encode("Test@123");
        testUser.setPassword(encodedPassword);
        testUser.setActive(true);
        testUser.setAdmin(false);

        // UserDetailsServiceのモック設定
        when(userDetailsService.loadUserByUsername(any()))
                .thenReturn(org.springframework.security.core.userdetails.User
                        .withUsername(testUser.getEmail())
                        .password(encodedPassword)
                        .roles(testUser.isAdmin() ? "ADMIN" : "USER")
                        .build());

        when(userService.findByEmail(any())).thenReturn(testUser);
    }

    @Test
    void loginSuccess() throws Exception {
        mockMvc.perform(formLogin("/login")
                .userParameter("email")
                .user("email", testUser.getEmail())
                .password("Test@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/calendar"))
                .andExpect(authenticated().withUsername(testUser.getEmail()));
    }

    @Test
    void loginFailure() throws Exception {
        mockMvc.perform(formLogin("/login")
                .userParameter("email")
                .user("email", "wrong@example.com")
                .password("wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }
} 