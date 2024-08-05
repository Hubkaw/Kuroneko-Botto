package com.kuroneko.controller;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class KuronekoMainController implements ErrorController {

    private JDA JDA;

    @GetMapping("/")
    public String getKuronekoMainPage(Model model){
        model.addAttribute("kuronekoServerAmount", JDA.getGuilds().size());
        model.addAttribute("kuronekoInvite", JDA.getInviteUrl(Permission.ADMINISTRATOR));
        return "main";
    }

    @RequestMapping("/error")
    public String getKuronekoErrorPage(Model model){
        return "redirect:/";
    }

}
