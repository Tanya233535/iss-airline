package com.example.issairline.controller;

import com.example.issairline.entity.CrewMember;
import com.example.issairline.entity.Flight;
import com.example.issairline.service.CrewMemberService;
import com.example.issairline.repository.FlightRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/crew")
@RequiredArgsConstructor
@Slf4j
public class CrewMemberController {

    private final CrewMemberService crewMemberService;
    private final FlightRepository flightRepository;


    @GetMapping
    public String list(Model model) {
        model.addAttribute("crewMembers", crewMemberService.findAll());
        return "crew_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("crewMember", new CrewMember());
        model.addAttribute("flights", flightRepository.findAll());
        return "crew_form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return crewMemberService.findById(id)
                .map(member -> {
                    model.addAttribute("crewMember", member);
                    model.addAttribute("flights", flightRepository.findAll());
                    return "crew_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Член экипажа не найден!");
                    return "redirect:/crew";
                });
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("crewMember") CrewMember crewMember,
                       BindingResult result,
                       @RequestParam(value = "flight", required = false) Long flightId,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("flights", flightRepository.findAll());
            model.addAttribute("errorMessage", "Проверьте правильность заполнения полей!");
            return "crew_form";
        }

        try {
            if (flightId != null) {
                Flight flight = flightRepository.findById(flightId)
                        .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));
                crewMember.setFlight(flight);
            } else {
                crewMember.setFlight(null);
            }

            crewMemberService.save(crewMember);
            redirectAttributes.addFlashAttribute("successMessage", "Член экипажа успешно сохранён!");
            return "redirect:/crew";

        } catch (EntityExistsException e) {
            log.warn("Ошибка при сохранении: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("flights", flightRepository.findAll());
            return "crew_form";

        } catch (EntityNotFoundException e) {
            log.warn("Ошибка при сохранении: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/crew";

        } catch (Exception e) {
            log.error("Ошибка при сохранении члена экипажа", e);
            model.addAttribute("flights", flightRepository.findAll());
            model.addAttribute("errorMessage", "Не удалось сохранить члена экипажа!");
            return "crew_form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            crewMemberService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Член экипажа успешно удалён!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при удалении члена экипажа", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось удалить члена экипажа!");
        }
        return "redirect:/crew";
    }
}
