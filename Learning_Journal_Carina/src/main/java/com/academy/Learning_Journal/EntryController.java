package com.academy.Learning_Journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Optional;


@Controller
public class EntryController {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntryRepository entryRepository;

    @GetMapping("/entries")
    public String showEntryForm(Model model) {
        model.addAttribute("entries", entryRepository.findAll());
        return "entries";
    }

    @PostMapping("/saveEntry")
    public String saveEntry(
            @RequestParam(name = "title", required = true)      String formTitle,
            @RequestParam(name = "content", required = true)    String formContent
    ) {
        Entry e = Entry.builder().title(formTitle).content(formContent).creationTime(Instant.now()).build();
        entryRepository.save(e);
        return "redirect:/entries";
    }

    @PostMapping("/deleteEntry")
    public String deleteEntry(
            @RequestParam(name = "id", required = true) Long formId
    ) {
        Entry entry = entryRepository.getById(formId);
        entryRepository.delete(entry);

        return "redirect:/entries";
    }

    @PostMapping("/updateEntry")
    public String updateEntry(
            @RequestParam(name = "id", required = true)         Long entryId,
            @RequestParam(name = "title", required = true)      String entryTitle,
            @RequestParam(name = "content", required = true)    String entryContent
    ) {
        Entry entry = entryRepository.getById(entryId);
        entry.setTitle(entryTitle);
        entry.setContent(entryContent);

        return "redirect:/entries";
    }



}
