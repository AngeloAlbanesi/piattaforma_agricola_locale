# Piattaforma di Digitalizzazione e Valorizzazione della Filiera Agricola Locale

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()

## Indice

- [Descrizione del Progetto](#descrizione-del-progetto)
- [Funzionalita Principali](#funzionalita-principali)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Attori del Sistema](#attori-del-sistema)
- [Installazione e Avvio](#installazione-e-avvio)
- [Come Usare il Progetto](#come-usare-il-progetto)
- [Architettura e Design Pattern](#architettura-e-design-pattern)
- [Come Contribuire](#come-contribuire)
- [Crediti](#crediti)
- [Licenza](#licenza)

## Descrizione del Progetto

### Cosa fa il progetto?

La **Piattaforma di Digitalizzazione e Valorizzazione della Filiera Agricola Locale** è un sistema web completo che permette la gestione, valorizzazione e tracciabilita dei prodotti agricoli di un territorio comunale. La piattaforma facilita la connessione tra tutti gli attori della filiera agricola locale, dalla produzione alla vendita finale.

### Perché è stato creato?

Il progetto nasce dall'esigenza di:

- **Promuovere il territorio** e i suoi prodotti tipici
- **Garantire la tracciabilita** completa dei prodotti agricoli
- **Facilitare la commercializzazione** diretta tra produttori e consumatori
- **Valorizzare le tradizioni locali** e i metodi di produzione sostenibili
- **Creare una rete** tra tutti gli attori della filiera agricola

### Quali tecnologie sono state usate e perché?

- **Java 21**: Linguaggio principale per performance e robustezza
- **Spring Boot 3.4.6**: Framework per sviluppo rapido e configurazione automatica
- **Spring Security**: Gestione sicurezza e autenticazione con JWT
- **Spring Data JPA**: Persistenza dati e gestione database
- **H2 Database**: Database per sviluppo
- **MapStruct**: Mapping automatico tra DTO e entita
- **Lombok**: Riduzione boilerplate code
- **Maven**: Gestione dipendenze e build automation

### Sfide affrontate e funzionalita future

**Sfide principali:**

- Implementazione di un sistema di tracciabilita completo
- Gestione di ruoli e permessi complessi

## Funzionalita Principali

### E-commerce e Marketplace

- Vendita diretta di prodotti agricoli
- Gestione carrello e ordini
- Sistema di pagamento integrato
- Creazione di pacchetti prodotto personalizzati

### Tracciabilita e Geolocalizzazione

- Tracciamento completo della filiera produttiva
- Visualizzazione su mappa interattiva
- Collegamento tra fasi di produzione e trasformazione
- Certificazioni di qualita e origine

### Gestione Multi-Ruolo

- Sistema di autenticazione e autorizzazione avanzato
- Gestione permessi granulari per ogni tipologia di utente
- Workflow di approvazione contenuti

### Processi di Trasformazione

- Documentazione completa dei processi produttivi
- Collegamento tra materie prime e prodotti finiti
- Gestione fasi di lavorazione

### Condivisione Social

- Promozione eventi e prodotti

### Gestione Eventi

- Organizzazione fiere e mercati locali
- Prenotazione ad eventi

## Tecnologie Utilizzate

| Categoria | Tecnologia | Versione | Scopo |
|-----------|------------|----------|-------|
| **Backend** | Java | 21 | Linguaggio principale |
| **Framework** | Spring Boot | 3.4.6 | Framework applicativo |
| **Sicurezza** | Spring Security | - | Autenticazione e autorizzazione |
| **Database** | Spring Data JPA | - | Persistenza dati |
| **Database** | H2 Database | - | Database in-memory |
| **Mapping** | MapStruct | 1.6.3 | Mapping DTO-Entity |
| **Utility** | Lombok | - | Riduzione boilerplate |
| **Auth** | JWT | 0.12.6 | Token-based authentication |
| **Build** | Maven | - | Gestione dipendenze |

## Attori del Sistema

### Attori Principali

| Ruolo | Descrizione | Funzionalita |
|-------|-------------|--------------|
| **Produttore** | Agricoltori e allevatori locali | Caricamento prodotti, gestione certificazioni, vendita diretta |
| **Trasformatore** | Aziende di trasformazione | Gestione processi, collegamento filiera, vendita prodotti trasformati |
| **Distributore** | Negozi e rivenditori locali | Vendita prodotti, creazione pacchetti, gestione inventario |
| **Curatore** | Responsabile qualita contenuti | Verifica e approvazione contenuti, controllo qualita |
| **Animatore** | Organizzatore eventi | Creazione eventi, gestione fiere, coordinamento visite |
| **Acquirente** | Consumatori finali | Acquisto prodotti, prenotazione eventi, tracciabilita |
| **Utente Generico** | Visitatori piattaforma | Consultazione informazioni, esplorazione territorio |
| **Gestore Piattaforma** | Amministratore sistema | Gestione utenti, configurazioni, monitoraggio |

### Sistemi Esterni

- **Sistema OSM**: Mappe
- **Sistemi Social**: Condivisione contenuti

## Installazione e Avvio

### Prerequisiti

- **Java 21** o superiore
- **Maven 3.6+**
- **Git**

### Installazione

1. **Clona il repository**

```bash
git clone https://github.com/your-username/piattaforma-agricola-locale.git
cd piattaforma-agricola-locale
```

2. **Installa le dipendenze**

```bash
./mvnw clean install
```

3. **Configura il database** (opzionale)

```bash
# Il progetto usa H2 in-memory di default
# Per configurazione personalizzata, modifica src/main/resources/application.properties
```

4. **Avvia l'applicazione**

```bash
./mvnw spring-boot:run
```

5. **Accedi all'applicazione**

```
http://localhost:8080
```

### Configurazione Rapida

```properties
# application.properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.jpa.show-sql=true
```

## Come Usare il Progetto

### Strumenti per Testare le API

Per testare le API REST del progetto, puoi utilizzare:

1. **Swagger UI** (Raccomandato)
   - Accedi a `http://localhost:8080/swagger-ui.html`
   - Interfaccia interattiva per testare tutte le API
   - Documentazione automatica degli endpoint

2. **Postman**
   - Importa la collection delle API
   - Testa gli endpoint con richieste personalizzate
   - Gestione dell'autenticazione JWT

### Registrazione e Autenticazione

#### Registrazione Utente

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "mario.rossi",
  "email": "mario.rossi@email.com",
  "password": "password123",
  "nome": "Mario",
  "cognome": "Rossi",
  "ruolo": "PRODUTTORE",
  "telefono": "+39 123 456 7890",
  "indirizzo": "Via Roma 1, 12345 Citta"
}
```

#### Login

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "mario.rossi",
  "password": "password123"
}
```

### Utilizzo del Token JWT

Per le API protette, includi il token nell'header:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Esempi di API per Produttore

#### Aggiungere un Prodotto

```bash
POST http://localhost:8080/api/prodotti
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "nome": "Pomodori Bio",
  "descrizione": "Pomodori biologici coltivati localmente",
  "prezzo": 3.50,
  "quantitaDisponibile": 100,
  "categoria": "VERDURA",
  "metodoColtivazione": {
    "tipo": "BIOLOGICO",
    "descrizione": "Coltivazione senza pesticidi"
  },
  "certificazioni": ["BIO", "KM0"]
}
```

#### Creare un Processo di Trasformazione

```bash
POST http://localhost:8080/api/processi
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "nome": "Produzione Passata di Pomodoro",
  "descrizione": "Trasformazione pomodori freschi in passata",
  "fasi": [
    {
      "nome": "Lavaggio",
      "descrizione": "Lavaggio accurato dei pomodori",
      "ordine": 1
    },
    {
      "nome": "Cottura",
      "descrizione": "Cottura a bassa temperatura",
      "ordine": 2
    }
  ]
}
```

### Console H2 (Sviluppo)

Accedi al database H2 per debugging:

```
# Database configuration (H2 in-memory for development)
spring.datasource.url=jdbc:h2:file:./data/piattaforma_agricola
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
```

## Architettura e Design Pattern

### Architettura a Livelli

```
┌─────────────────┐
│   Presentation  │ ←  REST Controllers
├─────────────────┤
│    Service      │ ← Business Logic, Transaction Management
├─────────────────┤
│   Repository    │ ← Data Access Layer, JPA Repositories
├─────────────────┤
│     Model       │ ← Domain Entities, DTOs
└─────────────────┘
```

## Design Pattern Implementati

Questo progetto implementa numerosi design pattern enterprise per garantire un'architettura robusta, scalabile e manutenibile. Ogni pattern è stato scelto per risolvere problemi specifici dell'architettura e migliorare la qualità del codice.

### 1. Factory Method Pattern ⭐⭐⭐

**Implementazione**: Gestione creazione utenti con validazione e type safety

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/service/factory/`

**Problema risolto**: La creazione di utenti di diversi tipi (Produttore, Trasformatore, Distributore, etc.) richiede validazioni specifiche, configurazioni diverse e gestione di dati aziendali opzionali. Un approccio diretto porterebbe a duplicazione di codice e logica di validazione sparsa.

**Struttura Implementata**:

```java
// Factory Interface - Definisce metodi type-safe
public interface UtenteFactory {
    Acquirente creaAcquirente(String nome, String cognome, String email, 
                              String passwordHash, String numeroTelefono);
    
    Produttore creaProduttore(String nome, String cognome, String email, 
                              String passwordHash, String numeroTelefono, 
                              DatiAzienda datiAzienda);
    
    // Metodo generico per creazione runtime
    Utente creaUtente(TipoRuolo tipoRuolo, String nome, String cognome, 
                      String email, String passwordHash, String numeroTelefono, 
                      DatiAzienda datiAzienda);
}

// Abstract Factory - Validazioni comuni
public abstract class AbstractUtenteFactory implements UtenteFactory {
    protected void validateUserData(String nome, String cognome, String email, String passwordHash) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Formato email non valido");
        }
    }
    
    protected boolean requiresDatiAzienda(TipoRuolo tipoRuolo) {
        return tipoRuolo == TipoRuolo.PRODUTTORE || 
               tipoRuolo == TipoRuolo.TRASFORMATORE || 
               tipoRuolo == TipoRuolo.DISTRIBUTORE_DI_TIPICITA;
    }
}
```

**Concrete Factory con Spring Integration**:

```java
@Component
@Primary
public class SpringReadyUtenteFactory extends AbstractUtenteFactory {
    private final IUtenteBaseRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Utente creaUtente(TipoRuolo tipoRuolo, String nome, String cognome, 
                             String email, String password, String numeroTelefono, 
                             DatiAzienda datiAzienda) {
        validateUserData(nome, cognome, email, password);
        
        // Encoding automatico della password
        String passwordHash = passwordEncoder.encode(password);
        
        // Creazione dinamica basata sul tipo
        switch (tipoRuolo) {
            case PRODUTTORE:
                validateDatiAzienda(datiAzienda);
                return new Produttore(nome, cognome, email, passwordHash, 
                                      numeroTelefono, datiAzienda, tipoRuolo);
            case ACQUIRENTE:
                return new Acquirente(nome, cognome, email, passwordHash, 
                                      numeroTelefono, tipoRuolo);
            // Altri casi...
        }
    }
}
```

**Rationale**:

- **Type Safety**: Metodi dedicati prevengono errori a compile-time
- **Validazione Centralizzata**: Logica comune in AbstractUtenteFactory
- **Estensibilità**: Facile aggiunta di nuovi tipi utente
- **Integrazione Spring**: Dependency injection ready

### 2. Facade Pattern ⭐⭐⭐

**Implementazione**: Semplificazione di interfacce complesse per subsistemi

**Problema risolto**: I client devono interagire con multiple classi repository e service per operazioni complesse. Senza facade, il codice client diventa accoppiato a implementazioni interne.

#### 2.1 AcquistabileService Facade

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/service/impl/AcquistabileService.java`

```java
@Service
public class AcquistabileService implements IAcquistabileService {
    
    // Nasconde la complessità di 3 repository diversi
    private final IProdottoRepository prodottoRepository;
    private final IPacchettoRepository pacchettoRepository;
    private final IEventoRepository eventoRepository;
    
    // Interfaccia unificata per tutti i tipi acquistabili
    @Override
    public Acquistabile findByTipoAndId(TipoAcquistabile tipo, Long id) {
        switch (tipo) {
            case PRODOTTO:
                return prodottoRepository.findById(id).orElse(null);
            case PACCHETTO:
                return pacchettoRepository.findById(id).orElse(null);
            case EVENTO:
                return eventoRepository.findById(id).orElse(null);
            default:
                throw new IllegalArgumentException("Tipo acquistabile non supportato: " + tipo);
        }
    }
}
```

#### 2.2 OrdineService Facade

```java
@Service
public class OrdineService implements IOrdineService, IOrdineObservable {
    
    // Coordina multiple dependencies
    private final IOrdineRepository ordineRepository;
    private final IRigaOrdineRepository rigaOrdineRepository;
    private final CarrelloService carrelloService;
    private final List<IVenditoreObserver> observers;
    
    // Facade per operazione complessa di creazione ordini
    @Override
    public List<Ordine> creaOrdiniDaCarrello(Acquirente acquirente) {
        // 1. Recupera carrello
        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        
        // 2. Raggruppa per venditore
        Map<Venditore, List<ElementoCarrello>> elementiPerVenditore = raggruppaPer Venditore(carrello);
        
        // 3. Crea ordini separati
        List<Ordine> ordiniCreati = new ArrayList<>();
        for (Map.Entry<Venditore, List<ElementoCarrello>> entry : elementiPerVenditore.entrySet()) {
            Ordine ordine = new Ordine(dataOrdine, acquirente, entry.getKey());
            ordineRepository.save(ordine);
            ordiniCreati.add(ordine);
        }
        
        // 4. Pulisce carrello
        carrelloService.svuotaCarrello(acquirente);
        
        return ordiniCreati;
    }
}
```

**Rationale**:

- **Semplificazione**: Client usano interfaccia semplice invece di multiple classi
- **Disaccoppiamento**: Nasconde implementazioni interne
- **Coordinamento**: Gestisce interazioni tra subsistemi

### 3. Strategy Pattern ⭐⭐⭐

**Implementazione**: Sistema di pagamento modulare e estensibile

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/service/pagamento/`

**Problema risolto**: Il sistema deve supportare diversi metodi di pagamento senza modificare il codice esistente quando si aggiungono nuovi metodi.

**Struttura**:

```java
// Strategy Interface
public interface IMetodoPagamentoStrategy {
    boolean elaboraPagamento(Ordine ordine) throws PagamentoException;
    boolean elaboraPagamento(Ordine ordine, PagamentoRequestDTO datiPagamento) throws PagamentoException;
}

// Concrete Strategy - Carta di Credito
public class PagamentoCartaCreditoStrategy implements IMetodoPagamentoStrategy {
    
    @Override
    public boolean elaboraPagamento(Ordine ordine, PagamentoRequestDTO datiPagamento) {
        DatiCartaCreditoDTO datiCarta = datiPagamento.getDatiCartaCredito();
        
        // Validazione specifica per carta di credito
        if (!validaDatiCarta(datiCarta)) {
            return false;
        }
        
        // Simulazione comunicazione con gateway
        return simulaAutorizzazionePagamento(datiCarta, ordine.getImportoTotale());
    }
    
    private boolean validaDatiCarta(DatiCartaCreditoDTO datiCarta) {
        // Validazione numero carta (13-19 cifre)
        if (!datiCarta.getNumeroCartaCredito().matches("^[0-9]{13,19}$")) {
            return false;
        }
        // Validazione data scadenza
        return validaDataScadenza(datiCarta.getDataScadenza());
    }
}

// Context - Usage nel Controller
@RestController
public class OrdineController {
    
    private IMetodoPagamentoStrategy getPaymentStrategy(String metodoPagamento) {
        switch (metodoPagamento.toUpperCase()) {
            case "CARTA_CREDITO":
                return new PagamentoCartaCreditoStrategy();
            case "PAYPAL":
                return new PagamentoPayPalStrategy();
            default:
                throw new IllegalArgumentException("Metodo non supportato: " + metodoPagamento);
        }
    }
}
```

**Rationale**:

- **Estensibilità**: Nuovi metodi di pagamento senza modificare codice esistente
- **Runtime Selection**: Scelta del metodo a runtime
- **Single Responsibility**: Ogni strategia gestisce un solo tipo di pagamento

### 4. State Pattern ⭐⭐⭐

**Implementazione**: Gestione stati ordine con transizioni controllate

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/model/ordine/stateOrdine/`

**Problema risolto**: Gli ordini hanno stati complessi con transizioni specifiche permesse. Un approccio if/else porterebbe a codice difficile da mantenere.

**Struttura**:

```java
// State Interface
public interface IStatoOrdine {
    void processaOrdine(Ordine ordine);
    void spedisciOrdine(Ordine ordine);
    void annullaOrdine(Ordine ordine);
    void consegnaOrdine(Ordine ordine);
    StatoCorrente getStatoCorrente();
    
    // Template method per transizioni
    default void cambiaStato(Ordine ordine, IStatoOrdine nuovoStato) {
        ordine.setStato(nuovoStato);
        ordine.setStatoCorrente(nuovoStato.getStatoCorrente());
    }
}

// Context - Ordine Entity
@Entity
public class Ordine {
    @Enumerated(EnumType.STRING)
    private StatoCorrente statoCorrente;
    
    @Transient
    private IStatoOrdine stato;
    
    // Delegazione allo stato corrente
    public void processa() {
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        stato.processaOrdine(this);
    }
    
    public void paga() {
        processa(); // In "AttesaPagamento", processa() esegue il pagamento
    }
}
```

**Concrete States**:

```java
// Stato: In Attesa di Pagamento
public class StatoOrdineNuovoInAttesaDiPagamento implements IStatoOrdine {
    
    @Override
    public void processaOrdine(Ordine ordine) {
        // Transizione permessa: pagamento -> pronto per lavorazione
        IStatoOrdine nuovoStato = new StatoOrdinePagatoProntoPerLavorazione();
        cambiaStato(ordine, nuovoStato);
    }
    
    @Override
    public void spedisciOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("Non è possibile spedire un ordine non pagato");
    }
    
    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.ATTESA_PAGAMENTO;
    }
}
```

**Rationale**:

- **Controllo Transizioni**: Ogni stato definisce transizioni permesse
- **Eliminazione Condizionali**: No più if/else complessi
- **Estensibilità**: Facile aggiunta di nuovi stati

### 5. Observer Pattern ⭐⭐⭐

**Implementazione**: Sistema di notifiche per eventi asincroni

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/service/observer/`

**Problema risolto**: Quando avvengono eventi importanti (creazione ordini, nuovi prodotti), multiple parti del sistema devono essere notificate senza creare accoppiamento diretto.

#### 5.1 Observer per Ordini

```java
// Observable Interface
public interface IOrdineObservable {
    void aggiungiObserver(IVenditoreObserver observer);
    void rimuoviObserver(IVenditoreObserver observer);
    void notificaObservers(Ordine ordine, Venditore venditoreSpecifico);
}

// Observer Interface
public interface IVenditoreObserver {
    void update(Ordine ordine, List<RigaOrdine> righeDiCompetenza);
}

// Concrete Observable
@Service
public class OrdineService implements IOrdineService, IOrdineObservable {
    
    private final List<IVenditoreObserver> observers = new ArrayList<>();
    
    @Override
    public void notificaObservers(Ordine ordine, Venditore venditoreSpecifico) {
        // Raggruppa righe per venditore
        Map<Venditore, List<RigaOrdine>> righePeerVenditore = raggruppaPer Venditore(ordine);
        
        // Notifica ogni venditore con le sue righe
        for (Map.Entry<Venditore, List<RigaOrdine>> entry : righePeerVenditore.entrySet()) {
            List<RigaOrdine> righeDiCompetenza = entry.getValue();
            
            observers.forEach(obs -> {
                try {
                    obs.update(ordine, righeDiCompetenza);
                } catch (Exception e) {
                    logger.error("Errore notifica observer", e);
                }
            });
        }
    }
    
    // Trigger della notifica
    public void confermaPagamento(Ordine ordine, IMetodoPagamentoStrategy strategiaPagamento) {
        boolean successo = strategiaPagamento.elaboraPagamento(ordine);
        
        if (successo) {
            ordine.paga(); // State transition
            ordineRepository.save(ordine);
            
            // Notifica tutti gli observer interessati
            notificaObservers(ordine, null);
        }
    }
}

// Concrete Observer
@Service
public class VenditoreObserverService implements IVenditoreObserver {
    
    @Override
    public void update(Ordine ordine, List<RigaOrdine> righeDiCompetenza) {
        for (RigaOrdine riga : righeDiCompetenza) {
            Acquistabile acquistabile = riga.getAcquistabile();
            int quantitaOrdinata = riga.getQuantitaOrdinata();
            
            // Decrementa inventario
            if (acquistabile instanceof Prodotto) {
                Prodotto prodotto = (Prodotto) acquistabile;
                int nuovaQuantita = prodotto.getQuantitaDisponibile() - quantitaOrdinata;
                prodottoService.aggiornaQuantitaDisponibile(prodotto.getId(), nuovaQuantita);
            }
        }
    }
}
```

**Configurazione Automatica Spring**:

```java
@Configuration
public class ObserverConfig {
    
    @PostConstruct
    public void registerObservers() {
        // Registrazione automatica degli observer
        ordineService.aggiungiObserver(venditoreObserverService);
        prodottoService.aggiungiObserver(curatoreObserverService);
        
        logger.info("Observer registrati con successo");
    }
}
```

**Rationale**:

- **Disaccoppiamento**: Oggetti osservati non conoscono i loro observer
- **Estensibilità**: Facile aggiunta di nuovi observer
- **Reattività**: Notifiche immediate su eventi importanti

### 6. DTO Pattern ⭐⭐⭐

**Implementazione**: Oggetti per trasferimento dati tra layer

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/dto/`

**Problema risolto**: Evitare esposizione di entità interne, controllo dei dati trasferiti, validazione input.

**Struttura Organizzata per Dominio**:

```java
// DTO per richieste di creazione
public class CreateProductRequestDTO {
    
    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    @Size(min = 3, max = 100, message = "Il nome deve essere tra 3 e 100 caratteri")
    private String nome;
    
    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
    @DecimalMax(value = "9999.99", message = "Il prezzo non può superare €9999.99")
    private Double prezzo;
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 0, message = "La quantità non può essere negativa")
    private Integer quantitaDisponibile;
    
    @Valid
    private MetodoDiColtivazioneDTO metodoColtivazione;
    
    private List<@Valid CreateCertificazioneRequestDTO> certificazioni;
}

// DTO per response dettagliate
public class ProductDetailDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private Double prezzo;
    private StatoVerificaValori statoVerifica;
    
    // Informazioni del venditore (nested DTO)
    private UserPublicDTO venditore;
    
    // Certificazioni
    private List<CertificazioneDTO> certificazioni;
    
    // Tracciabilità (se disponibile)
    private TraceabilityDTO tracciabilita;
    
    // Timestamp
    private LocalDateTime dataCreazione;
}
```

**Mapping con MapStruct**:

```java
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UtenteMapper.class, CertificazioneMapper.class}
)
@Component
public abstract class ProdottoMapper {
    
    // Mapping da Entity a DTO
    @Mapping(target = "idVenditore", source = "venditore.idUtente")
    @Mapping(target = "nomeVenditore", source = "venditore.nome")
    public abstract ProductSummaryDTO toSummaryDTO(Prodotto prodotto);
    
    @Mapping(target = "tracciabilita", source = "processoTrasformazione", qualifiedByName = "mapToTraceability")
    public abstract ProductDetailDTO toDetailDTO(Prodotto prodotto);
    
    // Mapping da DTO a Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statoVerifica", constant = "IN_REVISIONE")
    @Mapping(target = "dataCreazione", expression = "java(java.time.LocalDateTime.now())")
    public abstract Prodotto toEntity(CreateProductRequestDTO dto);
}
```

**Rationale**:

- **Sicurezza**: Non espone entità interne
- **Validazione**: Validazione centralizzata input
- **Versioning**: Supporto per diverse versioni API
- **Performance**: Controllo dati trasferiti

### 9. Aspect-Oriented Programming (AOP) ⭐⭐

**Implementazione**: Cross-cutting concerns con Spring AOP

**Ubicazione**: `src/main/java/it/unicam/cs/ids/piattaforma_agricola_locale/security/AccreditamentoAspect.java`

**Problema risolto**: Gestire funzionalità trasversali (security, logging, caching) senza duplicare codice.

**Implementazione**:

```java
// Custom Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAccreditation {
    TipoRuolo[] roles() default {};
}

// Aspect Implementation
@Aspect
@Component
@RequiredArgsConstructor
public class AccreditamentoAspect {
    
    private final IUtenteService utenteService;
    
    @Before("@annotation(requiresAccreditation)")
    public void checkAccreditation(JoinPoint joinPoint, RequiresAccreditation requiresAccreditation) {
        
        // Ottieni l'utente corrente dal SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        // Verifica accreditamento
        Optional<Utente> utenteOpt = utenteService.findByEmail(userEmail);
        if (utenteOpt.isEmpty()) {
            throw new UtenteNonAccreditatoException("Utente non trovato: " + userEmail);
        }
        
        Utente utente = utenteOpt.get();
        
        // Verifica se è un venditore e se è accreditato
        if (utente instanceof Venditore) {
            Venditore venditore = (Venditore) utente;
            if (venditore.getStatoAccreditamento() != StatoAccreditamento.APPROVED) {
                throw new UtenteNonAccreditatoException(
                    "Venditore non accreditato. Stato attuale: " + venditore.getStatoAccreditamento()
                );
            }
        }
        
        // Verifica ruoli se specificati
        TipoRuolo[] requiredRoles = requiresAccreditation.roles();
        if (requiredRoles.length > 0) {
            boolean hasRequiredRole = Arrays.asList(requiredRoles).contains(utente.getTipoRuolo());
            if (!hasRequiredRole) {
                throw new AccessDeniedException("Ruolo non autorizzato per questa operazione");
            }
        }
    }
}

// Usage negli endpoints
@RestController
public class ProdottoController {
    
    @PostMapping
    @RequiresAccreditation(roles = {TipoRuolo.PRODUTTORE, TipoRuolo.TRASFORMATORE})
    public ResponseEntity<ProductDetailDTO> creaProdotto(@RequestBody CreateProductRequestDTO request) {
        // Metodo eseguito solo se l'utente è accreditato e ha il ruolo corretto
        // La verifica avviene automaticamente tramite l'aspect
    }
}
```

**Rationale**:

- **Separation of Concerns**: Logica di sicurezza separata dalla business logic
- **Reusability**: Aspect riutilizzabile su tutti i metodi
- **Maintainability**: Centralizzazione della logica di sicurezza
- **Clean Code**: Controller focalizzati sulla business logic

## Valutazione Complessiva

### Punti di Forza

1. **Implementazione Professionale**: Pattern complessi come State, Observer e Factory sono implementati correttamente seguendo le best practice
2. **Integrazione Spring**: Eccellente integrazione con il framework Spring Boot, sfruttando dependency injection e AOP
3. **Separazione delle Responsabilità**: Chiara divisione tra presentation, business logic e data access layer
4. **Type Safety**: Uso di generics e interfacce specifiche per prevenire errori a compile-time
5. **Estensibilità**: Architettura preparata per future estensioni senza modifiche al codice esistente

### Benefici Architetturali

- **Manutenibilità**: Codice ben organizzato e facile da modificare
- **Testabilità**: Pattern come Dependency Injection facilitano unit testing
- **Scalabilità**: Architettura pronta per crescere con nuove funzionalità
- **Robustezza**: Gestione degli errori centralizzata e pattern di validazione

### Principi SOLID

Il progetto segue i principi SOLID per garantire:

- **S**ingle Responsibility Principle
- **O**pen/Closed Principle
- **L**iskov Substitution Principle
- **I**nterface Segregation Principle
- **D**ependency Inversion Principle

## Come Contribuire

### Processo di Contribuzione

1. **Fork** del repository
2. **Crea** un branch per la feature (`git checkout -b feature/nuova-funzionalita`)
3. **Commit** delle modifiche (`git commit -m 'Aggiunge nuova funzionalita'`)
4. **Push** del branch (`git push origin feature/nuova-funzionalita`)
5. **Apri** una Pull Request

### Linee Guida

- Segui le convenzioni di codice Java
- Scrivi test per ogni nuova funzionalita
- Documenta le API con JavaDoc
- Usa commit message descrittivi
- Aggiorna la documentazione se necessario

### Aree di Contribuzione

- **Bug Fix**: Correzione errori
- **Nuove Funzionalita**: Implementazione features
- **Documentazione**: Miglioramento docs
- **UI/UX**: Miglioramento interfaccia
- **Performance**: Ottimizzazioni
- **Sicurezza**: Miglioramenti security

## Crediti

### Sviluppatori

- **Team di Sviluppo** - Universita di Camerino
- **Corso**: Ingegneria del Software (IDS)
- **Sviluppatori**:
- - [Angelo Albanesi](https://github.com/angeloalbanesi)
- - [Paolo Campanari](https://github.com/PaoloCampanari)
- - [Lorenzo Donadio](https://github.com/Lor3Don4)

### Risorse e Riferimenti

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://spring.io/projects/spring-security)
- [OpenStreetMap API](https://wiki.openstreetmap.org/wiki/API)

### Librerie Open Source

Ringraziamo tutti i maintainer delle librerie utilizzate:

- Spring Framework Team
- Hibernate Team
- MapStruct Contributors
- Lombok Project

## Licenza

Questo progetto e rilasciato sotto la **Licenza MIT**.

```
MIT License

Copyright (c) 2024 Piattaforma Agricola Locale

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
