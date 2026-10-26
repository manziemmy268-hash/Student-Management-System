package com.university.web;

import com.university.service.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final DepartmentService departmentService;

    public PublicController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public List<WebDtos.DepartmentResponse> departments() {
        return departmentService.getAllDepartments().stream()
                .map(WebDtos::department)
                .collect(Collectors.toList());
    }
}
