package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import java.util.Objects;

/**
 * Rappresenta una fonte di materia prima esterna alla piattaforma,
 * identificata da una stringa.
 */
@Entity
@DiscriminatorValue("ESTERNA")
@JsonTypeName("ESTERNA")
public class FonteEsterna extends FonteMateriaPrima {

    @Column(name = "nome_fornitore", nullable = false)
    private String nomeFornitore;

    public FonteEsterna() {}

    public FonteEsterna(String nomeFornitore) {
        if (nomeFornitore == null || nomeFornitore.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del fornitore non può essere nullo o vuoto");
        }
        this.nomeFornitore = nomeFornitore;
    }

    public void setNomeFornitore(String nomeFornitore) {
        this.nomeFornitore = nomeFornitore;
    }

    public String getNomeFornitore() {
        return nomeFornitore;
    }

    @Override
    public String getDescrizione() {
        return nomeFornitore;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FonteEsterna that = (FonteEsterna) obj;
        return Objects.equals(nomeFornitore, that.nomeFornitore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeFornitore);
    }

    @Override
    public String toString() {
        return "FonteEsterna{nomeFornitore='" + nomeFornitore + "'}";
    }
}