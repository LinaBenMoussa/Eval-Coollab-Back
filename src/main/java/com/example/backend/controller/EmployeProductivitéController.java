package com.example.backend.controller;

import com.example.backend.entity.Productivite;
import com.example.backend.service.ProductiviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/productivite")
public class EmployeProductivitéController {

    @Autowired
    private ProductiviteService productiviteService;

    @GetMapping("/top5-collaborateurs")
    public List<Productivite> getTop5Collaborateurs() {
        return productiviteService.getTop5Collaborateurs();
    }
}
