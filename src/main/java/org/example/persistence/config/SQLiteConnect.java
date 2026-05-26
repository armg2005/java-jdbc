package org.example.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Autores: [Alisson Ricady e Gustavo Moreira]
 * Descrição: Classe responsável por estabelecer a conexão com o banco SQLite.
 */
public class SQLiteConnect {

    // Caminho para o arquivo do banco de dados
    private static final String URL = "jdbc:sqlite:data/SQLite.db";

    public static Connection getConnection() throws SQLException {
        try {
            // Se o arquivo SQLite.db não existir, o driver criará um vazio automaticamente
            return DriverManager.getConnection(URL);

        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados SQLite: " + e.getMessage());
            throw e;
        }
    }
}