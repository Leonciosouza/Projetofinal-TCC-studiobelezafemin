package com.belezastudio.api.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueio_agenda")
public class BloqueioAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBloqueio;

    // Se for nulo, significa que o salão INTEIRO está fechado.
    @ManyToOne
    @JoinColumn(name = "id_profissional", nullable = true)
    private Profissional profissional;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String motivo;

    // Gere os Getters e Settes aqui (ou use @Data do lombok se preferir).


    public Long getIdBloqueio() {
        return idBloqueio;
    }

    public void setIdBloqueio(Long idBloqueio) {
        this.idBloqueio = idBloqueio;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
