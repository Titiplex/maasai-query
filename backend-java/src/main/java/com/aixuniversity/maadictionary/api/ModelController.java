package com.aixuniversity.maadictionary.api;

import com.aixuniversity.maadictionary.dao.utils.DaoRegistry;
import com.aixuniversity.maadictionary.model.AbstractModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Démonstration : récupérer *n'importe quel* modèle via son DAO et son ID.
 * GET /api/v1/model/Vocabulary/42  → renvoie l'objet JSON correspondant.
 */
@RestController
@RequestMapping("/api/v1/model")
public class ModelController {

    @SuppressWarnings("unchecked")
    @GetMapping("/{type}/{id}")
    public AbstractModel get(@PathVariable String type, @PathVariable int id) throws SQLException {
        try {
            Class<?> clazz = Class.forName("com.aixuniversity.maadictionary.model." + type);
            return DaoRegistry.getDao((Class<? extends AbstractModel>) clazz).searchById(id);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown model " + type);
        }
    }
}