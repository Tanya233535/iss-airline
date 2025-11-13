package com.example.issairline.controller;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.service.AircraftService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public String list(Model model,
                       @ModelAttribute("successMessage") String successMessage,
                       @ModelAttribute("errorMessage") String errorMessage) {
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "aircraft_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "aircraft_form";
    }


    @GetMapping("/edit/{code}")
    public String editForm(@PathVariable("code") String code, Model model, RedirectAttributes ra) {
        var aircraft = aircraftService.getAircraftByCode(code);
        if (aircraft == null) {
            ra.addFlashAttribute("errorMessage", "Самолёт не найден!");
            return "redirect:/aircrafts";
        }
        model.addAttribute("aircraft", aircraft);
        return "aircraft_form";
    }


    @PostMapping
    public String save(@ModelAttribute Aircraft aircraft, RedirectAttributes ra) {
        try {
            aircraftService.saveAircraft(aircraft);
            ra.addFlashAttribute("successMessage", "Самолёт успешно сохранён!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Ошибка при сохранении: " + e.getMessage());
        }
        return "redirect:/aircrafts";
    }


    @GetMapping("/delete/{code}")
    public String delete(@PathVariable("code") String code, RedirectAttributes ra) {
        try {
            aircraftService.deleteAircraft(code);
            ra.addFlashAttribute("successMessage", "Самолёт успешно удалён!");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("errorMessage", "Самолёт не может быть удалён, пока на него есть рейсы!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/aircrafts";
    }
}
