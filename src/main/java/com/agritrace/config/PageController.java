package com.agritrace.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class PageController {

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/consumer")
    public String consumer() {
        return "consumer";
    }

    @GetMapping("/farmer")
    public String farmer() {
        return "farmer";
    }

    @GetMapping("/distributor")
    public String distributor() {
        return "distributor";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
