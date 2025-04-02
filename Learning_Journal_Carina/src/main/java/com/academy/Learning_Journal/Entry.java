package com.academy.Learning_Journal;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="entries")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant creationTime;

    @ManyToOne
    @JoinColumn(name="person_id", nullable = false)
    private Person author;

}
