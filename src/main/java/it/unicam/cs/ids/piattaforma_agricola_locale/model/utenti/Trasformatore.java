// /*
//  *   Copyright (c) 2025 
//  *   All rights reserved.
//  */
// package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

// import java.util.List;

// import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.tracciabilita.ProcessoDiTrasformazione;

// public class Trasformatore extends Venditore {
// // Costruttore overload per factory (solo parametri base Venditore)
//     public Trasformatore(String idUtente, String nome, String cognome, String email, String passwordHash,
//             String numeroTelefono, DatiAzienda datiAzienda,
//             java.util.List<Prodotto> prodottiOfferti, TipoRuolo tipoRuolo, boolean isAttivo) {
//         super(idUtente, nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, prodottiOfferti, tipoRuolo, isAttivo);
//         this.processiDiTrasformazione = new java.util.ArrayList<>();
//     }
//     private List<ProcessoDiTrasformazione> processiDiTrasformazione;

//     public Trasformatore(String idUtente, String nome, String cognome, String email, String passwordHash,
//             String numeroTelefono, DatiAzienda datiAzienda,
//             List<Prodotto> prodottiOfferti, // Aggiunto per Venditore
//             List<ProcessoDiTrasformazione> processiDiTrasformazione, // Specifico per Trasformatore
//             TipoRuolo tipoRuolo, boolean isAttivo) {
//         super(idUtente, nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, prodottiOfferti, tipoRuolo,
//                 isAttivo);
//         this.processiDiTrasformazione = processiDiTrasformazione;
//     }

//     public void aggiungiProcessoTrasformazione(ProcessoDiTrasformazione processo) {
//         this.processiDiTrasformazione.add(processo);
//     }
//     public void rimuoviProcessoTrasformazione(ProcessoDiTrasformazione processo) {
//         this.processiDiTrasformazione.remove(processo);
//     }
//     public List<ProcessoDiTrasformazione> getProcessiDiTrasformazione() {
//         return processiDiTrasformazione;
//     }
//     public void setProcessiDiTrasformazione(List<ProcessoDiTrasformazione> processiDiTrasformazione) {
//         this.processiDiTrasformazione = processiDiTrasformazione;
//     }
// }