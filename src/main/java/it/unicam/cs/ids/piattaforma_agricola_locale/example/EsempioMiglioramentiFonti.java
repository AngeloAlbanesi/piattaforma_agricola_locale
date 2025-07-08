package it.unicam.cs.ids.piattaforma_agricola_locale.example;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.FaseLavorazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.ProcessoTrasformazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ProcessoTrasformazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteEsterna;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteInterna;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.ProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProcessoMapper;

/**
 * Esempio pratico che dimostra i miglioramenti implementati per la gestione
 * delle fonti di materie prime nei DTO del processo di trasformazione.
 *
 * MIGLIORAMENTI IMPLEMENTATI:
 * 1. Preservazione dell'oggetto FonteMateriaPrima completo nei DTO
 * 2. Metodi di utilità per distinguere fonti interne ed esterne
 * 3. Accesso diretto all'ID del produttore per fonti interne
 * 4. Mantenimento della tracciabilità completa
 */
public class EsempioMiglioramentiFonti {

    public static void main(String[] args) {
        System.out.println("=== ESEMPIO: MIGLIORAMENTI GESTIONE FONTI MATERIE PRIME ===\n");

        // Setup iniziale
        EsempioMiglioramentiFonti esempio = new EsempioMiglioramentiFonti();
        esempio.dimostraMiglioramenti();
    }

