package org.example.service;

import org.example.persistence.dao.CategoryDAO;
import org.example.persistence.entity.Category;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Autores: [Alisson Ricady e Gustavo Moreira]
 * Descrição: Regras de negócio e validação para a entidade Category.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public String cadastrarNovaCategoria(String description) {
        // Validação: Campo obrigatório, não nulo e não vazio
        if (description == null || description.trim().isEmpty()) {
            return "Erro: A descrição da categoria é obrigatória e não pode estar vazia.";
        }

        // Validação: Tamanho mínimo e máximo (conforme ER VARCHAR(45))
        if (description.trim().length() < 3 || description.trim().length() > 45) {
            return "Erro: A descrição deve ter entre 3 e 45 caracteres.";
        }

        Category newCategory = new Category(description.trim());


        try {

            categoryDAO.insert(newCategory); //error explode aqui

            return "Sucesso: Categoria '" + description + "' inserida com sucesso!";
        } catch (SQLException e) {
            return "Falha ao inserir categoria: " + e.getMessage();
        }
    }
    public List<Category> listarTodas() {
        try {
            return categoryDAO.findAll();
        } catch (SQLException e) {
            // Em caso de erro no banco, informamos o problema e retornamos uma lista vazia
            System.err.println("Erro interno ao buscar as categorias: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}