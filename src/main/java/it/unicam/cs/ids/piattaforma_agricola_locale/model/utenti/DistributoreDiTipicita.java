/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.PacchettoService;

public class DistributoreDiTipicita extends Venditore {
    // Costruttore overload per factory (solo parametri base Venditore)
    private List<Pacchetto> pacchettiOfferti;

    public DistributoreDiTipicita(int idUtente, String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda,
            java.util.List<Prodotto> prodottiOfferti, TipoRuolo tipoRuolo, boolean isAttivo) {
        super(idUtente, nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, prodottiOfferti, tipoRuolo,
                isAttivo);
        this.pacchettiOfferti = new java.util.ArrayList<>();
    }

    public List<Pacchetto> getPacchettiOfferti() {
        return pacchettiOfferti;
    }

    /*
     * public boolean aggiungiPacchettoCatalogo(Pacchetto pacchetto) {
     * this.pacchettiOfferti.add(pacchetto);
     * return true;
     * }
     * 
     * public boolean rimuoviPacchettoCatalogo(Pacchetto pacchetto) {
     * return this.pacchettiOfferti.remove(pacchetto);
     * }
     * 
     * 
     * public boolean aggiungiProdottoAlPacchetto(Pacchetto pacchetto, Acquistabile
     * prodotto) {
     * if (this.pacchettiOfferti.contains(pacchetto)) {
     * pacchetto.aggiungiElemento(prodotto);
     * 
     * return true;
     * }
     * return false;
     * }
     * 
     * public boolean rimuoviProdottoDalPacchetto(Pacchetto pacchetto, Acquistabile
     * prodotto) {
     * if (this.pacchettiOfferti.contains(pacchetto)) {
     * pacchetto.rimuoviElemento(prodotto);
     * return true;
     * }
     * return false;
     * }
     * 
     */
}