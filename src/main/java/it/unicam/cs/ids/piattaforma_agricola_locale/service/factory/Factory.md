# Factory Method Pattern per la Gestione Utenti

## Panoramica

Questo package implementa il pattern Factory Method per la creazione di utenti nella piattaforma di digitalizzazione e valorizzazione della filiera agricola locale. Il pattern è stato progettato per supportare la creazione di diversi tipi di utenti in modo flessibile, estensibile e type-safe, con la preparazione per una futura migrazione a Spring Boot.

## Struttura del Pattern

### Componenti Principali

1. **Interfaccia Factory (`UtenteFactory`)**: Definisce i metodi type-safe per la creazione di tutti i tipi di utenti.
2. **Classe Astratta (`AbstractUtenteFactory`)**: Implementa funzionalità comuni e validazioni condivise tra le factory concrete.
3. **Factory Concrete**:
   - `SimpleUtenteFactory`: Implementazione standard per l'ambiente attuale.
   - `SpringReadyUtenteFactory`: Implementazione predisposta per l'integrazione con Spring Boot.
4. **Provider (`UtenteFactoryProvider`)**: Implementa il pattern Singleton e fornisce un punto di accesso centralizzato per ottenere le factory.

### Diagramma delle Classi

```
                  ┌─────────────────┐
                  │  UtenteFactory  │
                  │    (Interface)  │
                  └────────┬────────┘
                           │
                           │
              ┌────────────▼───────────┐
              │  AbstractUtenteFactory │
              │    (Abstract Class)    │
              └────────────┬───────────┘
                           │
                 ┌─────────┴──────────┐
                 │                    │
    ┌────────────▼────────┐  ┌────────▼─────────────┐
    │ SimpleUtenteFactory │  │ SpringReadyUtenteFactory │
    │   (Concrete Class)  │  │    (Concrete Class)      │
    └─────────────────────┘  └────────────────────────┘
                 ▲
                 │
    ┌────────────┴────────────┐
    │   UtenteFactoryProvider │
    │      (Singleton)        │
    └─────────────────────────┘
```

## Flusso di Creazione Utente

1. **Richiesta di Creazione**: Il client richiede la creazione di un utente attraverso il `UtenteFactoryProvider`.
2. **Ottenimento della Factory**: Il provider restituisce l'implementazione appropriata della factory.
3. **Validazione Dati**: La factory valida i dati dell'utente (nome, email, password, ecc.).
4. **Creazione Istanza**: Viene creata l'istanza dell'utente del tipo specifico richiesto.
5. **Persistenza**: L'utente viene salvato nel repository.
6. **Restituzione**: L'istanza dell'utente creata e persistita viene restituita al client.

### Esempio Dettagliato del Flusso di Creazione

Di seguito è illustrato un esempio dettagliato del flusso di creazione di un utente di tipo Produttore:

#### 1. Richiesta di Creazione

Il processo inizia quando un client (ad esempio, un controller o un servizio) necessita di creare un nuovo utente Produttore:

```java
// Dati dell'utente ricevuti dalla richiesta di registrazione
String nome = "Marco";
String cognome = "Bianchi";
String email = "marco.bianchi@example.com";
String password = "Password123!";
String passwordHash = hashPassword(password); // Funzione di hashing della password
String numeroTelefono = "3331234567";

// Dati dell'azienda
DatiAzienda datiAzienda = new DatiAzienda(
    null, // L'ID verrà assegnato automaticamente
    "Azienda Agricola Bianchi",
    "12345678901", // Partita IVA
    "Via dei Campi 42, Ancona",
    "Azienda specializzata in prodotti biologici locali",
    "logo_bianchi.png",
    "www.aziendabianchi.it"
);
```

#### 2. Ottenimento della Factory

Il client ottiene un'istanza della factory attraverso il provider:

```java
// Ottenere l'istanza del provider (Singleton)
UtenteFactoryProvider provider = UtenteFactoryProvider.getInstance();

// Ottenere la factory appropriata
UtenteFactory factory = provider.getUtenteFactory();
```

#### 3. Validazione Dati

Quando il client chiama il metodo `creaProduttore()`, la factory esegue la validazione dei dati:

