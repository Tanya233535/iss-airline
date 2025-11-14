package com.example.issairline.controller;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Maintenance;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.repository.MaintenanceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/maintenance")
@RequiredArgsConstructor
@Slf4j
public class MaintenanceController {

    private final MaintenanceRepository maintenanceRepository;
    private final AircraftRepository aircraftRepository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "successMessage", required = false) String successMessage,
                       @RequestParam(value = "errorMessage", required = false) String errorMessage) {

        model.addAttribute("records", maintenanceRepository.findAll());
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);

        return "maintenance_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("maintenance", new Maintenance());
        model.addAttribute("aircrafts", aircraftRepository.findAll());
        model.addAttribute("statuses", Maintenance.Status.values());
        return "maintenance_form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes attrs) {

        Maintenance m = maintenanceRepository.findById(id).orElse(null);

        if (m == null) {
            attrs.addFlashAttribute("errorMessage", "Запись не найдена!");
            return "redirect:/maintenance";
        }

        model.addAttribute("maintenance", m);
        model.addAttribute("aircrafts", aircraftRepository.findAll());
        model.addAttribute("statuses", Maintenance.Status.values());

        return "maintenance_form";
    }


    @PostMapping
    public String save(@Valid @ModelAttribute("maintenance") Maintenance maintenance,
                       BindingResult result,
                       @RequestParam("aircraft") String aircraftCode,
                       RedirectAttributes attrs,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("aircrafts", aircraftRepository.findAll());
            model.addAttribute("statuses", Maintenance.Status.values());
            return "maintenance_form";
        }

        try {
            Aircraft aircraft = aircraftRepository.findById(aircraftCode)
                    .orElseThrow(() -> new RuntimeException("Самолёт не найден"));
            maintenance.setAircraft(aircraft);

            if (maintenance.getMaintenanceDate() != null &&
                    maintenance.getMaintenanceDate().isAfter(LocalDate.now()))
                throw new RuntimeException("Дата обслуживания не может быть в будущем");

            if (maintenance.getNextDueDate() != null &&
                    maintenance.getNextDueDate().isBefore(maintenance.getMaintenanceDate()))
                throw new RuntimeException("Следующая дата ТО не может быть раньше текущей");


            if (!maintenance.getType().matches("A-check|B-check|C-check|D-check"))
                throw new RuntimeException("Некорректный тип проверки (допустимы A/B/C/D-check)");

            if (maintenance.getMaintenanceDate() != null) {
                LocalDate date = maintenance.getMaintenanceDate();

                switch (maintenance.getType()) {
                    case "A-check":
                        maintenance.setNextDueDate(date.plusMonths(3));
                        break;
                    case "B-check":
                        maintenance.setNextDueDate(date.plusMonths(6));
                        break;
                    case "C-check":
                        maintenance.setNextDueDate(date.plusMonths(18));
                        break;
                    case "D-check":
                        maintenance.setNextDueDate(date.plusYears(8));
                        break;
                    default:
                        throw new RuntimeException("Неизвестный тип проверки: " + maintenance.getType());
                }
            }

            if (maintenance.getStatus() == Maintenance.Status.IN_PROGRESS) {
                aircraft.setStatus(Aircraft.Status.MAINTENANCE);
            }

            if (maintenance.getStatus() == Maintenance.Status.COMPLETED) {
                aircraft.setStatus(Aircraft.Status.ACTIVE);
            }

            maintenanceRepository.save(maintenance);

            attrs.addFlashAttribute("successMessage", "Запись обслуживания сохранена!");
            return "redirect:/maintenance";

        } catch (Exception e) {
            log.error("Ошибка при сохранении ТО", e);
            model.addAttribute("aircrafts", aircraftRepository.findAll());
            model.addAttribute("statuses", Maintenance.Status.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "maintenance_form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            maintenanceRepository.deleteById(id);
            attrs.addFlashAttribute("successMessage", "Запись удалена!");
        } catch (Exception e) {
            attrs.addFlashAttribute("errorMessage", "Ошибка удаления: " + e.getMessage());
        }
        return "redirect:/maintenance";
    }
}
