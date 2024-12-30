package io.shiftmanager.you.controller;

import io.shiftmanager.you.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ログインページの表示
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        // すでにログインしている場合はリダイレクト
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return "redirect:/calendar"; // カレンダー画面へ
        }
        return "login"; // login.htmlを表示
    }

    // ログイン失敗時の処理
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMessage", "ユーザー名またはパスワードが間違っています");
        return "login";
    }

    // ログアウト処理
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            redirectAttributes.addFlashAttribute("message", "ログアウトしました");
        }
        return "redirect:/login";
    }

    // アクセス拒否時の処理
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403"; // 403.htmlを表示
    }
}