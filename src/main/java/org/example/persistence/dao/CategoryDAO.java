package org.example.persistence.dao;

import org.example.persistence.entity.Category;
import org.example.persistence.config.SQLiteConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Autores: [Seu Nome]
 * Descrição: Classe responsável pela inserção na tabela Category.
 */
public class CategoryDAO {

    public void insert(Category category) throws SQLException {
        String sql = "INSERT INTO Category (description) VALUES (?)";

        // try-with-resources garante o fechamento automático da Connection e PreparedStatement
        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getDescription());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Erro ao inserir categoria no banco de dados: " + e.getMessage(), e);
        }
    }
    public List<Category> findAll() throws SQLException {
        List<Category> categorias = new ArrayList<>();
        String sql = "SELECT id, description FROM Category ORDER BY id";

        // O try-with-resources fecha Connection, PreparedStatement e ResultSet automaticamente
        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Percorrendo o ResultSet
            while (rs.next()) {
                Category category = new Category();
                // Pegando os valores das colunas da linha atual
                category.setId(rs.getInt("id"));
                category.setDescription(rs.getString("description"));

                // Adicionando o objeto preenchido na lista
                categorias.add(category);
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao listar categorias: " + e.getMessage(), e);
        }

        return categorias;
    }
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM Category WHERE id = ?";
        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou algo, false se não
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar existência da categoria: " + e.getMessage(), e);
        }
    }
}