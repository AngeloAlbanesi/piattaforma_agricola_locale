package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;

public interface UtenteFactory {
    Utente creaUtente(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        String idUtente,
        TipoRuolo tipoRuolo,
        boolean isAttivo,
        it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda datiAzienda,
        java.util.List<it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto> prodottiOfferti
    );
}