package org.example.service;

import org.example.persistence.dao.CategoryDAO;
import org.example.persistence.dao.QuizItemDAO;
import org.example.persistence.entity.QuizItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Autores: [Seu Nome]
 * Descrição: Classe de serviço contendo as regras de negócio para QuizItem.
 */
public class QuizItemService {

    private final QuizItemDAO quizItemDAO;
    private final CategoryDAO categoryDAO; // Adicionado para fazer a validação cruzada

    public QuizItemService() {
        this.quizItemDAO = new QuizItemDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public String cadastrarNovaFrase(String sentence, Integer answer, Integer level, Integer categoryId) {
        // Validação: Todos os campos são obrigatórios
        if (sentence == null || sentence.trim().isEmpty() || answer == null || level == null || categoryId == null) {
            return "Erro: Todos os campos são obrigatórios.";
        }

        // Validação da regra de negócio para 'answer' (0 ou 1)
        if (answer != 0 && answer != 1) {
            return "Erro: O valor de 'answer' deve ser 0 (Falso) ou 1 (Verdadeiro).";
        }

        // Validação da regra de negócio para 'level' (0, 1 ou 2)
        if (level < 0 || level > 2) {
            return "Erro: O valor de 'level' deve ser 0 (Fácil), 1 (Médio) ou 2 (Difícil).";
        }

        QuizItem newItem = new QuizItem(sentence, answer, level, categoryId);

        try {
            quizItemDAO.insert(newItem);
            return "Sucesso: Frase cadastrada com sucesso no banco de dados!";
        } catch (SQLException e) {
            // Tratamento da exceção retornando mensagem amigável
            return "Falha ao inserir frase no banco: " + e.getMessage();
        }
    }
    public List<QuizItem> listarPorCategoria(int categoryId) throws IllegalArgumentException {
        try {
            if (categoryId == 0) {
                // Regra: ID 0 = Lista todas as frases
                return quizItemDAO.findAll();
            } else {
                // Validação: Se ID > 0, verifica se a categoria existe
                if (!categoryDAO.exists(categoryId)) {
                    throw new IllegalArgumentException("Categoria não encontrada.");
                }
                // Se existe, busca as frases dela
                return quizItemDAO.findByCategory(categoryId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno no banco de dados ao buscar frases.", e);
        }
    }
    public QuizItem buscarPorId(int id) {
        try {
            return quizItemDAO.findById(id);
        } catch (SQLException e) {
            System.err.println("Erro interno ao buscar frase: " + e.getMessage());
            return null;
        }
    }

    // 2. MÉTODO DE ATUALIZAÇÃO COM VALIDAÇÃO
    public String atualizarFrase(int id, String newSentence, Integer newAnswer, Integer newLevel) {
        // Validação dos campos obrigatórios
        if (newSentence == null || newSentence.trim().isEmpty() || newAnswer == null || newLevel == null) {
            return "Erro: A frase, resposta e nível são obrigatórios.";
        }

        // Validação da regra de negócio para 'answer' (0 ou 1)
        if (newAnswer != 0 && newAnswer != 1) {
            return "Erro: O valor de 'answer' deve ser 0 (Falso) ou 1 (Verdadeiro).";
        }

        // Validação da regra de negócio para 'level' (0, 1 ou 2)
        if (newLevel < 0 || newLevel > 2) {
            return "Erro: O valor de 'level' deve ser 0 (Fácil), 1 (Médio) ou 2 (Difícil).";
        }

        try {
            // Buscamos a frase antiga para não perder dados como o categoryId
            QuizItem itemToUpdate = quizItemDAO.findById(id);
            if (itemToUpdate == null) {
                return "Frase não encontrada."; // Segurança extra
            }

            // Atualizamos apenas os campos permitidos
            itemToUpdate.setSentence(newSentence.trim());
            itemToUpdate.setAnswer(newAnswer);
            itemToUpdate.setLevel(newLevel);

            quizItemDAO.update(itemToUpdate);
            return "Sucesso: Frase atualizada com sucesso!";

        } catch (SQLException e) {
            return "Falha ao atualizar a frase: " + e.getMessage();
        }
    }
    public String excluirFrase(int id) {
        try {
            // Opcional: Você pode chamar buscarPorId(id) aqui novamente para garantir
            // que a frase existe, mas como o Main já faz isso, podemos chamar direto o delete.
            quizItemDAO.delete(id);
            return "Sucesso: Frase excluída com sucesso!";
        } catch (SQLException e) {
            return "Falha ao excluir a frase: " + e.getMessage();
        }
    }
    public String exportarParaCSV(String nomeArquivo) {
        try {
            List<QuizItem> itens = quizItemDAO.findAllForExport();

            if (itens.isEmpty()) {
                return "Aviso: Não há dados cadastrados para exportar.";
            }

            // O try-with-resources garante que o arquivo será fechado ao final do bloco
            try (FileWriter fw = new FileWriter(nomeArquivo);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                // 1. Escreve o cabeçalho
                bw.write("categoria;id;sentence;answer;level");
                bw.newLine(); // Quebra de linha

                // 2. Percorre a lista e escreve cada item
                for (QuizItem item : itens) {
                    String linha = String.format("%s;%d;%s;%d;%d",
                            item.getCategoryName(),
                            item.getId(),
                            item.getSentence(),
                            item.getAnswer(),
                            item.getLevel()
                    );
                    bw.write(linha);
                    bw.newLine();
                }
            } catch (IOException e) {
                return "Erro ao gravar o arquivo CSV: " + e.getMessage();
            }

            return "Sucesso: Dados exportados com sucesso para o arquivo '" + nomeArquivo + "'.";

        } catch (SQLException e) {
            return "Erro no banco de dados ao buscar dados para exportação: " + e.getMessage();
        }
    }
}