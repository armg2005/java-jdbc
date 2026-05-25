package org.example.persistence.entity;

/**
 * Autores: [Alisson Ricady]
 * Descrição: Classe que representa a entidade QuizItem no banco de dados.
 */
public class QuizItem {
    private Integer id;
    private String sentence;
    private Integer answer;
    private Integer level;
    private Integer categoryId;

    // Novo atributo para guardar o nome da categoria vindo do JOIN
    private String categoryName;

    public QuizItem() {}

    public QuizItem(String sentence, Integer answer, Integer level, Integer categoryId) {
        this.sentence = sentence;
        this.answer = answer;
        this.level = level;
        this.categoryId = categoryId;
    }

    // --- Getters e Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }

    public Integer getAnswer() { return answer; }
    public void setAnswer(Integer answer) { this.answer = answer; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    // Getters e Setters do novo atributo (categoryName)
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}