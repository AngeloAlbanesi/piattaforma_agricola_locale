package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

public enum StatoAccreditamento {
    PENDING,      // In attesa di approvazione da parte del Gestore
    ACCREDITATO,  // Approvato, può operare secondo il suo TipoRuolo
    SOSPESO,      // Accreditamento temporaneamente sospeso dal Gestore
    RIFIUTATO     // Richiesta di accreditamento rifiutata
}