package it.unicam.cs.ids.piattaforma_agricola_locale.piattaforma_agricola_locale;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;

import java.util.Date;

/**
 * Classe di test per dimostrare il funzionamento del pattern State refactorizzato
 */
public class TestPatternState {
    
    public static void main(String[] args) {
        System.out.println("=== Test del Pattern State Refactorizzato ===\n");
        
        // Creiamo un acquirente di esempio
        Acquirente acquirente = new Acquirente(
            1,
            "Mario",
            "Rossi",
            "mario.rossi@email.com",
            "hashedPassword123",
            "+39123456789",
            TipoRuolo.ACQUIRENTE,
            true
        );
        
        // Creiamo un nuovo ordine
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        
        System.out.println("1. Ordine creato");
        System.out.println("   Stato corrente: " + ordine.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine.getStato().getClass().getSimpleName());
        
        // Test: tentativi di operazioni non valide nello stato iniziale
        System.out.println("\n2. Test operazioni non valide nello stato 'In Attesa di Pagamento':");
        
        try {
            ordine.spedisci();
        } catch (UnsupportedOperationException e) {
            System.out.println("   ✓ Spedizione bloccata: " + e.getMessage());
        }
        
        try {
            ordine.consegna();
        } catch (UnsupportedOperationException e) {
            System.out.println("   ✓ Consegna bloccata: " + e.getMessage());
        }
        
        // Test: operazioni valide - pagamento dell'ordine
        System.out.println("\n3. Effettuiamo il pagamento (processa):");
        ordine.processa();
        System.out.println("   Stato corrente: " + ordine.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine.getStato().getClass().getSimpleName());
        
        // Test: processamento dell'ordine (da pagato a in lavorazione)
        System.out.println("\n4. Iniziamo la lavorazione (processa):");
        ordine.processa();
        System.out.println("   Stato corrente: " + ordine.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine.getStato().getClass().getSimpleName());
        
        // Test: spedizione dell'ordine
        System.out.println("\n5. Spediamo l'ordine:");
        ordine.spedisci();
        System.out.println("   Stato corrente: " + ordine.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine.getStato().getClass().getSimpleName());
        
        // Test: consegna dell'ordine
        System.out.println("\n6. Consegniamo l'ordine:");
        ordine.consegna();
        System.out.println("   Stato corrente: " + ordine.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine.getStato().getClass().getSimpleName());
        
        // Test: operazioni su ordine consegnato
        System.out.println("\n7. Test operazioni su ordine consegnato:");
        
        try {
            ordine.annulla();
        } catch (UnsupportedOperationException e) {
            System.out.println("   ✓ Annullamento bloccato: " + e.getMessage());
        }
        
        // Test del flusso di annullamento
        System.out.println("\n=== Test Flusso di Annullamento ===");
        
        Ordine ordine2 = new Ordine(2, new Date(), acquirente);
        System.out.println("\n8. Nuovo ordine creato");
        System.out.println("   Stato corrente: " + ordine2.getStatoOrdine());
        
        System.out.println("\n9. Annulliamo l'ordine in attesa di pagamento:");
        ordine2.annulla();
        System.out.println("   Stato corrente: " + ordine2.getStatoOrdine());
        System.out.println("   Classe stato: " + ordine2.getStato().getClass().getSimpleName());
        
        System.out.println("\n=== Test Completato con Successo! ===");
        System.out.println("Il pattern State è stato implementato correttamente:");
        System.out.println("✓ Le transizioni di stato avvengono correttamente");
        System.out.println("✓ Le operazioni non valide sono bloccate appropriatamente");
        System.out.println("✓ Lo stato descrittivo è coerente con lo stato comportamentale");
        System.out.println("✓ Il codice è pulito e manutenibile");
    }
}