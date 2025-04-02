package com.academy.Learning_Journal;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
public class EntryController {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Transactional
    public Optional<Person> getLoggedInPerson(String sessionId) {
        if (sessionId.isEmpty() || sessionId == null) {
            return Optional.empty();
        } else {
            return sessionRepository.findById(UUID.fromString(sessionId)).map(Session::getPerson);      //alternative schreibweise: .map(session -> session.getPerson());
        }
    }

    @Transactional
    @GetMapping("/entries")
    public String showEntries(@CookieValue(value = "session_id", defaultValue = "") String cookieSessionId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<Person> p = getLoggedInPerson(cookieSessionId);
        if (p.isEmpty()) {
            redirectAttributes.addFlashAttribute("error","Du bist nicht eingeloggt.");
            return "redirect:/login";
        }
        Person person = p.get();
        Hibernate.initialize(person.getEntries());
        model.addAttribute("entries", person.getEntries().reversed());

        return "entries";
    }

    @Transactional
    @PostMapping("/entries/save")
    public String saveEntry(
            @RequestParam(name = "title", required = true)      String formTitle,
            @RequestParam(name = "content", required = true)    String formContent,
            @CookieValue(value = "session_id", defaultValue = "")      String cookieSessionId
    ) {
        Session s = sessionRepository.findById(UUID.fromString(cookieSessionId)).get();
        Entry e = Entry.builder().title(formTitle).content(formContent).creationTime(Instant.now()).author(s.getPerson()).build();
        entryRepository.save(e);
        personRepository.findByUsername(s.getPerson().getUsername()).get().getEntries().add(e);
        return "redirect:/entries";
    }

    @PostMapping("/entries/delete")
    public String deleteEntry(
            @RequestParam(name = "id", required = true) Long formId
    ) {
        if (formId != null) {
            entryRepository.deleteById(formId);
        }
        return "redirect:/entries";
    }

    @GetMapping("/entries/edit")
    public String editEntry(@RequestParam(value="id", required = true)  Long entryId,
                                                                        Model model
    ) {
        Optional<Entry> e = entryRepository.findById(entryId);
        if (e.isEmpty()) {
            return "redirect:/entries";
        }
        model.addAttribute("entry", e.get());
        model.addAttribute("editMode", true);
        return "entries";
    }

    @PostMapping("/entries/update")
    public String updateEntry(@RequestParam Long id,
                              @RequestParam String title,
                              @RequestParam String content) {
        Optional<Entry> e = entryRepository.findById(id);
        if (e.isEmpty()) {
            return "redirect:/entries";
        }

        Entry entry = e.get();
        entry.setTitle(title);
        entry.setContent(content);
        entryRepository.save(entry);

        return "redirect:/entries";
    }




}
