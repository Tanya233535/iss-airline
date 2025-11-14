package com.example.issairline.controller;

import com.example.issairline.entity.Passenger;
import com.example.issairline.entity.Flight;
import com.example.issairline.service.PassengerService;
import com.example.issairline.repository.FlightRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/passengers")
@RequiredArgsConstructor
@Slf4j
public class PassengerController {

    private final PassengerService passengerService;
    private final FlightRepository flightRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("passengers", passengerService.findAll());
        return "passenger_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("passenger", new Passenger());
        model.addAttribute("flights", flightRepository.findAll());
        return "passenger_form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Passenger p = passengerService.findById(id);
            model.addAttribute("passenger", p);
            model.addAttribute("flights", flightRepository.findAll());
            return "passenger_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пассажир не найден");
            return "redirect:/passengers";
        }
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("passenger") Passenger passenger,
                       BindingResult result,
                       @RequestParam(value = "flight", required = false) Long flightId,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("flights", flightRepository.findAll());
            model.addAttribute("errorMessage", "Проверьте правильность заполнения полей!");
            return "passenger_form";
        }

        try {
            if (flightId != null) {
                Flight f = flightRepository.findById(flightId)
                        .orElseThrow(() -> new RuntimeException("Рейс не найден"));
                passenger.setFlight(f);
            } else {
                passenger.setFlight(null);
            }

            passengerService.save(passenger);
            redirectAttributes.addFlashAttribute("successMessage", "Пассажир успешно сохранён!");
            return "redirect:/passengers";

        } catch (Exception e) {
            model.addAttribute("flights", flightRepository.findAll());
            model.addAttribute("errorMessage", "Ошибка: " + e.getMessage());
            return "passenger_form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            passengerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Пассажир удалён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/passengers";
    }
}
