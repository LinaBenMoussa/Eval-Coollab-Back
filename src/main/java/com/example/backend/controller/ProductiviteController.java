package com.example.backend.controller;

import com.example.backend.entity.Productivite;
import com.example.backend.service.ProductiviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/productivite")
@CrossOrigin(origins = "*")
public class ProductiviteController {

    private final ProductiviteService productiviteService;

    @Autowired
    public ProductiviteController(ProductiviteService productiviteService) {
        this.productiviteService = productiviteService;
    }


    @GetMapping("/ranking/{managerId}")
    public ResponseEntity<List<Productivite>> getProductivityRanking(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null || endDate == null) {
            // Utiliser la période par défaut (mois en cours)
            List<Productivite> ranking = productiviteService.getProductivityRankingByManager(managerId);
            return ResponseEntity.ok(ranking);
        } else {
            // Utiliser la période spécifiée
            List<Productivite> ranking = productiviteService.getProductivityRankingByManager(managerId, startDate, endDate);
            return ResponseEntity.ok(ranking);
        }
    }
}