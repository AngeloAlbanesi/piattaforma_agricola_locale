package it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione;

public class MetodoDiColtivazione {
    
    private long id;
    private String nome;
    private String descrizioneDettagliata;
    private String tecnicaPrincipale;
    private String ambienteColtivazione;

    public MetodoDiColtivazione(long id, String nome, String descrizioneDettagliata, String tecnicaPrincipale, String ambienteColtivazione) {
        this.id = id;
        this.nome = nome;
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.tecnicaPrincipale = tecnicaPrincipale;
        this.ambienteColtivazione = ambienteColtivazione;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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