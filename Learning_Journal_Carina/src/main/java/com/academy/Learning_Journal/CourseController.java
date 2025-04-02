package com.academy.Learning_Journal;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping("/courses")
    public String showCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "courses";
    }

    @PostMapping("/courses/create")
    public String createCourse(@RequestParam String name) {
        Course course = Course.builder().name(name).build();
        courseRepository.save(course);
        return "redirect:/courses";
    }

    @Transactional
    @PostMapping("/courses/enroll")
    public String enrollCourse(
            @RequestParam                                          Long courseId,
            @CookieValue(value = "session_id", required = false)   String sessionId,
                                                                   RedirectAttributes redirectAttributes
    ) {
        Optional<Person> personOpt = getLoggedInPerson(sessionId);
        if (personOpt.isEmpty()) {
            return "redirect:/login";
        }
            Person person = personOpt.get();
            Course course = courseRepository.findById(courseId).orElseThrow();

            if (person.getCourses().contains(course)) {
                redirectAttributes.addFlashAttribute("error", "Du bist bereits in diesem Kurs eingeschrieben.");
                return "redirect:/courses";
            }
            person.getCourses().add(course);
            personRepository.save(person);
            course.getEnrolledPerson().add(person);
            courseRepository.save(course);

            redirectAttributes.addFlashAttribute("success", "Du hast dich erfolgreich in den Kurs eingeschrieben!");
            return "redirect:/profile";

    }

    @Transactional
    public Optional<Person> getLoggedInPerson(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Optional.empty();
        }
        return sessionRepository.findById(UUID.fromString(sessionId)).map(Session::getPerson);
    }

}
