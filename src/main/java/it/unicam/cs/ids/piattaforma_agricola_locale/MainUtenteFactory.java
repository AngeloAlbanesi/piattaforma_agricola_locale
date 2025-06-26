package it.unicam.cs.ids.piattaforma_agricola_locale;

import it.unicam.cs.ids.piattaforma_agricola_locale.demo.UtenteFactoryDemo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactoryProvider;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.UtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;

/**
 * Classe principale per la dimostrazione del sistema di gestione utenti.
 */
public class MainUtenteFactory {
    
    public static void main(String[] args) {
        System.out.println("=== Piattaforma di Digitalizzazione e Valorizzazione della Filiera Agricola Locale ===");
        System.out.println("Avvio del sistema di gestione utenti...\n");
        
        // Esegui la demo del sistema di gestione utenti
        UtenteFactoryDemo.main(args);
        
        // Esempio di utilizzo avanzato
        advancedUsageExample();
    }
    
    /**
     * Esempio di utilizzo avanzato del sistema di gestione utenti.
     */
    private static void advancedUsageExample() {
        System.out.println("\n=== Esempio di utilizzo avanzato ===");
        
        // Ottieni il provider delle factory
        UtenteFactoryProvider provider = UtenteFactoryProvider.getInstance();
        
        // Ottieni una factory standard
        UtenteFactory factory = provider.getUtenteFactory();
        
        // Crea il servizio utenti
        IUtenteService utenteService = new UtenteService(factory, provider.getUtenteRepository());
        
        try {
            // Registra un trasformatore
            DatiAzienda datiAziendaTrasformatore = new DatiAzienda(
                    null,
                    "Trasformazioni Bio Italia",
                    "98765432101",
                    "Via dell'Industria 456, Ancona",
                    "Azienda specializzata nella trasformazione di prodotti biologici",
                    "https://example.com/logo_trasformatore.png",
                    "https://www.trasformazionibioitalia.it");
            
            Trasformatore trasformatore = utenteService.registraTrasformatore(
                    "Antonio",
                    "Bianchi",
                    "antonio.bianchi@example.com",
                    "password789",
                    "3335557777",
                    datiAziendaTrasformatore);
            
            System.out.println("Trasformatore registrato con successo: " + trasformatore.getNome() + " " + trasformatore.getCognome());
            System.out.println("ID: " + trasformatore.getId());
            System.out.println("Azienda: " + trasformatore.getDatiAzienda().getNomeAzienda());
            
            // Registra un curatore
            Curatore curatore = utenteService.registraCuratore(
                    "Laura",
                    "Neri",
                    "laura.neri@example.com",
                    "passwordABC",
                    "3336669999");
            
            System.out.println("\nCuratore registrato con successo: " + curatore.getNome() + " " + curatore.getCognome());
            System.out.println("ID: " + curatore.getId());
            System.out.println("Stato accreditamento: " + curatore.getStatoAccreditamento());
            
            // Aggiorna lo stato di accreditamento del curatore
            utenteService.aggiornaStatoAccreditamento(curatore.getId(), StatoAccreditamento.ACCREDITATO);
            
            // Verifica lo stato aggiornato
            utenteService.trovaUtentePerID(curatore.getId()).ifPresent(u -> {
                Curatore c = (Curatore) u;
                System.out.println("Nuovo stato accreditamento: " + c.getStatoAccreditamento());
            });
            
            // Disattiva l'account del trasformatore
            utenteService.disattivaAccount(trasformatore.getId());
            System.out.println("\nAccount del trasformatore disattivato");
            
            // Verifica che l'autenticazione fallisca con un account disattivato
            utenteService.autenticaUtente("antonio.bianchi@example.com", "password789")
                    .ifPresentOrElse(
                            u -> System.out.println("Autenticazione riuscita (non dovrebbe accadere)"),
                            () -> System.out.println("Autenticazione fallita come previsto per account disattivato"));
            
            // Riattiva l'account del trasformatore
            utenteService.riattivaAccount(trasformatore.getId());
            System.out.println("Account del trasformatore riattivato");
            
            // Verifica che l'autenticazione funzioni con un account riattivato
            utenteService.autenticaUtente("antonio.bianchi@example.com", "password789")
                    .ifPresentOrElse(
                            u -> System.out.println("Autenticazione riuscita dopo riattivazione"),
                            () -> System.out.println("Autenticazione fallita (non dovrebbe accadere)"));
            
        } catch (Exception e) {
            System.err.println("Errore durante l'esecuzione dell'esempio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}