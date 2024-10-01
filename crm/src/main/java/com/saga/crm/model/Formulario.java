package com.saga.crm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Entity
public class Formulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;

    @Getter
    private Boolean ativo;

    @OneToMany(mappedBy = "formulario")
    @JsonManagedReference
    private Set<FormularioChecklist> formularioChecklists;

    @OneToMany(mappedBy = "formulario")
    private Set<Certificados> certificados;



    public Formulario(Long id, String titulo, String descricao, Set<FormularioChecklist> formularioChecklists) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.formularioChecklists = formularioChecklists;
    }

    public Formulario() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Formulario that = (Formulario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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

    public Set<FormularioChecklist> getFormularioChecklists() {
        return formularioChecklists;
    }

    public void setFormularioChecklists(Set<FormularioChecklist> formularioChecklists) {
        this.formularioChecklists = formularioChecklists;
    }
}
