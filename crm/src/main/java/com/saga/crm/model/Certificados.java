package com.saga.crm.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
public class Certificados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean aprovado;

    private Long nota_gov;
    private Long nota_amb;
    private Long nota_soc;
    //@Temporal(TemporalType.DATE)
    private LocalDateTime data;

    @ManyToOne
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "formulario_id")
    private Formulario formulario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificados that = (Certificados) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Certificados(Long id, boolean aprovado, LocalDateTime data, Empresa empresa, Long nota_gov, Long nota_amb, Long nota_soc, Formulario formulario) {
        this.id = id;
        this.aprovado = aprovado;
        this.data = data;
        this.empresa = empresa;
        this.nota_gov = nota_gov;
        this.nota_amb = nota_amb;
        this.nota_soc = nota_soc;
        this.formulario = formulario;
    }

    public Certificados() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Long getNota_gov() {
        return nota_gov;
    }

    public void setNota_gov(Long nota_gov) {
        this.nota_gov = nota_gov;
    }

    public Long getNota_amb() {
        return nota_amb;
    }

    public void setNota_amb(Long nota_amb) {
        this.nota_amb = nota_amb;
    }

    public Long getNota_soc() {
        return nota_soc;
    }

    public void setNota_soc(Long nota_soc) {
        this.nota_soc = nota_soc;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }
}
