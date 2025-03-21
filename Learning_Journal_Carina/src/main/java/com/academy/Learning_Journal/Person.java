package com.academy.Learning_Journal;

import jakarta.persistence.*;
import lombok.*;

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

//    @OneToMany(mappedBy = "author")
//    private Set<Entry> entries;
}
