package ru.otus.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.dto.ClientDto;
import ru.otus.services.ClientService;

@Controller
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/clients")
    public String listPage(Model model) {
        List<ClientDto> clients = clientService.findAll();
        model.addAttribute("clients", clients);
        return "clients";
    }

    @PostMapping("/clients")
    public String clientSave(@ModelAttribute ClientDto client) {
        clientService.save(client);
        return "redirect:/clients";
    }
}
