package com.academy.Learning_Journal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntryRepository entryRepository;

    @GetMapping("/addPerson")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/addPerson")
    public String addPerson(
            @RequestParam(name = "vorname", required = true)    String formFirstName,
            @RequestParam(name = "nachname", required = true)   String formLastName,
            @RequestParam(name = "email", required = true)      String formEmail,
            @RequestParam(name = "username", required = true)   String formUsername,
            @RequestParam(name = "password", required = true)   String formPassword
    ) {
        Person p = Person.builder()
                .vorname(formFirstName)
                .nachname(formLastName)
                .email(formEmail)
                .username(formUsername)
                .password(formPassword)
                .build();
        personRepository.save(p);
        return "redirect:/entries";
    }


}
