package com.academy.Learning_Journal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
public class SessionController {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(name = "username", required = true)   String loginUsername,
            @RequestParam(name = "password", required = true)   String loginPassword,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Person> p = personRepository.findByUsername(loginUsername);

        if (p.isPresent() && loginPassword.equals(p.get().getPassword())) {
            Session session = new Session();
            session.setPerson(p.get());
            session = sessionRepository.save(session);
            response.addCookie(new Cookie("session_id", session.getId().toString()));
        } else {
            // Dem User wird mitgeteilt, wo der Fehler liegt
            if (p.isEmpty() && loginPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("error","Du bist nicht eingeloggt.");
            } else if (!p.isPresent()) {
                redirectAttributes.addFlashAttribute("error","Falscher Benutzername.");
            } else if (!loginPassword.equals(p.get().getPassword())){
                redirectAttributes.addFlashAttribute("error","Falsches Passwort.");
            }
            return "redirect:/login";
        }
        return "redirect:/entries";
    }

    @PostMapping("/logout")
    public String logout(
            @CookieValue(value = "session_id", defaultValue = "Anonymous") String cookieSessionId,
            HttpServletResponse response
    ) {
        sessionRepository.deleteById(UUID.fromString(cookieSessionId));

        Cookie c = new Cookie("session_id", "");
        c.setMaxAge(0);
        response.addCookie(c);

        return "redirect:/login";
    }
}
