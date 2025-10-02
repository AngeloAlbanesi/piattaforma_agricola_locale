# Implementazione API Animatore - Frontend

## 📋 Panoramica

Questa implementazione completa integra tutte le API documentate in `docs/API_ANIMATORE.md` nel frontend Angular della piattaforma agricola locale.

## ✅ Funzionalità Implementate

### 1. **Modelli TypeScript** (`core/models/animatore.models.ts`)

Aggiornati i modelli per supportare:

- ✅ `EventoDTO` con stati: `IN_PROGRAMMA`, `IN_CORSO`, `CONCLUSO`, `ANNULLATO`
- ✅ `EventoPartecipanteDTO` - partecipanti utenti agli eventi
- ✅ `AziendaPartecipanteDTO` - aziende partecipanti agli eventi
- ✅ `PromoteRequestDTO` e `ShareResponseDTO` - promozione eventi
- ✅ `CreateEventoRequestDTO` e `AggiornaEventoRequestDTO` - creazione/modifica eventi

### 2. **Servizio API** (`core/services/animatore.service.ts`)

Implementati tutti gli endpoint documentati:

#### Gestione Eventi

- ✅ `POST /api/eventi/creaEvento` - Crea nuovo evento
- ✅ `PUT /api/eventi/{id}` - Aggiorna evento
- ✅ `DELETE /api/eventi/{id}` - Elimina evento
- ✅ `POST /api/eventi/{id}/promote` - Promuovi evento sui social

#### Gestione Partecipanti Utenti

- ✅ `GET /api/eventi/{id}/partecipanti` - Visualizza partecipanti

#### Gestione Stato Eventi

- ✅ `PATCH /api/eventi/{id}/inizia` - Avvia evento (IN_PROGRAMMA → IN_CORSO)
- ✅ `PATCH /api/eventi/{id}/termina` - Termina evento (IN_CORSO → CONCLUSO)
- ✅ `PATCH /api/eventi/{id}/annulla` - Annulla evento

#### Gestione Aziende Partecipanti

- ✅ `POST /api/eventi/{id}/partecipanti-azienda/{venditorId}` - Aggiungi azienda
- ✅ `DELETE /api/eventi/{id}/partecipanti-azienda/{venditorId}` - Rimuovi azienda
- ✅ `GET /api/eventi/{id}/partecipanti-azienda` - Visualizza aziende

#### Metodi Utility

- ✅ `canStartEvent()` - Verifica se evento può essere avviato
- ✅ `canEndEvent()` - Verifica se evento può essere terminato
- ✅ `canCancelEvent()` - Verifica se evento può essere annullato
- ✅ Metodi helper per gestire campi multipli (nomeEvento/titolo, dataOraInizio/dataInizio, etc.)

### 3. **Componenti Dialog**

#### `PromoteEventDialogComponent`

Dialog per promuovere eventi su canali social:

- Selezione canali: Facebook, Twitter, Instagram, Email, WhatsApp
- Campo messaggio promozionale (max 280 caratteri)
- Validazione form
- Design Material UI responsive

#### `ConfirmActionDialogComponent`

Dialog di conferma per azioni sugli eventi:

- Supporta azioni: avvia, termina, annulla
- Mostra dettagli evento
- Warning per azioni irreversibili
- Icone colorate per tipo azione

#### `ManageAziendeDialogComponent`

Dialog per gestire aziende partecipanti:

- Autocomplete per ricerca aziende
- Lista aziende partecipanti con dettagli completi
- Azioni aggiungi/rimuovi azienda
- Visualizzazione certificazioni
- Design card-based responsive

### 4. **Componente Eventi Management** (aggiornato)

#### `EventiManagementComponent`

Tabella gestione eventi con menu azioni completo:

**Menu Azioni:**

- 👁️ Visualizza Dettagli
- ✏️ Modifica
- ▶️ Avvia Evento (solo se IN_PROGRAMMA)
- ⏹️ Termina Evento (solo se IN_CORSO)
- ❌ Annulla Evento (se non CONCLUSO)
- 👥 Partecipanti Utenti
- 🏢 Aziende Partecipanti
- 📢 Promuovi Evento
- 🗑️ Elimina

**Funzionalità:**

- Filtri per stato e ricerca
- Paginazione
- Ordinamento colonne
- Gestione errori con snackbar
- Stati evento colorati con chip

### 5. **Componente Partecipanti**

#### `EventParticipantsComponent`

Pagina dedicata per visualizzare i partecipanti utenti:

- Tabella con informazioni complete
- Statistiche: totale partecipanti e posti prenotati
- Link email per contatto diretto
- Empty state quando non ci sono partecipanti
- Pulsante back per tornare alla lista eventi

## 🎨 Pattern e Convenzioni

### Gestione Errori

```typescript
this.animatoreService.metodoCheChiamaAPI().subscribe({
  next: (data) => {
    // Successo
    this.snackBar.open('Operazione completata', 'Chiudi', { duration: 3000 });
  },
  error: (err) => {
    // Errore
    this.snackBar.open(
      'Errore: ' + (err.error?.message || 'Errore sconosciuto'),
      'Chiudi',
      { duration: 5000 }
    );
  }
});
```

### Dialog Pattern

```typescript
const dialogRef = this.dialog.open(ComponentDialog, {
  width: '600px',
  data: { /* dati */ }
});

dialogRef.afterClosed().subscribe(result => {
  if (result) {
    // Azione confermata
  }
});
```

### Compatibilità Backend

I modelli supportano campi alternativi per garantire compatibilità:

- `nomeEvento` / `titolo`
- `dataOraInizio` / `dataInizio`
- `dataOraFine` / `dataFine`
- `luogoEvento` / `luogo`

## 🔧 Utilizzo

### Avviare un Evento

```typescript
// Nel componente
startEvent(evento: EventoDTO): void {
  // Mostra dialog di conferma
  // Chiama animatoreService.iniziaEvento(evento.id)
  // Aggiorna la lista eventi
}
```

### Promuovere un Evento

```typescript
promoteEvent(evento: EventoDTO): void {
  const dialogRef = this.dialog.open(PromoteEventDialogComponent, {
    data: { evento }
  });

  dialogRef.afterClosed().subscribe((request: PromoteRequestDTO) => {
    if (request) {
      this.animatoreService.promoteEvento(evento.id, request).subscribe(
        response => console.log('Promosso su', response.canaliPromossi)
      );
    }
  });
}
```

### Gestire Aziende Partecipanti

```typescript
manageAziende(evento: EventoDTO): void {
  // Carica aziende partecipanti
  this.animatoreService.getAziendePartecipanti(evento.id).subscribe(
    aziendePartecipanti => {
      // Apri dialog con lista aziende
      // Gestisci aggiungi/rimuovi tramite callback
    }
  );
}
```

## 📁 Struttura File

```
frontend/piattaforma-agricola/src/app/
├── core/
│   ├── models/
│   │   └── animatore.models.ts          ✅ Aggiornato
│   └── services/
│       └── animatore.service.ts         ✅ Aggiornato
└── features/
    └── dashboard/
        └── animatore/
            ├── components/
            │   ├── event-dialogs/
            │   │   ├── promote-event-dialog.component.ts      ✅ Nuovo
            │   │   ├── confirm-action-dialog.component.ts     ✅ Nuovo
            │   │   └── manage-aziende-dialog.component.ts     ✅ Nuovo
            │   └── eventi-management/
            │       ├── eventi-management.component.ts         ✅ Aggiornato
            │       └── eventi-management.component.html       ✅ Aggiornato
            └── pages/
                └── event-participants/
                    └── event-participants.component.ts        ✅ Nuovo
```

## 🚀 Prossimi Passi

1. **Testing**: Implementare test unitari per servizi e componenti
2. **Routing**: Aggiungere rotte per i nuovi componenti
3. **API Aziende**: Implementare endpoint per recuperare tutte le aziende disponibili
4. **Validazione Form**: Aggiungere validatori custom per creazione/modifica eventi
5. **Gestione Immagini**: Implementare upload immagini per eventi
6. **Notifiche Real-time**: Integrare WebSocket per notifiche partecipanti

## 📝 Note Tecniche

- **Standalone Components**: Tutti i componenti utilizzano il nuovo pattern standalone di Angular
- **Material UI**: Design system coerente con Material Design
- **TypeScript Strict**: Tipizzazione completa per type safety
- **RxJS**: Pattern reattivi per gestione stati e chiamate async
- **Responsive**: UI ottimizzata per desktop e mobile

## 🐛 Known Issues

- [ ] Le aziende disponibili nel dialog `ManageAziendeDialogComponent` devono essere caricate da un endpoint dedicato (TODO)
- [ ] Il routing per `EventParticipantsComponent` deve essere aggiunto al modulo routing
- [ ] La gestione degli stati potrebbe richiedere ulteriore validazione lato backend

## 📚 Riferimenti

- [Documentazione API Backend](../../../docs/API_ANIMATORE.md)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)
