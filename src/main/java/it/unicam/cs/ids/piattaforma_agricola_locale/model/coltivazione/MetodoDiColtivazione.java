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
    
    @Column(name = "descrizione_dettagliata", columnDefinition = "TEXT")
    private String descrizioneDettagliata;
    
    @Column(name = "tecnica_principale")
    private String tecnicaPrincipale;
    
    @Column(name = "ambiente_coltivazione")
    private String ambienteColtivazione;

    public MetodoDiColtivazione() {}

    public MetodoDiColtivazione(Long id, String nome, String descrizioneDettagliata, String tecnicaPrincipale, String ambienteColtivazione) {
        this.id = id;
        this.nome = nome;
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.tecnicaPrincipale = tecnicaPrincipale;
        this.ambienteColtivazione = ambienteColtivazione;
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

    public String getDescrizioneDettagliata() {
        return descrizioneDettagliata;
    }

    public void setDescrizioneDettagliata(String descrizioneDettagliata) {
        this.descrizioneDettagliata = descrizioneDettagliata;
    }

    public String getTecnicaPrincipale() {
        return tecnicaPrincipale;
    }

    public void setTecnicaPrincipale(String tecnicaPrincipale) {
        this.tecnicaPrincipale = tecnicaPrincipale;
    }

    public String getAmbienteColtivazione() {
        return ambienteColtivazione;
    }

    public void setAmbienteColtivazione(String ambienteColtivazione) {
        this.ambienteColtivazione = ambienteColtivazione;
    }

    @Override
    public String toString() {
        return "MetodoDiColtivazione{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descrizioneDettagliata='" + descrizioneDettagliata + '\'' +
                ", tecnicaPrincipale='" + tecnicaPrincipale + '\'' +
                ", ambienteColtivazione='" + ambienteColtivazione + '\'' +
                '}';
    }
}