package com.example.issairline.controller;

import com.example.issairline.entity.User;
import com.example.issairline.repository.UserRepository;
import com.example.issairline.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String list(Model model,
                       @RequestParam(required = false) String successMessage,
                       @RequestParam(required = false) String errorMessage) {

        model.addAttribute("users", userService.findAll());
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "users_list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "user_form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id,
                           Model model,
                           RedirectAttributes attrs) {
        User user = userService.findById(id);
        if (user == null) {
            attrs.addFlashAttribute("errorMessage", "Пользователь не найден!");
            return "redirect:/users";
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", User.Role.values());
        return "user_form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute("user") User user,
                       BindingResult result,
                       @RequestParam(value = "encode", defaultValue = "true") boolean encodePassword,
                       RedirectAttributes attrs,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            return "user_form";
        }

        try {
            if (user.getId() == null && userService.existsUsername(user.getUsername())) {
                model.addAttribute("roles", User.Role.values());
                model.addAttribute("errorMessage", "Такой логин уже существует!");
                return "user_form";
            }

            userService.save(user, encodePassword);
            attrs.addFlashAttribute("successMessage", "Пользователь сохранён!");
            return "redirect:/users";

        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя", e);
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("errorMessage", "Ошибка: " + e.getMessage());
            return "user_form";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            userService.delete(id);
            attrs.addFlashAttribute("successMessage", "Пользователь удалён!");
        } catch (Exception e) {
            attrs.addFlashAttribute("errorMessage", "Ошибка при удалении!");
        }
        return "redirect:/users";
    }
}
