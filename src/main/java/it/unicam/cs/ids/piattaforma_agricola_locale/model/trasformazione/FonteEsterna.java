package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import java.util.Objects;

/**
 * Rappresenta una fonte di materia prima esterna alla piattaforma,
 * identificata da una stringa.
 */
public class FonteEsterna implements FonteMateriaPrima {

    private final String nomeFornitore;

    public FonteEsterna(String nomeFornitore) {
        if (nomeFornitore == null || nomeFornitore.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del fornitore non può essere nullo o vuoto");
        }
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