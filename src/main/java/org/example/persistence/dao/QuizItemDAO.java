package org.example.persistence.dao;

import org.example.persistence.entity.QuizItem;
import org.example.persistence.config.SQLiteConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Autores: [Seu Nome]
 * Descrição: Classe responsável pelo acesso a dados da tabela QuizItem.
 */
public class QuizItemDAO {

    // ==========================================================
    // ETAPA 1: INSERIR NOVA FRASE
    // ==========================================================
    public void insert(QuizItem item) throws SQLException {
        String sql = "INSERT INTO QuizItem (sentence, answer, level, category_id) VALUES (?, ?, ?, ?)";

        // O bloco try-with-resources garante o fechamento automático da Connection e do PreparedStatement
        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getSentence());
            stmt.setInt(2, item.getAnswer());
            stmt.setInt(3, item.getLevel());
            stmt.setInt(4, item.getCategoryId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Erro ao acessar o banco de dados para inserir a frase: " + e.getMessage(), e);
        }
    }

    // ==========================================================
    // ETAPA 4: LISTAR FRASES POR CATEGORIA (ID ESPECÍFICO)
    // ==========================================================
    public List<QuizItem> findByCategory(int categoryId) throws SQLException {
        List<QuizItem> itens = new ArrayList<>();

        // INNER JOIN para buscar o nome da categoria junto com os dados da frase
        String sql = "SELECT q.id, q.sentence, q.answer, q.level, q.category_id, c.description AS category_name " +
                "FROM QuizItem q " +
                "INNER JOIN Category c ON q.category_id = c.id " +
                "WHERE q.category_id = ? " +
                "ORDER BY q.id";

        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                preencherLista(itens, rs);
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar frases pela categoria: " + e.getMessage(), e);
        }

        return itens;
    }

    // ==========================================================
    // ETAPA 4: LISTAR TODAS AS FRASES (ID = 0)
    // ==========================================================
    public List<QuizItem> findAll() throws SQLException {
        List<QuizItem> itens = new ArrayList<>();

        // INNER JOIN ordenado pelo nome da categoria, conforme requisito do trabalho
        String sql = "SELECT q.id, q.sentence, q.answer, q.level, q.category_id, c.description AS category_name " +
                "FROM QuizItem q " +
                "INNER JOIN Category c ON q.category_id = c.id " +
                "ORDER BY c.description, q.id";

        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            preencherLista(itens, rs);

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar todas as frases: " + e.getMessage(), e);
        }

        return itens;
    }

    /**
     * Evita repetição de código extraindo os dados do ResultSet e preenchendo a lista.
     */
    private void preencherLista(List<QuizItem> itens, ResultSet rs) throws SQLException {
        while (rs.next()) {
            QuizItem item = new QuizItem();
            item.setId(rs.getInt("id"));
            item.setSentence(rs.getString("sentence"));
            item.setAnswer(rs.getInt("answer"));
            item.setLevel(rs.getInt("level"));
            item.setCategoryId(rs.getInt("category_id"));

            // Atributo adicionado na Etapa 4 para armazenar o nome que veio do JOIN
            item.setCategoryName(rs.getString("category_name"));

            itens.add(item);
        }
    }
    public QuizItem findById(int id) throws SQLException {
        String sql = "SELECT * FROM QuizItem WHERE id = ?";

        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QuizItem item = new QuizItem();
                    item.setId(rs.getInt("id"));
                    item.setSentence(rs.getString("sentence"));
                    item.setAnswer(rs.getInt("answer"));
                    item.setLevel(rs.getInt("level"));
                    // O nome da coluna no banco pode ser category_id ou categoryId dependendo de como você criou na Etapa 1
                    item.setCategoryId(rs.getInt("category_id"));
                    return item;
                }
            }
        }
        return null; // Retorna nulo se o ID não existir no banco
    }

    // 2. MÉTODO PARA ATUALIZAR A FRASE
    public void update(QuizItem item) throws SQLException {
        // SQL do UPDATE limitando apenas aos campos permitidos na Etapa 5
        String sql = "UPDATE QuizItem SET sentence = ?, answer = ?, level = ? WHERE id = ?";

        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getSentence());
            stmt.setInt(2, item.getAnswer());
            stmt.setInt(3, item.getLevel());
            stmt.setInt(4, item.getId()); // O ID vai no WHERE

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar frase no banco de dados.", e);
        }
    }
    public void delete(int id) throws SQLException {
        // SQL do DELETE filtrando pelo ID da frase
        String sql = "DELETE FROM QuizItem WHERE id = ?";

        // O try-with-resources garante o fechamento da Connection e PreparedStatement
        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir frase no banco de dados.", e);
        }
    }
    public List<QuizItem> findAllForExport() throws SQLException {
        List<QuizItem> itens = new ArrayList<>();

        // SQL com JOIN ordenado por categoria e id da frase
        String sql = "SELECT c.description AS category_name, q.id, q.sentence, q.answer, q.level " +
                "FROM QuizItem q " +
                "INNER JOIN Category c ON q.category_id = c.id " +
                "ORDER BY c.description, q.id";

        try (Connection conn = SQLiteConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                QuizItem item = new QuizItem();
                // A exportação não precisa do categoryId numérico, apenas do nome
                item.setCategoryName(rs.getString("category_name"));
                item.setId(rs.getInt("id"));
                item.setSentence(rs.getString("sentence"));
                item.setAnswer(rs.getInt("answer"));
                item.setLevel(rs.getInt("level"));

                itens.add(item);
            }
        }
        return itens;
    }
}