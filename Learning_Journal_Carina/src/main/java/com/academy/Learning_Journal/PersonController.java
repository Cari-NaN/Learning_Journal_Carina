package com.academy.Learning_Journal;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private CourseRepository courseRepository;

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
                .role("USER")
                .build();
        personRepository.save(p);

        return "redirect:/login";
    }

    @Transactional
    public Optional<Person> getLoggedInPerson(String sessionId) {
        if (sessionId.isEmpty() || sessionId == null) {
            return Optional.empty();
        } else {
            return sessionRepository.findById(UUID.fromString(sessionId)).map(Session::getPerson);      //alternative schreibweise: .map(session -> session.getPerson());
        }
    }

    @Transactional
    @GetMapping("/profile")
    public String showProfile(
            @CookieValue(value="session_id", defaultValue = "")     String cookieSessionId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Person> p = getLoggedInPerson(cookieSessionId);
        if (p.isEmpty()) {
            redirectAttributes.addFlashAttribute("error","Du bist nicht eingeloggt.");

            return "redirect:/login";
        }
//        Hibernate.initialize(courseRepository.findAll());
        model.addAttribute("person", p.get());
        model.addAttribute("courses", courseRepository.findAll());
//        model.addAttribute("allCourses", courseRepository.findAll());
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
