package com.project.professor.allocation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.*;

@Entity
@Table(name = "allocation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;

    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

}
