package it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione;

import jakarta.persistence.*;

@Entity
@Table(name = "metodi_coltivazione")
public class MetodoDiColtivazione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    
    @Column(name = "data_inizio")
    private java.time.LocalDate dataInizio;
    
    @Column(name = "data_fine")
    private java.time.LocalDate dataFine;

    public MetodoDiColtivazione() {}

    public MetodoDiColtivazione(Long id, String nome, String descrizione, java.time.LocalDate dataInizio, java.time.LocalDate dataFine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public java.time.LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(java.time.LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public java.time.LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(java.time.LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    @Override
    public String toString() {
        return "MetodoDiColtivazione{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                '}';
    }
}