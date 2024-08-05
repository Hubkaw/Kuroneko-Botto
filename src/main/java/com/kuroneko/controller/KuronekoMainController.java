package com.kuroneko.controller;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class KuronekoMainController {

    private JDA JDA;

    @GetMapping("/")
    public String getKuronekoMainPage(Model model){
        model.addAttribute("kuronekoServerAmount", JDA.getGuilds().size());
        model.addAttribute("kuronekoInvite", JDA.getInviteUrl(Permission.ADMINISTRATOR));
        return "main";
    }
}
