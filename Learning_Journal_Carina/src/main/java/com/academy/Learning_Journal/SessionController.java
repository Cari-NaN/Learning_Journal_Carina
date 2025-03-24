package com.academy.Learning_Journal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            HttpServletResponse response
    ) {
        Person p = personRepository.findByUsername(loginUsername);

        if (loginPassword.equals(p.getPassword())) {
            Session session = new Session();
            session.setPerson(p);
            session = sessionRepository.save(session);
            response.addCookie(new Cookie("session_id", session.getId().toString()));
        } else {
            System.out.println("Ungültiger Nutzer");
            System.out.println(1/0);
            return "login";
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

        return "redirect:/";
    }
}
