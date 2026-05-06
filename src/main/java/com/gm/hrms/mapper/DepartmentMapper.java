package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.request.SubDepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.SubDepartmentResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.SubDepartment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DepartmentMapper {

    // ── Department ────────────────────────────────────────────────────────────

    public static Department toEntity(DepartmentRequestDTO dto) {
        Department dept = Department.builder()
                .name(dto.getName().trim())
                .code(dto.getCode().trim())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();

        if (dto.getSubDepartments() != null) {
            List<SubDepartment> subs = dto.getSubDepartments().stream()
                    .map(s -> toSubEntity(s, dept))
                    .collect(Collectors.toList());
            dept.getSubDepartments().addAll(subs);
        }

        return dept;
    }

    /**
     * Applies PATCH-style partial updates to an existing Department.
     * Replaces sub-departments list entirely (orphanRemoval handles deletes).
     */
    public static void patchUpdate(Department dept, DepartmentRequestDTO dto) {
        if (dto.getName()        != null) dept.setName(dto.getName().trim());
        if (dto.getCode()        != null) dept.setCode(dto.getCode().trim());
        if (dto.getDescription() != null) dept.setDescription(dto.getDescription());
        if (dto.getStatus()      != null) dept.setStatus(dto.getStatus());

        if (dto.getSubDepartments() != null) {
            // Clear existing (orphanRemoval will delete them from DB)
            dept.getSubDepartments().clear();

            List<SubDepartment> updated = dto.getSubDepartments().stream()
                    .map(s -> toSubEntity(s, dept))
                    .collect(Collectors.toList());

            dept.getSubDepartments().addAll(updated);
        }
    }

    public static DepartmentResponseDTO toResponse(Department dept) {
        List<SubDepartmentResponseDTO> subDtos =
                dept.getSubDepartments() == null
                        ? Collections.emptyList()
                        : dept.getSubDepartments().stream()
                        .map(DepartmentMapper::toSubResponse)
                        .collect(Collectors.toList());

        return DepartmentResponseDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .code(dept.getCode())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .subDepartments(subDtos)
                .build();
    }

    // ── SubDepartment ─────────────────────────────────────────────────────────

    public static SubDepartment toSubEntity(SubDepartmentRequestDTO dto, Department parent) {
        SubDepartment sub = SubDepartment.builder()
                .name(dto.getName().trim())
                .code(dto.getCode().trim())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .department(parent)
                .build();

        // Preserve id for updates so JPA can merge rather than insert
        if (dto.getId() != null) sub.setId(dto.getId());

        return sub;
    }

    public static SubDepartmentResponseDTO toSubResponse(SubDepartment sub) {
        return SubDepartmentResponseDTO.builder()
                .id(sub.getId())
                .name(sub.getName())
                .code(sub.getCode())
                .description(sub.getDescription())
                .status(sub.getStatus())
                .build();
    }
}