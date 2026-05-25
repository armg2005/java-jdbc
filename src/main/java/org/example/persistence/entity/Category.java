package org.example.persistence.entity;

/**
 * Autores: [Seu Nome]
 * Descrição: Representa a entidade Category no banco de dados.
 */
public class Category {
    private Integer id;
    private String description;

    public Category() {}

    public Category(String description) {
        this.description = description;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}