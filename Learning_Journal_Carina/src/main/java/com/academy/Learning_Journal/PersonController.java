package com.academy.Learning_Journal;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PersonController {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private SessionRepository sessionRepository;

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
        return "redirect:/login";
    }

    private Optional<Person> getLoggedInPerson(String sessionId) {
        if (sessionId.isEmpty()) {
            return Optional.empty();
        } else {
            return sessionRepository.findById(UUID.fromString(sessionId)).map(Session::getPerson);      //alternative schreibweise: .map(session -> session.getPerson());
                                                                                                        //map() gibt Optional.empty() zurück, anstatt Exception zu werfen, deswegen kein get()
        }
    }

    @GetMapping("/profile")
    public String showProfile(
            @CookieValue(value="session_id", defaultValue = "")     String cookieSessionId,
            Model model
    ) {
        Optional<Person> p = getLoggedInPerson(cookieSessionId);
        if (p.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("person", p.get());
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@CookieValue(value = "session_id", defaultValue = "") String cookieSessionId, Model model) {
        Optional<Person> p = getLoggedInPerson(cookieSessionId);
        if (p.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("person", p.get());
        model.addAttribute("editMode", true);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@CookieValue(value = "session_id", defaultValue = "") String cookieSessionId,
                                @RequestParam String vorname,
                                @RequestParam String nachname,
                                @RequestParam String email,
                                @RequestParam String username) {
        Optional<Person> personOpt = getLoggedInPerson(cookieSessionId);
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }

        Person person = personOpt.get();
        person.setVorname(vorname);
        person.setNachname(nachname);
        person.setEmail(email);
        person.setUsername(username);
        personRepository.save(person);

        return "redirect:/profile";
    }

}
