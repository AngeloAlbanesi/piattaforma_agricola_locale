package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import jakarta.persistence.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import java.util.Objects;

/**
 * Rappresenta una fonte di materia prima interna alla piattaforma,
 * ovvero un Produttore registrato.
 */
@Entity
@DiscriminatorValue("INTERNA")
public class FonteInterna extends FonteMateriaPrima {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produttore", nullable = false)
    private Produttore produttore;

    public FonteInterna() {}

    public FonteInterna(Produttore produttore) {
        if (produttore == null) {
            throw new IllegalArgumentException("Il produttore non può essere nullo");
        }
        this.produttore = produttore;
    }

    public void setProduttore(Produttore produttore) {
        this.produttore = produttore;
    }

    public Produttore getProduttore() {
        return produttore;
    }

    @Override
    public String getDescrizione() {
        String nomeCompleto = produttore.getNome() + " " + produttore.getCognome();
        String nomeAzienda = (produttore.getDatiAzienda() != null
                && produttore.getDatiAzienda().getNomeAzienda() != null)
                ? produttore.getDatiAzienda().getNomeAzienda()
                : "N/D";
        return nomeCompleto + " - " + nomeAzienda;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FonteInterna that = (FonteInterna) obj;
        return Objects.equals(produttore, that.produttore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produttore);
    }

    @Override
    public String toString() {
        return "FonteInterna{produttore=" + produttore.getNome() + " " + produttore.getCognome() +
                ", azienda="
                + (produttore.getDatiAzienda() != null ? produttore.getDatiAzienda().getNomeAzienda() : "N/D") + "}";
    }
}