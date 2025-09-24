package com.example.VidobuProje.controller;

import com.example.VidobuProje.entity.Department;
import com.example.VidobuProje.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        return department.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new department", 
               description = "Create a new department with name and description")
    @ApiResponse(responseCode = "201", description = "Department created successfully",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Department.class),
                 examples = @ExampleObject(value = """
                 {
                   "id": 1,
                   "name": "IT Department",
                   "description": "Information Technology Department"
                 }""")))
    @ApiResponse(responseCode = "400", description = "Department name already exists")
    public ResponseEntity<Department> createDepartment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Department details",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Department.class),
                    examples = @ExampleObject(value = """
                    {
                      "name": "IT Department",
                      "description": "Information Technology Department"
                    }""")
                )
            )
            @RequestBody Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Department savedDepartment = departmentRepository.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDepartment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        if (existingDepartment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if (!existingDepartment.get().getName().equals(department.getName()) &&
            departmentRepository.existsByName(department.getName())) {
            return ResponseEntity.badRequest().build();
        }
        
        department.setId(id);
        Department updatedDepartment = departmentRepository.save(department);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
