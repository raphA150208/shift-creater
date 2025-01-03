package io.shiftmanager.you.controller;

import io.shiftmanager.you.model.User;
import io.shiftmanager.you.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "account-create";
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "account-create";
        }

        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "アカウントが正常に作成されました");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "アカウントの作成に失敗しました");
            return "redirect:/account/create";
        }
    }
} 