package io.shiftmanager.you.controller;

import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserWebController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "duplicate",
                    "このメールアドレスは既に使用されています");
            return "register";
        }

        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "アカウントが正常に作成されました");
            return "redirect:/login";
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.user",
                    "アカウントの作成に失敗しました");
            return "register";
        }
    }
}