```java
// Il client richiede la creazione di un Produttore
try {
    Produttore produttore = factory.creaProduttore(
        nome,
        cognome,
        email,
        passwordHash,
        numeroTelefono,
        datiAzienda
    );
    
    // All'interno del metodo creaProduttore, avviene la validazione:
    // 1. validateUserData(nome, cognome, email, passwordHash);
    //    - Verifica che nome, cognome, email non siano vuoti
    //    - Verifica che l'email abbia un formato valido
    //    - Verifica che la password non sia vuota
    //    - Verifica che l'email non sia già in uso nel sistema
    //
    // 2. validateDatiAzienda(datiAzienda);
    //    - Verifica che datiAzienda non sia null
    //    - Verifica che il nome dell'azienda non sia vuoto
    //    - Verifica che la partita IVA non sia vuota
    //    - Verifica che la partita IVA abbia un formato valido (11 cifre)
    
    // Se la validazione fallisce, viene lanciata un'eccezione IllegalArgumentException
} catch (IllegalArgumentException e) {
    // Gestione dell'errore di validazione
    System.err.println("Errore di validazione: " + e.getMessage());
    // Qui si potrebbe restituire un errore al client o registrare l'errore
}
```

#### 4. Creazione Istanza

Se la validazione ha successo, la factory crea l'istanza dell'utente:

```java
// All'interno del metodo creaProduttore della SimpleUtenteFactory:

// Creazione dell'istanza di Produttore
Produttore produttore = new Produttore(
    nome, 
    cognome, 
    email, 
    passwordHash, 
    numeroTelefono, 
    datiAzienda, 
    TipoRuolo.PRODUTTORE
);

// Durante la creazione:
// - Viene assegnato il ruolo PRODUTTORE
// - L'utente viene impostato come attivo (isAttivo = true)
// - L'ID utente viene temporaneamente assegnato (sarà sovrascritto dal repository)
```

#### 5. Persistenza

La factory salva l'utente nel repository:

```java
// All'interno del metodo creaProduttore della SimpleUtenteFactory:

// Salvataggio nel repository
Produttore produttoreSalvato = (Produttore) utenteRepository.save(produttore);

// Nel metodo save del UtenteBaseRepository:
// 1. Se l'ID dell'utente è 0, viene assegnato un nuovo ID incrementale
// 2. L'utente viene memorizzato nella mappa interna con chiave l'ID
// 3. L'utente viene restituito
```

#### 6. Restituzione

Infine, l'istanza dell'utente creata e persistita viene restituita al client:

```java
// Il client riceve l'istanza del Produttore creato e persistito
Produttore nuovoProduttore = factory.creaProduttore(
    nome,
    cognome,
    email,
    passwordHash,
    numeroTelefono,
    datiAzienda
);

// A questo punto, nuovoProduttore ha:
// - Un ID assegnato dal repository
// - Tutti i dati forniti durante la creazione
// - Lo stato attivo
// - Il ruolo PRODUTTORE

// Il client può ora utilizzare l'istanza dell'utente
System.out.println("Produttore creato con ID: " + nuovoProduttore.getId());
```

#### Gestione degli Errori

Durante il flusso, possono verificarsi diversi errori:

1. **Errori di validazione dei dati utente**:
   - Nome o cognome vuoti
   - Email non valida o già in uso
   - Password vuota

2. **Errori di validazione dei dati azienda**:
   - Nome azienda vuoto
   - Partita IVA non valida o vuota

