/*package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.GestorePiattaforma;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
//import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

import java.util.UUID;

public class SimpleUtenteFactory implements UtenteFactory {
    @Override
    public Utente creaUtente(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        TipoRuolo tipoRuolo,
        boolean isAttivo,
        DatiAzienda datiAzienda,
        java.util.List<Prodotto> prodottiOfferti
    ) {
        int idUtente = UUID.randomUUID().hashCode();
        switch (tipoRuolo) {

            case ACQUIRENTE:
                return new Acquirente(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
            case PRODUTTORE:
                return new Produttore(
                    idUtente, nome, cognome, email, passwordHash, numeroTelefono,
                    datiAzienda, prodottiOfferti, tipoRuolo
                );
            // case TRASFORMATORE:
            //     return new Trasformatore(
            //         idUtente, nome, cognome, email, passwordHash, numeroTelefono,
            //         datiAzienda, prodottiOfferti, tipoRuolo, isAttivo
            //     );
            case DISTRIBUTORE_DI_TIPICITA:
                return new DistributoreDiTipicita(
                    idUtente, nome, cognome, email, passwordHash, numeroTelefono,
                    datiAzienda, prodottiOfferti, tipoRuolo);
            case CURATORE:
                return new Curatore(idUtente,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
            case ANIMATORE_DELLA_FILIERA:
                return new AnimatoreDellaFiliera(idUtente,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
            default:
                throw new IllegalArgumentException("TipoRuolo non supportato: " + tipoRuolo);
        }
    }
}*/