package com.example.issairline.controller;

import com.example.issairline.entity.Flight;
import com.example.issairline.service.FlightService;
import com.example.issairline.service.AircraftService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

/**
 * Контроллер для управления рейсами
 */
@Controller
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final AircraftService aircraftService;

    public FlightController(FlightService flightService, AircraftService aircraftService) {
        this.flightService = flightService;
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public String listFlights(Model model) {
        model.addAttribute("flights", flightService.findAll());
        return "flight_list";
    }

    @GetMapping("/new")
    public String newFlight(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        return "flight_form";
    }

    @PostMapping
    public String saveFlight(@ModelAttribute Flight flight, Model model) {
        try {
            flightService.save(flight);
            return "redirect:/flights";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
            model.addAttribute("flight", flight);
            return "flight_form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editFlight(@PathVariable Long id, Model model) {
        Flight flight = flightService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейс не найден: " + id));
        model.addAttribute("flight", flight);
        model.addAttribute("aircrafts", aircraftService.getAllAircrafts());
        return "flight_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteFlight(@PathVariable Long id) {
        flightService.deleteById(id);
        return "redirect:/flights";
    }
}

