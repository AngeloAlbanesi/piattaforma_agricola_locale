/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

/**
 * Classe che rappresenta un trasformatore nella piattaforma agricola.
 * Un trasformatore è un tipo specializzato di venditore che può anche
 * gestire processi di trasformazione per convertire materie prime in prodotti
 * finiti.
 */
@Entity
@DiscriminatorValue("TRASFORMATORE")
public class Trasformatore extends Venditore {

    public Trasformatore() {}

    /**
     * Costruttore overload per factory (solo parametri base Venditore).
     */
    public Trasformatore(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda,
            TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
    }

    /**
     * Restituisce tutti i prodotti trasformati offerti dal trasformatore.
     *
     * @return Lista dei prodotti trasformati
     */
    public List<Prodotto> getProdottiTrasformati() {
        List<Prodotto> prodottiTrasformati = new ArrayList<>();
        for (Prodotto prodotto : getProdottiOfferti()) {
            if (prodotto.isTrasformato()) {
                prodottiTrasformati.add(prodotto);
            }
        }
        return prodottiTrasformati;
    }

    /**
     * Restituisce tutti i prodotti coltivati/allevati offerti dal trasformatore.
     *
     * @return Lista dei prodotti coltivati/allevati
     */
    public List<Prodotto> getProdottiColtivati() {
        List<Prodotto> prodottiColtivati = new ArrayList<>();
        for (Prodotto prodotto : getProdottiOfferti()) {
            if (prodotto.isColtivato()) {
                prodottiColtivati.add(prodotto);
            }
        }
        return prodottiColtivati;
    }

    /**
     * Conta il numero di prodotti trasformati offerti dal trasformatore.
     *
     * @return Il numero di prodotti trasformati
     */
    public int contaProdottiTrasformati() {
        return getProdottiTrasformati().size();
    }

    /**
     * Conta il numero di prodotti coltivati/allevati offerti dal trasformatore.
     *
     * @return Il numero di prodotti coltivati/allevati
     */
    public int contaProdottiColtivati() {
        return getProdottiColtivati().size();
    }

    /**
     * Verifica se il trasformatore offre prodotti trasformati.
     *
     * @return true se offre prodotti trasformati, false altrimenti
     */
    public boolean offreProdottiTrasformati() {
        return contaProdottiTrasformati() > 0;
    }

    /**
     * Verifica se il trasformatore offre anche prodotti coltivati/allevati.
     *
     * @return true se offre prodotti coltivati/allevati, false altrimenti
     */
    public boolean offreProdottiColtivati() {
        return contaProdottiColtivati() > 0;
    }

    /**
     * Verifica se il trasformatore è un produttore misto (offre sia prodotti
     * trasformati che coltivati).
     *
     * @return true se è un produttore misto, false altrimenti
     */
    public boolean isProduttoreMisto() {
        return offreProdottiTrasformati() && offreProdottiColtivati();
    }

    /**
     * Restituisce una descrizione dettagliata delle capacità del trasformatore.
     *
     * @return Stringa descrittiva delle capacità
     */
    public String getDescrizioneCapacita() {
        StringBuilder descrizione = new StringBuilder();
        descrizione.append("Trasformatore: ").append(getNome()).append(" ").append(getCognome());

        if (isProduttoreMisto()) {
            descrizione.append(" - Produttore misto (").append(contaProdottiColtivati())
                    .append(" prodotti coltivati, ").append(contaProdottiTrasformati())
                    .append(" prodotti trasformati)");
        } else if (offreProdottiTrasformati()) {
            descrizione.append(" - Specializzato in trasformazione (")
                    .append(contaProdottiTrasformati()).append(" prodotti trasformati)");
        } else {
            descrizione.append(" - Prevalentemente produttore (")
                    .append(contaProdottiColtivati()).append(" prodotti coltivati)");
        }

        return descrizione.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "Trasformatore{id=%d, nome='%s %s', azienda='%s', prodotti=%d (%d trasformati, %d coltivati)}",
                getIdUtente(), getNome(), getCognome(),
                getDatiAzienda() != null ? getDatiAzienda().getNomeAzienda() : "N/A",
                getProdottiOfferti().size(),
                contaProdottiTrasformati(),
                contaProdottiColtivati());
    }
}