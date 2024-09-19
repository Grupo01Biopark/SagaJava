package com.saga.crm.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Perguntas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;

    private Integer importante = 0;

    @Getter
    private Boolean ativa = true;

    @ManyToOne
    private Eixo eixo;

    @ManyToOne
    private Porte porte;

    @ManyToOne
    private Setor setor;

    @OneToMany(mappedBy = "perguntas")
    private Set<ChecklistPerguntas> checklistPerguntas;

    @OneToMany(mappedBy = "pergunta")
    private List<Respostas> respostas;

    public Perguntas(Long id, String titulo, String descricao, Eixo eixo, Porte porte, Setor setor, Set<ChecklistPerguntas> checklistPerguntas) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.eixo = eixo;
        this.porte = porte;
        this.setor = setor;
        this.checklistPerguntas = checklistPerguntas;
    }

    public Perguntas() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Perguntas perguntas = (Perguntas) o;
        return Objects.equals(id, perguntas.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Eixo getEixo() {
        return eixo;
    }

    public void setEixo(Eixo eixo) {
        this.eixo = eixo;
    }

    public Porte getPorte() {
        return porte;
    }

    public void setPorte(Porte porte) {
        this.porte = porte;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Set<ChecklistPerguntas> getChecklistPerguntas() {
        return checklistPerguntas;
    }

    public void setChecklistPerguntas(Set<ChecklistPerguntas> checklistPerguntas) {
        this.checklistPerguntas = checklistPerguntas;
    }

    public Integer getImportante() {
        return importante;
    }

    public void setImportante(Integer importante) {
        this.importante = importante;
    }
}