3. **Errori di persistenza**:
   - Problemi di connessione al database (in un'implementazione reale)
   - Violazioni di vincoli di unicità

Tutti questi errori sono gestiti tramite eccezioni che possono essere catturate e gestite dal client.

## Dettagli di Implementazione

### UtenteFactory (Interfaccia)

Definisce i metodi type-safe per la creazione di tutti i tipi di utenti supportati:
- `creaAcquirente`: Crea un utente generico (Acquirente)
- `creaProduttore`: Crea un utente Produttore
- `creaTrasformatore`: Crea un utente Trasformatore
- `creaDistributoreDiTipicita`: Crea un utente DistributoreDiTipicita
- `creaCuratore`: Crea un utente Curatore
- `creaAnimatoreDellaFiliera`: Crea un utente AnimatoreDellaFiliera
- `creaGestorePiattaforma`: Crea un utente GestorePiattaforma
- `creaUtente`: Metodo generico per creare un utente in base al tipo specificato a runtime

### AbstractUtenteFactory (Classe Astratta)

Fornisce implementazioni di base e validazioni comuni:
- `validateUserData`: Valida i dati base dell'utente (nome, cognome, email, password)
- `validateDatiAzienda`: Valida i dati dell'azienda (per ruoli che li richiedono)
- `requiresDatiAzienda`: Verifica se un tipo di ruolo richiede dati aziendali
- `isValidEmail`: Verifica se l'email ha un formato valido
- `isValidPartitaIva`: Verifica se la partita IVA ha un formato valido

### SimpleUtenteFactory (Implementazione Concreta)

Implementa tutti i metodi definiti nell'interfaccia `UtenteFactory`:
- Estende la validazione per verificare che l'email non sia già in uso
- Crea istanze dei diversi tipi di utenti
- Salva gli utenti creati nel repository
- Gestisce la logica specifica per ogni tipo di utente

### SpringReadyUtenteFactory (Implementazione Concreta)

Simile a `SimpleUtenteFactory`, ma progettata per essere facilmente convertita in un componente Spring:
- Separazione della logica di validazione dell'email in un metodo dedicato
- Struttura pronta per l'aggiunta di annotazioni Spring (@Component, @Service)
- Preparata per l'iniezione delle dipendenze tramite costruttore

### UtenteFactoryProvider (Singleton)

Fornisce un punto di accesso centralizzato per ottenere le factory:
- Implementa il pattern Singleton
- Gestisce l'istanza del repository
- Offre metodi per ottenere diverse implementazioni di factory:
  - `getUtenteFactory()`: Restituisce una factory standard
  - `getSpringReadyUtenteFactory()`: Restituisce una factory pronta per Spring Boot
  - `getUtenteFactory(IUtenteBaseRepository)`: Restituisce una factory personalizzata con un repository specifico

## Vantaggi dell'Implementazione

1. **Type Safety**: Metodi dedicati per ogni tipo di utente garantiscono la correttezza a compile-time.
2. **Validazione Robusta**: Validazione centralizzata dei dati utente e aziendali.
3. **Estensibilità**: Facile aggiunta di nuovi tipi di utenti o logiche di creazione.
4. **Separazione delle Responsabilità**: Chiara separazione tra interfaccia, logica comune e implementazioni specifiche.
5. **Preparazione per Spring Boot**: Implementazione pronta per la migrazione a Spring Boot.
6. **Integrazione con Repository**: Integrazione diretta con il sistema di persistenza.
7. **Flessibilità**: Supporto per scenari di creazione utente sia type-safe che dinamici.

## Utilizzo

### Esempio Base

```java
// Ottenere la factory dal provider
UtenteFactory factory = UtenteFactoryProvider.getInstance().getUtenteFactory();

// Creare un utente Acquirente
Acquirente acquirente = factory.creaAcquirente(
    "Mario", 
    "Rossi", 
    "mario.rossi@example.com", 
    "password_hash", 
    "1234567890"
);

// Creare un utente Produttore
DatiAzienda datiAzienda = new DatiAzienda(
    null, 
    "Azienda Agricola Rossi", 
    "12345678901", 
    "Via Roma 123", 
    "Azienda agricola biologica", 
    "logo.png", 
    "www.aziendarossi.it"
);

Produttore produttore = factory.creaProduttore(
    "Giuseppe", 
    "Verdi", 
    "giuseppe.verdi@example.com", 
    "password_hash", 
    "0987654321", 
    datiAzienda
);
```

### Utilizzo Dinamico

```java
// Creare un utente in base al tipo specificato a runtime
Utente utente = factory.creaUtente(
    TipoRuolo.TRASFORMATORE,
    "Anna", 
    "Bianchi", 
    "anna.bianchi@example.com", 
    "password_hash", 
    "1122334455", 
    datiAzienda
);
```

## Preparazione per Spring Boot

Per la futura migrazione a Spring Boot, sarà sufficiente:

1. Aggiungere le annotazioni Spring alla classe `SpringReadyUtenteFactory`:
   ```java
   @Service
   public class SpringReadyUtenteFactory extends AbstractUtenteFactory {
       // ...
   }
   ```

2. Modificare il costruttore per l'iniezione delle dipendenze:
   ```java
   @Autowired
   public SpringReadyUtenteFactory(IUtenteBaseRepository utenteRepository) {
       this.utenteRepository = utenteRepository;
   }
   ```

3. Configurare il repository come componente Spring:
   ```java
   @Repository
   public class UtenteBaseRepository implements IUtenteBaseRepository {
       // ...
   }
   ```

## Conclusione

L'implementazione del Factory Method pattern per la gestione utenti fornisce una soluzione flessibile, estensibile e robusta per la creazione di diversi tipi di utenti nella piattaforma. La struttura è progettata per supportare sia l'ambiente attuale che la futura migrazione a Spring Boot, garantendo una transizione fluida e mantenendo la coerenza del codice.