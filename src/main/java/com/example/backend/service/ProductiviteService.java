package com.example.backend.service;

import com.example.backend.entity.Productivite;
import com.example.backend.repository.ProductiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class ProductiviteService {

    private final ProductiviteRepository productiviteRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductiviteService(ProductiviteRepository productiviteRepository, JdbcTemplate jdbcTemplate) {
        this.productiviteRepository = productiviteRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    // Nouvelle méthode utilisant la procédure stockée
    public List<Productivite> getProductivityRankingByManager(Long managerId, LocalDate startDate, LocalDate endDate) {
        String sql = "{call GetProductivityRankingByManagerDirect(?, ?, ?)}";

        List<Productivite> result = jdbcTemplate.query(
                connection -> {
                    CallableStatement callableStatement = connection.prepareCall(sql);
                    callableStatement.setLong(1, managerId);
                    callableStatement.setDate(2, java.sql.Date.valueOf(startDate));
                    callableStatement.setDate(3, java.sql.Date.valueOf(endDate));
                    return callableStatement;
                },
                new RowMapper<Productivite>() {
                    @Override
                    public Productivite mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Productivite productivite = new Productivite();
                        productivite.setCollaborateurId(rs.getLong("collaborateur_id"));
                        productivite.setNom(rs.getString("nom"));
                        productivite.setPrenom(rs.getString("prenom"));
                        productivite.setScore(rs.getFloat("score"));
                        productivite.setRang(rs.getInt("rang"));
                        productivite.setPeriode(rs.getString("periode"));
                        return productivite;
                    }
                }
        );

        return result;
    }

    // Surcharge de la méthode pour utiliser la période actuelle par défaut (mois en cours)
    public List<Productivite> getProductivityRankingByManager(Long managerId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1); // Premier jour du mois en cours
        LocalDate endDate = today; // Aujourd'hui

        return getProductivityRankingByManager(managerId, startDate, endDate);
    }
}