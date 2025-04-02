package com.academy.Learning_Journal;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name="persons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;

    private String  vorname;

    private String  nachname;

    private String  email;

    private String  username;

    private String  password;

    private String role;

    @OneToMany(mappedBy = "author")
    private List<Entry> entries;

    @OneToMany(mappedBy = "person")
    private Set<Session> sessions;

    @ManyToMany
    @JoinTable(name = "courses_enrolled",
    joinColumns = @JoinColumn(name = "person_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id"))
    Set<Course> courses;

}