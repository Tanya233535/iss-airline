package com.example.issairline.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error/403")
    public String error403() {
        return "errors/403";
    }

    @RequestMapping("/error/404")
    public String error404() {
        return "errors/404";
    }

    @RequestMapping("/error/500")
    public String error500() {
        return "errors/500";
    }
}
