package com.example.issairline.controller;

import com.example.issairline.entity.Flight;
import com.example.issairline.service.AircraftService;
import com.example.issairline.service.FlightService;
import com.example.issairline.service.ScheduledStatusUpdater;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final AircraftService aircraftService;
    private final ScheduledStatusUpdater scheduledStatusUpdater;

    public FlightController(FlightService flightService,
                            AircraftService aircraftService,
                            ScheduledStatusUpdater scheduledStatusUpdater) {
        this.flightService = flightService;
        this.aircraftService = aircraftService;
        this.scheduledStatusUpdater = scheduledStatusUpdater;
    }

    @GetMapping
    public String listFlights(Model model,
                              @ModelAttribute(value = "successMessage", binding = false) String successMessage,
                              @ModelAttribute(value = "errorMessage", binding = false) String errorMessage) {

        model.addAttribute("flights", flightService.findAll());

        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }
        if (errorMessage != null && !errorMessage.isBlank()) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "flight_list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        return "flight_form";
    }

    @PostMapping
    public String saveFlight(@ModelAttribute Flight flight, RedirectAttributes ra) {
        try {
            flightService.save(flight);
            ra.addFlashAttribute("successMessage", "Рейс успешно сохранён!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/flights";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return flightService.findById(id)
                .map(flight -> {
                    model.addAttribute("flight", flight);
                    model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
                    return "flight_form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Рейс не найден!");
                    return "redirect:/flights";
                });
    }

    @GetMapping("/delete/{id}")
    public String deleteFlight(@PathVariable Long id, RedirectAttributes ra) {
        try {
            flightService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Рейс успешно удалён!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/flights";
    }

    @GetMapping("/update-statuses")
    public String updateStatuses(RedirectAttributes ra) {
        try {
            scheduledStatusUpdater.runUpdateCycle();
            ra.addFlashAttribute("successMessage", "Статусы рейсов успешно обновлены!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Ошибка при обновлении статусов: " + e.getMessage());
        }
        return "redirect:/flights";
    }
}
