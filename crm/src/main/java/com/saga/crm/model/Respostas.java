package com.saga.crm.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Respostas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer conformidade;
    private String observacoes;

    @ManyToOne
    private Certificados certificado;
    @ManyToOne
    private Perguntas pergunta;

    @ManyToOne FormularioChecklist formularioChecklists;


    public Respostas(Long id, Integer conformidade, String observacoes, Perguntas pergunta, FormularioChecklist formularioChecklists) {
        this.id = id;
        this.conformidade = conformidade;
        this.observacoes = observacoes;
        this.pergunta = pergunta;
        this.formularioChecklists = formularioChecklists;
    }

    public Respostas() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Respostas respostas = (Respostas) o;
        return Objects.equals(id, respostas.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Certificados getCertificado() {
        return certificado;
    }

    public void setCertificado(Certificados certificado) {
        this.certificado = certificado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Perguntas getPergunta() {
        return pergunta;
    }

    public void setPergunta(Perguntas pergunta) {
        this.pergunta = pergunta;
    }

    public Integer getConformidade() {
        return conformidade;
    }

    public void setConformidade(Integer conformidade) {
        this.conformidade = conformidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public FormularioChecklist getFormularioChecklists() {
        return formularioChecklists;
    }

    public void setFormularioChecklists(FormularioChecklist formularioChecklists) {
        this.formularioChecklists = formularioChecklists;
    }
}
