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

    /**
     * Отображение списка всех самолётов.
     */
    @GetMapping
    public String listAircrafts(Model model) {
        List<Aircraft> aircrafts = aircraftService.getAllAircrafts();
        model.addAttribute("aircrafts", aircrafts);
        return "aircrafts/list";
    }

    /**
     * Форма добавления нового самолёта.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        return "aircrafts/form";
    }

    /**
     * Сохранение нового или изменённого самолёта.
     */
    @PostMapping
    public String saveAircraft(@ModelAttribute("aircraft") Aircraft aircraft) {
        aircraftService.saveAircraft(aircraft);
        return "redirect:/aircrafts";
    }

    /**
     * Удаление самолёта по коду.
     */
    @GetMapping("/delete/{code}")
    public String deleteAircraft(@PathVariable String code) {
        aircraftService.deleteAircraft(code);
        return "redirect:/aircrafts";
    }
}

