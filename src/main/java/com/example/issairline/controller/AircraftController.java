package com.example.issairline.controller;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.service.AircraftService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Веб-контроллер для работы с самолётами.
 * Использует шаблонизатор Thymeleaf для отображения страниц.
 */
@Controller
@RequestMapping("/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }


    @GetMapping
    public String listAircrafts(Model model) {
        List<Aircraft> aircrafts = aircraftService.getAllAircrafts();
        model.addAttribute("aircrafts", aircrafts);
        return "aircraft_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "aircraft_form";
    }


    @PostMapping
    public String saveAircraft(@ModelAttribute Aircraft aircraft, Model model) {
        try {
            aircraftService.saveAircraft(aircraft);
            return "redirect:/aircrafts";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("aircraft", aircraft);
            return "aircraft_form";
        }
    }

    @GetMapping("/edit/{aircraftCode}")
    public String showEditForm(@PathVariable String aircraftCode, Model model) {
        Aircraft aircraft = aircraftService.findById(aircraftCode)
                .orElseThrow(() -> new IllegalArgumentException("Самолёт не найден: " + aircraftCode));
        model.addAttribute("aircraft", aircraft);
        return "aircraft_form";
    }

    @GetMapping("/delete/{code}")
    public String deleteAircraft(@PathVariable String code) {
        aircraftService.deleteAircraft(code);
        return "redirect:/aircrafts";
    }
}