    public void dimostraMiglioramenti() {
        // 1. Creazione entità di test
        System.out.println("1. CREAZIONE ENTITÀ DI TEST");
        System.out.println("----------------------------");

        // Produttore interno
        DatiAzienda aziendaProduttore = new DatiAzienda(1, "Allevamento Bio Toscana",
                "12345678901", "Via Campagna 15, Siena",
                "Allevamento biologico certificato", "logo_bio.png", "www.biotoscana.it");
        Produttore produttore = new Produttore("Marco", "Bianchi",
                "marco@biotoscana.it", "password", "3331234567",
                aziendaProduttore, TipoRuolo.PRODUTTORE);

        // Trasformatore
        DatiAzienda aziendaTrasformatore = new DatiAzienda(2, "Caseificio Artigianale Toscano",
                "98765432109", "Via Industria 8, Firenze",
                "Produzione formaggi tradizionali", "logo_caseificio.png", "www.caseificiotoscano.it");
        Trasformatore trasformatore = new Trasformatore( "Giuseppe", "Rossi",
                "giuseppe@caseificiotoscano.it", "password", "3339876543",
                aziendaTrasformatore, TipoRuolo.TRASFORMATORE);

        System.out.println("✓ Produttore creato: " + produttore.getNome() + " " + produttore.getCognome());
        System.out.println("✓ Trasformatore creato: " + trasformatore.getNome() + " " + trasformatore.getCognome());
        System.out.println();

        // 2. Creazione processo con fasi miste
        System.out.println("2. CREAZIONE PROCESSO CON FONTI MISTE");
        System.out.println("-------------------------------------");

        ProcessoTrasformazioneService service = new ProcessoTrasformazioneService(
                new ProcessoTrasformazioneRepository());

        ProcessoTrasformazione processo = service.creaProcesso(
                "Produzione Pecorino Toscano DOP",
                "Processo tradizionale per pecorino stagionato 12 mesi",
                trasformatore,
                "Metodo tradizionale toscano");

        // Fase 1: Fonte interna (produttore della piattaforma)
        FaseLavorazione fase1 = new FaseLavorazione(
                "Raccolta Latte Ovino",
                "Mungitura mattutina delle pecore al pascolo",
                1,
                "Latte ovino fresco",
                new FonteInterna(produttore));

        // Fase 2: Fonte esterna (fornitore esterno)
        FaseLavorazione fase2 = new FaseLavorazione(
                "Aggiunta Caglio",
                "Aggiunta caglio naturale di agnello",
                2,
                "Caglio naturale",
                new FonteEsterna("Salumificio Senese - Lotto CAG2024-001"));

        // Fase 3: Altra fonte esterna
        FaseLavorazione fase3 = new FaseLavorazione(
                "Inoculazione Fermenti",
                "Aggiunta fermenti lattici selezionati",
                3,
                "Fermenti lattici",
                new FonteEsterna("Laboratorio Microbiologico Toscano Srl"));

        processo = service.aggiungiFaseAlProcesso(processo.getId(), fase1);
        processo = service.aggiungiFaseAlProcesso(processo.getId(), fase2);
        processo = service.aggiungiFaseAlProcesso(processo.getId(), fase3);

        System.out.println("✓ Processo creato: " + processo.getNome());
        System.out.println("✓ Fasi aggiunte: " + processo.getNumeroFasi());
        System.out.println();

        // 3. Conversione a DTO e dimostrazione miglioramenti
        System.out.println("3. CONVERSIONE A DTO E ANALISI MIGLIORAMENTI");
        System.out.println("--------------------------------------------");

        ProcessoMapper mapper = new ProcessoMapper();
        ProcessoTrasformazioneDTO dto = mapper.toDto(processo);

        System.out.println("Processo DTO: " + dto.getNomeProcesso());
        System.out.println("Numero fasi: " + dto.getFasi().size());
        System.out.println();

        // 4. Analisi dettagliata di ogni fase
        System.out.println("4. ANALISI DETTAGLIATA DELLE FASI");
        System.out.println("----------------------------------");

        for (int i = 0; i < dto.getFasi().size(); i++) {
            FaseLavorazioneDTO faseDTO = dto.getFasi().get(i);

            System.out.println("FASE " + (i + 1) + ": " + faseDTO.getNome());
            System.out.println("  Materia Prima: " + faseDTO.getMateriaPrimaUtilizzata());
            System.out.println("  Tipo Fonte: " + faseDTO.getTipoFonte());
            System.out.println("  Descrizione Fonte: " + faseDTO.getDescrizioneFonte());

            // MIGLIORAMENTO: Distinguere tra fonti interne ed esterne
            if (faseDTO.isFonteInterna()) {
                System.out.println("  🏠 FONTE INTERNA - Produttore della piattaforma");
                System.out.println("  📋 ID Produttore: " + faseDTO.getIdProduttoreInterno());
                System.out.println("  ✅ Tracciabilità completa disponibile");

                // MIGLIORAMENTO: Accesso diretto all'oggetto fonte
                FonteInterna fonteInterna = (FonteInterna) faseDTO.getFonteMateriaPrima();
                System.out.println("  👤 Produttore: " + fonteInterna.getProduttore().getNome() +
                        " " + fonteInterna.getProduttore().getCognome());
                System.out.println("  🏢 Azienda: " + fonteInterna.getProduttore().getDatiAzienda().getNomeAzienda());
            } else {
                System.out.println("  🌐 FONTE ESTERNA - Fornitore esterno");
                System.out.println("  📋 ID Produttore: N/A");
                System.out.println("  ⚠  Tracciabilità limitata");

                // MIGLIORAMENTO: Accesso diretto all'oggetto fonte
                FonteEsterna fonteEsterna = (FonteEsterna) faseDTO.getFonteMateriaPrima();
                System.out.println("  🏭 Fornitore: " + fonteEsterna.getNomeFornitore());
            }
            System.out.println();
        }

        // 5. Dimostrazione funzionalità avanzate
        System.out.println("5. FUNZIONALITÀ AVANZATE");
        System.out.println("------------------------");

        // Conteggio fonti per tipo
        long fontiInterne = dto.getFasi().stream()
                .filter(FaseLavorazioneDTO::isFonteInterna)
                .count();
        long fontiEsterne = dto.getFasi().stream()
                .filter(fase -> !fase.isFonteInterna())
                .count();

        System.out.println("📊 STATISTICHE PROCESSO:");
        System.out.println("  • Fonti interne: " + fontiInterne);
        System.out.println("  • Fonti esterne: " + fontiEsterne);
        System.out.println("  • Percentuale tracciabilità interna: " +
                String.format("%.1f%%", (fontiInterne * 100.0) / dto.getFasi().size()));

        // Elenco produttori interni coinvolti
        System.out.println("\n🏠 PRODUTTORI INTERNI COINVOLTI:");
        dto.getFasi().stream()
                .filter(FaseLavorazioneDTO::isFonteInterna)
                .forEach(fase -> {
                    FonteInterna fonte = (FonteInterna) fase.getFonteMateriaPrima();
                    System.out.println("  • ID " + fonte.getProduttore().getIdUtente() +
                            ": " + fonte.getProduttore().getNome() +
                            " " + fonte.getProduttore().getCognome() +
                            " (" + fonte.getProduttore().getDatiAzienda().getNomeAzienda() + ")");
                });

        // Elenco fornitori esterni
        System.out.println("\n🌐 FORNITORI ESTERNI:");
        dto.getFasi().stream()
                .filter(fase -> !fase.isFonteInterna())
                .forEach(fase -> {
                    FonteEsterna fonte = (FonteEsterna) fase.getFonteMateriaPrima();
                    System.out.println("  • " + fonte.getNomeFornitore());
                });

        System.out.println("\n=== MIGLIORAMENTI COMPLETATI CON SUCCESSO! ===");
        System.out.println("✅ Preservazione oggetti FonteMateriaPrima nei DTO");
        System.out.println("✅ Distinzione automatica tra fonti interne ed esterne");
        System.out.println("✅ Accesso diretto agli ID dei produttori interni");
        System.out.println("✅ Tracciabilità completa mantenuta");
        System.out.println("✅ Funzionalità avanzate per analisi e reporting");
    }
}