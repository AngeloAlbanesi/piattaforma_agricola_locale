# VenditoreOrderHandlerService - Guida all'uso

## Descrizione

La classe `VenditoreOrderHandlerService` implementa il pattern Observer per gestire automaticamente l'aggiornamento dell'inventario quando vengono creati nuovi ordini.

## Funzionalità

- **Automatizza la gestione dell'inventario**: Quando un ordine viene creato, il servizio decrementa automaticamente le quantità dei prodotti e pacchetti venduti
- **Gestione sicura**: Include validazione dei parametri e gestione delle eccezioni
- **Supporta dependency injection**: Può essere configurato con servizi mock per il testing

## Come registrare il servizio come observer

### Esempio base

```java
// Crea i servizi necessari
ProdottoService prodottoService = new ProdottoService();
PacchettoService pacchettoService = new PacchettoService();
OrdineService ordineService = new OrdineService();

// Crea l'handler
VenditoreOrderHandlerService orderHandler = new VenditoreOrderHandlerService();

// Registra l'handler come observer
ordineService.aggiungiObserver(orderHandler);
```

### Esempio con dependency injection (per testing)

```java
// Crea mock per il testing
ProdottoService mockProdottoService = mock(ProdottoService.class);
PacchettoService mockPacchettoService = mock(PacchettoService.class);

// Crea handler con dependency injection
VenditoreOrderHandlerService orderHandler = new VenditoreOrderHandlerService(
    mockProdottoService, 
    mockPacchettoService
);

// Registra l'handler
ordineService.aggiungiObserver(orderHandler);
```

## Comportamento

Quando viene creato un ordine, l'`OrdineService` notifica tutti gli observer registrati chiamando il metodo `update()`. Il `VenditoreOrderHandlerService` processa ogni `RigaOrdine` e:

1. **Per i Prodotti**: Chiama `ProdottoService.decrementaQuantita(id, quantita)`
2. **Per i Pacchetti**: Chiama `PacchettoService.decrementaQuantita(id, quantita)`
3. **Gestisce errori**: Cattura eventuali eccezioni senza interrompere il processo

## Registrazione multipla

È possibile registrare più istanze del servizio per gestire diversi scenari:

```java
VenditoreOrderHandlerService handler1 = new VenditoreOrderHandlerService();
VenditoreOrderHandlerService handler2 = new VenditoreOrderHandlerService();

ordineService.aggiungiObserver(handler1);
ordineService.aggiungiObserver(handler2);
```

## Rimozione di un observer

```java
ordineService.rimuoviObserver(orderHandler);
```

## Validazioni implementate

- **Ordine non null**: Il metodo update() verifica che l'ordine non sia null
- **Lista righe non null**: Verifica che la lista delle righe di competenza non sia null
- **Righe valide**: Ignora righe null o con acquistabile null
- **Gestione eccezioni**: Cattura eccezioni dai servizi senza propagarle

## Test

La classe include test completi che verificano:

- Costruttori (default e dependency injection)
- Gestione parametri null/invalidi
- Processamento corretto di prodotti e pacchetti
- Gestione delle eccezioni
- Integrazione con OrdineService
- Registrazione multipla di observer
