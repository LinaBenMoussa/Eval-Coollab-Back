package com.example.backend.service;

import com.example.backend.entity.Productivite;
import com.example.backend.repository.ProductiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductiviteService {

    private final ProductiviteRepository productiviteRepository;

    @Autowired
    public ProductiviteService(ProductiviteRepository employeProductivitéRepository) {
        this.productiviteRepository = employeProductivitéRepository;
    }

    public List<Productivite> getTop5Collaborateurs() {
        return productiviteRepository.findTop5ByOrderByRangAsc();
    }
}
