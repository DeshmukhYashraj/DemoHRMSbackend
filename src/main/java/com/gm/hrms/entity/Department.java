package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(nullable = false)
    private Boolean status; // true = Active, false = Inactive

    // ── Sub-departments ───────────────────────────────────────────────────────
    @OneToMany(
            mappedBy    = "department",
            cascade     = CascadeType.ALL,
            orphanRemoval = true,
            fetch       = FetchType.LAZY
    )
    @Builder.Default
    private List<SubDepartment> subDepartments = new ArrayList<>();
}