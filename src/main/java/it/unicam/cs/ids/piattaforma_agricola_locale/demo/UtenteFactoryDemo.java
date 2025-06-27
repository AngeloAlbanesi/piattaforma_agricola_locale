package it.unicam.cs.ids.piattaforma_agricola_locale.demo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactoryProvider;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.UtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;

/**
 * Classe dimostrativa per l'utilizzo del sistema di gestione utenti.
 */
public class UtenteFactoryDemo {
    
    public static void main(String[] args) {
        // NOTA: Questo demo è disabilitato durante la migrazione a Spring Boot
        // La gestione delle factory ora usa l'iniezione delle dipendenze di Spring
        System.out.println("Demo temporaneamente disabilitato - usare Spring Boot context per l'iniezione delle dipendenze");
        
        /* 
        
        // Ottieni il provider delle factory (DISABILITATO - ora gestito da Spring)
        UtenteFactoryProvider provider = UtenteFactoryProvider.getInstance();
        
        // Ottieni una factory standard
        UtenteFactory factory = provider.getUtenteFactory();
        
        // Crea il servizio utenti
        IUtenteService utenteService = new UtenteService(factory, provider.getUtenteRepository());
        
        // Esempio di registrazione di un acquirente
        try {
            Acquirente acquirente = utenteService.registraAcquirente(
                    "Mario", 
                    "Rossi", 
                    "mario.rossi@example.com", 
                    "password123", 
                    "3331234567");
            
            System.out.println("Acquirente registrato con successo: " + acquirente.getNome() + " " + acquirente.getCognome());
            System.out.println("ID: " + acquirente.getId());
            System.out.println("Ruolo: " + acquirente.getTipoRuolo());
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nella registrazione dell'acquirente: " + e.getMessage());
        }
        
        // Esempio di registrazione di un produttore
        try {
            // Crea i dati azienda
            DatiAzienda datiAzienda = new DatiAzienda(
                    null, // L'ID verrà assegnato automaticamente
                    "Azienda Agricola Verdi", 
                    "12345678901", 
                    "Via dei Campi 123, Ancona", 
                    "Azienda specializzata in prodotti biologici", 
                    "https://example.com/logo.png", 
                    "https://www.aziendaagricolaverdi.it");
            
            Produttore produttore = utenteService.registraProduttore(
                    "Giuseppe", 
                    "Verdi", 
                    "giuseppe.verdi@example.com", 
                    "password456", 
                    "3339876543", 
                    datiAzienda);
            
            System.out.println("\nProduttore registrato con successo: " + produttore.getNome() + " " + produttore.getCognome());
            System.out.println("ID: " + produttore.getId());
            System.out.println("Ruolo: " + produttore.getTipoRuolo());
            System.out.println("Azienda: " + produttore.getDatiAzienda().getNomeAzienda());
        } catch (IllegalArgumentException e) {
            System.err.println("Errore nella registrazione del produttore: " + e.getMessage());
        }
        
        // Esempio di autenticazione
        try {
            utenteService.autenticaUtente("mario.rossi@example.com", "password123")
                    .ifPresentOrElse(
                            utente -> System.out.println("\nAutenticazione riuscita per: " + utente.getNome() + " " + utente.getCognome()),
                            () -> System.out.println("\nAutenticazione fallita: credenziali non valide"));
        } catch (Exception e) {
            System.err.println("Errore nell'autenticazione: " + e.getMessage());
        }
        
        // Esempio di ricerca utenti
        System.out.println("\nLista di tutti gli utenti:");
        utenteService.trovaTuttiGliUtenti().forEach(utente -> 
            System.out.println("- " + utente.getNome() + " " + utente.getCognome() + " (" + utente.getTipoRuolo() + ")"));
        
        // Esempio di ricerca utenti per tipo
        System.out.println("\nLista dei produttori:");
        utenteService.trovaUtentiPerTipo(TipoRuolo.PRODUTTORE).forEach(utente -> 
            System.out.println("- " + utente.getNome() + " " + utente.getCognome()));
        */
    }
}