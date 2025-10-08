# Piano di Implementazione - Dashboard Trasformatore

## Gestione Processi di Trasformazione e Fasi di Lavorazione

---

## 📋 Indice

1. [Analisi della Situazione Attuale](#analisi-situazione)
2. [API del Backend da Implementare](#api-backend)
3. [Architettura Frontend](#architettura-frontend)
4. [Piano di Implementazione Dettagliato](#piano-implementazione)
5. [TODO List Completa](#todo-list)

---

## 🔍 Analisi della Situazione Attuale <a name="analisi-situazione"></a>

### Stato Attuale del Frontend

- ✅ **Componenti Base Esistenti:**
  - `ProcessiManagementComponent` - gestione lista processi (parzialmente implementato)
  - `FasiLavorazioneComponent` - gestione fasi (solo struttura base, senza logica)
  - `TrasformatoreService` - servizio con metodi API già definiti
  - `trasformatore.models.ts` - modelli TypeScript già definiti

- ❌ **Problematiche Identificate:**
  - Bottone "Nuovo Processo" nella tab Processi non funzionante
  - Bottone "Nuova Fase" nella tab Fasi di Lavorazione non funzionante
  - Manca il dialog per creare un nuovo processo
  - Manca il dialog per modificare un processo
  - Manca il dialog per creare una nuova fase
  - Manca il dialog per modificare una fase
  - Manca la logica di eliminazione con conferma
  - Manca l'integrazione tra la lista processi e la gestione fasi
  - Manca la visualizzazione dettagli processo

---

## 🔌 API del Backend da Implementare <a name="api-backend"></a>

### 1. **Gestione Processi di Trasformazione**

#### 1.1 Crea Processo

```
POST /api/processi-trasformazione
Headers: Authorization: Bearer {token}
Body: {
  "nome": "Produzione Frittata",
  "descrizione": "Processo di rompere le uova...",
  "metodoProduzione": "Automatizzato",
  "prodottoFinaleId": 1  // opzionale
}
Response: ProcessoTrasformazioneDTO
```

#### 1.2 Ottieni Tutti i Processi (Paginato)

```
GET /api/processi-trasformazione?page=0&size=20&sortBy=nome&sortDirection=asc&trasformatoreId={id}
Response: Page<ProcessoTrasformazioneDTO>
```

#### 1.3 Ottieni Dettagli Processo

```
GET /api/processi-trasformazione/{id}
Response: ProcessoTrasformazioneDTO
```

#### 1.4 Aggiorna Processo

```
PUT /api/processi-trasformazione/{id}
Headers: Authorization: Bearer {token}
Body: {
  "nome": "Produzione Olio Extra Vergine Biologico",
  "descrizione": "Processo di trasformazione...",
  "metodoProduzione": "Spremitura a freddo"
}
Response: ProcessoTrasformazioneDTO
```

#### 1.5 Elimina Processo

```
DELETE /api/processi-trasformazione/{id}
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

#### 1.6 Collega Processo a Prodotto

```
POST /api/processi-trasformazione/{id}/collega-prodotto?prodottoId={prodottoId}&rimuovi={true/false}
Headers: Authorization: Bearer {token}
Response: ProcessoTrasformazioneDTO
```

### 2. **Gestione Fasi di Lavorazione**

#### 2.1 Aggiungi Fase al Processo

```
POST /api/processi-trasformazione/{processoId}/fasi
Headers: Authorization: Bearer {token}
Body: {
  "nome": "Fase di Spremitura",
  "descrizione": "Spremo tutto",
  "ordineEsecuzione": 2,
  "materiaPrimaUtilizzata": "Grano duro",
  "fonte": {
    "tipo": "ESTERNA",
    "nomeFornitore": "Azienda Agricola Rossi"
  }
}
Response: ProcessoTrasformazioneResponseDTO
```

#### 2.2 Visualizza Fasi del Processo

```
GET /api/processi-trasformazione/{processoId}/fasi
Response: List<FaseLavorazioneDTO>
```

#### 2.3 Aggiorna Fase

```
PUT /api/processi-trasformazione/{processoId}/fasi/{faseId}
Headers: Authorization: Bearer {token}
Body: {
  "nome": "Fase Aggiornata",
  "descrizione": "Nuova descrizione",
  "ordineEsecuzione": 1,
  "materiaPrimaUtilizzata": "Nuovo materiale"
}
Response: FaseLavorazioneDTO
```

#### 2.4 Elimina Fase

```
DELETE /api/processi-trasformazione/{processoId}/fasi/{faseId}
Headers: Authorization: Bearer {token}
Response: 204 No Content
```

---

## 🏗️ Architettura Frontend <a name="architettura-frontend"></a>

### Struttura dei Componenti

```
trasformatore/
├── pages/
│   ├── trasformatore-dashboard/           # Dashboard principale
│   ├── processo-detail/                   # NUOVO - Dettaglio processo
│   └── processo-form/                     # NUOVO - Form crea/modifica processo
├── components/
│   ├── processi-management/               # Lista processi (esistente, da completare)
│   ├── fasi-lavorazione/                  # Lista fasi (esistente, da completare)
│   ├── processo-dialog/                   # NUOVO - Dialog crea/modifica processo
│   ├── fase-dialog/                       # NUOVO - Dialog crea/modifica fase
│   ├── delete-confirmation-dialog/        # NUOVO - Dialog conferma eliminazione
│   └── processo-card/                     # NUOVO - Card dettaglio processo
└── services/
    └── trasformatore.service.ts           # Servizio esistente con API già definite
```

### Modelli TypeScript (già esistenti)

```typescript
// Processo
interface ProcessoTrasformazioneDetailDTO {
  id: number;
  nome: string;
  descrizione: string;
  metodoProduzione?: string;
  stato: string;
  dataCreazione: string;
  numeroFasi: number;
  fasi: FaseLavorazioneDTO[];
  trasformatore: { id: number; nomeAzienda: string; };
}

// Fase
interface FaseLavorazioneDTO {
  id: number;
  nome: string;
  descrizione: string;
  ordineEsecuzione: number;
  materiaPrimaUtilizzata: string;
  fonte: {
    tipo: 'ESTERNA' | 'INTERNA';
    nomeFornitore?: string;
  };
}
```

---

## 📝 Piano di Implementazione Dettagliato <a name="piano-implementazione"></a>

### FASE 1: Preparazione e Setup

**Obiettivo:** Preparare l'ambiente e verificare le API

#### 1.1 Verifica Backend

- [x] Verificare che il backend sia in esecuzione su `localhost:8080`
- [x] Testare le API con Postman usando le credenziali del trasformatore
- [x] Verificare autenticazione e autorizzazioni
- [x] Confermare formato delle risposte JSON

#### 1.2 Analisi Codice Esistente

- [ ] Analizzare `TrasformatoreService` - verificare metodi già implementati
- [ ] Analizzare `trasformatore.models.ts` - verificare modelli esistenti
- [ ] Identificare eventuali discrepanze tra modelli frontend e backend

---

### FASE 2: Implementazione Gestione Processi

#### 2.1 Dialog Crea/Modifica Processo

**File:** `processo-dialog/processo-dialog.component.ts`

**Funzionalità:**

- Form reattivo con validazione
- Campi: nome, descrizione, metodoProduzione
- Opzione per collegare a prodotto esistente
- Gestione modalità creazione/modifica
- Feedback visivo per errori

**Implementazione:**

```typescript
export class ProcessoDialogComponent {
  processoForm: FormGroup;
  isEditMode: boolean = false;
  prodottiDisponibili: ProdottoDTO[] = [];
  
  constructor(
    public dialogRef: MatDialogRef<ProcessoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private trasformatoreService: TrasformatoreService,
    private prodottoService: ProdottoService
  ) {
    this.initForm();
  }
  
  initForm() {
    this.processoForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      descrizione: ['', [Validators.required, Validators.minLength(10)]],
      metodoProduzione: [''],
      prodottoFinaleId: [null]
    });
    
    if (this.data?.processo) {
      this.isEditMode = true;
      this.processoForm.patchValue(this.data.processo);
    }
  }
  
  onSave() {
    if (this.processoForm.valid) {
      this.dialogRef.close(this.processoForm.value);
    }
  }
}
```

#### 2.2 Completare ProcessiManagementComponent

**Modifiche Necessarie:**

- [ ] Implementare metodo `createNewProcess()` - aprire dialog
- [ ] Implementare metodo `editProcess()` - aprire dialog con dati
- [ ] Implementare metodo `deleteProcess()` - dialog conferma + chiamata API
- [ ] Implementare metodo `viewProcessDetails()` - navigazione a dettaglio
- [ ] Gestire refresh lista dopo operazioni CRUD
- [ ] Migliorare gestione errori con messaggi user-friendly

**Codice Chiave:**

```typescript
createNewProcess(): void {
  const dialogRef = this.dialog.open(ProcessoDialogComponent, {
    width: '600px',
    data: { processo: null }
  });
  
  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.trasformatoreService.createProcess(result).subscribe({
        next: (processo) => {
          this.snackBar.open('Processo creato con successo!', 'Chiudi', { duration: 3000 });
          this.loadProcessi();
        },
        error: (error) => {
          this.snackBar.open('Errore durante la creazione del processo', 'Chiudi', { duration: 3000 });
        }
      });
    }
  });
}

editProcess(processo: ProcessoTrasformazioneSummaryDTO): void {
  const dialogRef = this.dialog.open(ProcessoDialogComponent, {
    width: '600px',
    data: { processo }
  });
  
  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.trasformatoreService.updateProcess(processo.id, result).subscribe({
        next: (updatedProcesso) => {
          this.snackBar.open('Processo aggiornato con successo!', 'Chiudi', { duration: 3000 });
          this.loadProcessi();
        },
        error: (error) => {
          this.snackBar.open('Errore durante l\'aggiornamento', 'Chiudi', { duration: 3000 });
        }
      });
    }
  });
}

deleteProcess(processo: ProcessoTrasformazioneSummaryDTO): void {
  const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
    width: '400px',
    data: {
      title: 'Elimina Processo',
      message: `Sei sicuro di voler eliminare il processo "${processo.nome}"?`,
      confirmText: 'Elimina',
      cancelText: 'Annulla'
    }
  });
  
  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.trasformatoreService.deleteProcess(processo.id).subscribe({
        next: () => {
          this.snackBar.open('Processo eliminato con successo!', 'Chiudi', { duration: 3000 });
          this.loadProcessi();
        },
        error: (error) => {
          this.snackBar.open('Errore durante l\'eliminazione', 'Chiudi', { duration: 3000 });
        }
      });
    }
  });
}
```

#### 2.3 Pagina Dettaglio Processo

**File:** `processo-detail/processo-detail.component.ts`

**Funzionalità:**

- Visualizzare tutti i dettagli del processo
- Lista delle fasi del processo
- Pulsanti per modificare/eliminare processo
- Sezione per aggiungere/gestire fasi
- Breadcrumb per navigazione

---

### FASE 3: Implementazione Gestione Fasi

#### 3.1 Dialog Crea/Modifica Fase

**File:** `fase-dialog/fase-dialog.component.ts`

**Funzionalità:**

- Form reattivo con validazione
- Campi: nome, descrizione, ordineEsecuzione, materiaPrimaUtilizzata
- Gestione fonte (ESTERNA/INTERNA con nome fornitore)
- Modalità creazione/modifica
- Validazione ordine esecuzione (numero positivo)

**Implementazione:**

```typescript
export class FaseDialogComponent {
  faseForm: FormGroup;
  isEditMode: boolean = false;
  processoId: number;
  
  constructor(
    public dialogRef: MatDialogRef<FaseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder
  ) {
    this.processoId = data.processoId;
    this.initForm();
  }
  
  initForm() {
    this.faseForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      descrizione: ['', [Validators.required]],
      ordineEsecuzione: [1, [Validators.required, Validators.min(1)]],
      materiaPrimaUtilizzata: ['', [Validators.required]],
      fonte: this.fb.group({
        tipo: ['ESTERNA', [Validators.required]],
        nomeFornitore: ['']
      })
    });
    
    if (this.data?.fase) {
      this.isEditMode = true;
      this.faseForm.patchValue(this.data.fase);
    }
    
    // Validazione condizionale per nomeFornitore
    this.faseForm.get('fonte.tipo')?.valueChanges.subscribe(tipo => {
      const nomeFornitoreControl = this.faseForm.get('fonte.nomeFornitore');
      if (tipo === 'ESTERNA') {
        nomeFornitoreControl?.setValidators([Validators.required]);
      } else {
        nomeFornitoreControl?.clearValidators();
      }
      nomeFornitoreControl?.updateValueAndValidity();
    });
  }
  
  onSave() {
    if (this.faseForm.valid) {
      this.dialogRef.close(this.faseForm.value);
    }
  }
}
```

#### 3.2 Completare FasiLavorazioneComponent

**Modifiche Necessarie:**

- [ ] Aggiungere selector per scegliere il processo
- [ ] Caricare fasi del processo selezionato
- [ ] Implementare `aggiungiFase()` - aprire dialog
- [ ] Implementare `modificaFase()` - aprire dialog con dati
- [ ] Implementare `eliminaFase()` - dialog conferma + chiamata API
- [ ] Implementare ordinamento fasi per `ordineEsecuzione`
- [ ] Visualizzare badge per stato fase

**Codice Chiave:**

```typescript
export class FasiLavorazioneComponent implements OnInit {
  fasi: FaseLavorazioneDTO[] = [];
  processiDisponibili: ProcessoTrasformazioneSummaryDTO[] = [];
  processoSelezionato: number | null = null;
  isLoading = false;
  
  constructor(
    private trasformatoreService: TrasformatoreService,
    public dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}
  
  ngOnInit(): void {
    this.loadProcessi();
  }
  
  loadProcessi(): void {
    this.trasformatoreService.getMyProcesses().subscribe({
      next: (response) => {
        this.processiDisponibili = response.content;
      },
      error: (error) => {
        this.snackBar.open('Errore nel caricamento dei processi', 'Chiudi', { duration: 3000 });
      }
    });
  }
  
  onProcessoChange(processoId: number): void {
    this.processoSelezionato = processoId;
    this.loadFasi();
  }
  
  loadFasi(): void {
    if (!this.processoSelezionato) return;
    
    this.isLoading = true;
    this.trasformatoreService.getProcessPhases(this.processoSelezionato).subscribe({
      next: (fasi) => {
        this.fasi = fasi.sort((a, b) => a.ordineEsecuzione - b.ordineEsecuzione);
        this.isLoading = false;
      },
      error: (error) => {
        this.snackBar.open('Errore nel caricamento delle fasi', 'Chiudi', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }
  
  aggiungiFase(): void {
    if (!this.processoSelezionato) {
      this.snackBar.open('Seleziona prima un processo', 'Chiudi', { duration: 3000 });
      return;
    }
    
    const dialogRef = this.dialog.open(FaseDialogComponent, {
      width: '600px',
      data: { processoId: this.processoSelezionato, fase: null }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.trasformatoreService.createPhase(this.processoSelezionato!, result).subscribe({
          next: (fase) => {
            this.snackBar.open('Fase creata con successo!', 'Chiudi', { duration: 3000 });
            this.loadFasi();
          },
          error: (error) => {
            this.snackBar.open('Errore durante la creazione della fase', 'Chiudi', { duration: 3000 });
          }
        });
      }
    });
  }
  
  modificaFase(fase: FaseLavorazioneDTO): void {
    const dialogRef = this.dialog.open(FaseDialogComponent, {
      width: '600px',
      data: { processoId: this.processoSelezionato, fase }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.trasformatoreService.updatePhase(this.processoSelezionato!, fase.id, result).subscribe({
          next: (updatedFase) => {
            this.snackBar.open('Fase aggiornata con successo!', 'Chiudi', { duration: 3000 });
            this.loadFasi();
          },
          error: (error) => {
            this.snackBar.open('Errore durante l\'aggiornamento', 'Chiudi', { duration: 3000 });
          }
        });
      }
    });
  }
  
  eliminaFase(fase: FaseLavorazioneDTO): void {
    const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      width: '400px',
      data: {
        title: 'Elimina Fase',
        message: `Sei sicuro di voler eliminare la fase "${fase.nome}"?`,
        confirmText: 'Elimina',
        cancelText: 'Annulla'
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.trasformatoreService.deletePhase(this.processoSelezionato!, fase.id).subscribe({
          next: () => {
            this.snackBar.open('Fase eliminata con successo!', 'Chiudi', { duration: 3000 });
            this.loadFasi();
          },
          error: (error) => {
            this.snackBar.open('Errore durante l\'eliminazione', 'Chiudi', { duration: 3000 });
          }
        });
      }
    });
  }
}
```

---

### FASE 4: Componenti Condivisi

#### 4.1 Dialog Conferma Eliminazione

**File:** `delete-confirmation-dialog/delete-confirmation-dialog.component.ts`

**Funzionalità:**

- Dialog generico per conferma eliminazione
- Configurabile con titolo, messaggio, testi bottoni
- Stile consistente con Material Design

**Implementazione:**

```typescript
export class DeleteConfirmationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DeleteConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      title: string;
      message: string;
      confirmText: string;
      cancelText: string;
    }
  ) {}
  
  onConfirm(): void {
    this.dialogRef.close(true);
  }
  
  onCancel(): void {
    this.dialogRef.close(false);
  }
}
```

---

### FASE 5: Routing e Navigazione

#### 5.1 Aggiornare Routing

**File:** `trasformatore-routing.module.ts`

```typescript
const routes: Routes = [
  {
    path: '',
    component: TrasformatoreDashboardComponent
  },
  {
    path: 'processi/nuovo',
    component: ProcessoFormComponent
  },
  {
    path: 'processi/:id',
    component: ProcessoDetailComponent
  },
  {
    path: 'processi/edit/:id',
    component: ProcessoFormComponent
  }
];
```

---

### FASE 6: Testing e Refinement

#### 6.1 Test Funzionali

- [ ] Test creazione processo
- [ ] Test modifica processo
- [ ] Test eliminazione processo
- [ ] Test creazione fase
- [ ] Test modifica fase
- [ ] Test eliminazione fase
- [ ] Test filtri e ricerca
- [ ] Test paginazione
- [ ] Test validazione form

#### 6.2 Test UX

- [ ] Verificare feedback visivo per tutte le azioni
- [ ] Verificare messaggi di errore comprensibili
- [ ] Verificare loading states
- [ ] Verificare responsive design
- [ ] Verificare accessibilità (keyboard navigation, screen readers)

#### 6.3 Ottimizzazioni

- [ ] Implementare caching dove appropriato
- [ ] Ottimizzare chiamate API (evitare duplicazioni)
- [ ] Implementare skeleton loaders
- [ ] Gestire edge cases (lista vuota, errori di rete, etc.)

---

## ✅ TODO List Completa <a name="todo-list"></a>

### 🔧 PREPARAZIONE

- [x] **TODO-001:** Verificare backend in esecuzione su `localhost:8080`
- [x] **TODO-002:** Testare tutte le API con Postman usando token trasformatore
- [x] **TODO-003:** Verificare formato risposte JSON delle API
- [x] **TODO-004:** Creare branch `feature/trasformatore-processi-fasi` da `frontend`

### 📦 MODELLI E SERVIZI

- [ ] **TODO-005:** Verificare/aggiornare `trasformatore.models.ts` con tutti i DTO necessari
- [ ] **TODO-006:** Verificare che `TrasformatoreService` abbia tutti i metodi necessari
- [ ] **TODO-007:** Aggiungere metodi mancanti al servizio (se necessari)
- [ ] **TODO-008:** Creare interfacce per request/response dialog

### 🎨 COMPONENTI DIALOG

#### Dialog Processo

- [ ] **TODO-009:** Creare `processo-dialog.component.ts`
- [ ] **TODO-010:** Creare `processo-dialog.component.html`
- [ ] **TODO-011:** Creare `processo-dialog.component.scss`
- [ ] **TODO-012:** Implementare form reattivo con validazione
- [ ] **TODO-013:** Implementare logica modalità creazione/modifica
- [ ] **TODO-014:** Aggiungere select prodotti (se necessario)
- [ ] **TODO-015:** Implementare gestione errori nel dialog
- [ ] **TODO-016:** Testare dialog in modalità creazione
- [ ] **TODO-017:** Testare dialog in modalità modifica

#### Dialog Fase

- [ ] **TODO-018:** Creare `fase-dialog.component.ts`
- [ ] **TODO-019:** Creare `fase-dialog.component.html`
- [ ] **TODO-020:** Creare `fase-dialog.component.scss`
- [ ] **TODO-021:** Implementare form reattivo con validazione
- [ ] **TODO-022:** Implementare logica fonte ESTERNA/INTERNA
- [ ] **TODO-023:** Implementare validazione ordine esecuzione
- [ ] **TODO-024:** Implementare validazione condizionale nome fornitore
- [ ] **TODO-025:** Testare dialog in modalità creazione
- [ ] **TODO-026:** Testare dialog in modalità modifica

#### Dialog Conferma

- [ ] **TODO-027:** Creare `delete-confirmation-dialog.component.ts`
- [ ] **TODO-028:** Creare `delete-confirmation-dialog.component.html`
- [ ] **TODO-029:** Creare `delete-confirmation-dialog.component.scss`
- [ ] **TODO-030:** Implementare logica conferma/annulla
- [ ] **TODO-031:** Testare dialog con diversi messaggi

### 📋 GESTIONE PROCESSI

#### ProcessiManagementComponent

- [ ] **TODO-032:** Implementare metodo `createNewProcess()` con apertura dialog
- [ ] **TODO-033:** Implementare logica salvataggio nuovo processo
- [ ] **TODO-034:** Implementare metodo `editProcess()` con apertura dialog
- [ ] **TODO-035:** Implementare logica aggiornamento processo
- [ ] **TODO-036:** Implementare metodo `deleteProcess()` con dialog conferma
- [ ] **TODO-037:** Implementare logica eliminazione processo
- [ ] **TODO-038:** Implementare metodo `viewProcessDetails()` per navigazione
- [ ] **TODO-039:** Aggiungere refresh automatico lista dopo operazioni CRUD
- [ ] **TODO-040:** Migliorare gestione errori con messaggi user-friendly
- [ ] **TODO-041:** Implementare loading state per operazioni async
- [ ] **TODO-042:** Testare creazione processo
- [ ] **TODO-043:** Testare modifica processo
- [ ] **TODO-044:** Testare eliminazione processo
- [ ] **TODO-045:** Testare visualizzazione dettagli

#### Template HTML

- [ ] **TODO-046:** Verificare binding bottone "Nuovo Processo"
- [ ] **TODO-047:** Aggiungere tooltip ai bottoni azioni
- [ ] **TODO-048:** Verificare visualizzazione colonne tabella
- [ ] **TODO-049:** Aggiungere icone Material per azioni
- [ ] **TODO-050:** Implementare empty state quando lista vuota

### 🔧 GESTIONE FASI

#### FasiLavorazioneComponent

- [ ] **TODO-051:** Aggiungere select/dropdown per scelta processo
- [ ] **TODO-052:** Implementare metodo `loadProcessi()` per popolare select
- [ ] **TODO-053:** Implementare metodo `onProcessoChange()` per cambio processo
- [ ] **TODO-054:** Implementare metodo `loadFasi()` per caricare fasi processo
- [ ] **TODO-055:** Implementare metodo `aggiungiFase()` con apertura dialog
- [ ] **TODO-056:** Implementare logica creazione nuova fase
- [ ] **TODO-057:** Implementare metodo `modificaFase()` con apertura dialog
- [ ] **TODO-058:** Implementare logica aggiornamento fase
- [ ] **TODO-059:** Implementare metodo `eliminaFase()` con dialog conferma
- [ ] **TODO-060:** Implementare logica eliminazione fase
- [ ] **TODO-061:** Implementare ordinamento fasi per `ordineEsecuzione`
- [ ] **TODO-062:** Aggiungere refresh automatico lista dopo operazioni CRUD
- [ ] **TODO-063:** Implementare visualizzazione badge stato fase
- [ ] **TODO-064:** Gestire caso "nessun processo selezionato"
- [ ] **TODO-065:** Implementare loading state per caricamento fasi
- [ ] **TODO-066:** Testare selezione processo
- [ ] **TODO-067:** Testare creazione fase
- [ ] **TODO-068:** Testare modifica fase
- [ ] **TODO-069:** Testare eliminazione fase
- [ ] **TODO-070:** Testare ordinamento fasi

#### Template HTML

- [ ] **TODO-071:** Aggiungere select processo in cima al componente
- [ ] **TODO-072:** Verificare binding bottone "Nuova Fase"
- [ ] **TODO-073:** Aggiungere tooltip ai bottoni azioni
- [ ] **TODO-074:** Implementare visualizzazione ordine esecuzione
- [ ] **TODO-075:** Implementare badge per tipo fonte (ESTERNA/INTERNA)
- [ ] **TODO-076:** Implementare empty state quando nessuna fase
- [ ] **TODO-077:** Aggiungere messaggio "Seleziona un processo" iniziale

### 📄 PAGINA DETTAGLIO PROCESSO

- [ ] **TODO-078:** Creare `processo-detail.component.ts`
- [ ] **TODO-079:** Creare `processo-detail.component.html`
- [ ] **TODO-080:** Creare `processo-detail.component.scss`
- [ ] **TODO-081:** Implementare caricamento dettagli processo da route param
- [ ] **TODO-082:** Implementare visualizzazione informazioni processo
- [ ] **TODO-083:** Implementare sezione lista fasi del processo
- [ ] **TODO-084:** Aggiungere bottoni modifica/elimina processo
- [ ] **TODO-085:** Aggiungere bottone "Aggiungi Fase"
- [ ] **TODO-086:** Implementare breadcrumb per navigazione
- [ ] **TODO-087:** Implementare card informazioni trasformatore
- [ ] **TODO-088:** Testare caricamento dettagli
- [ ] **TODO-089:** Testare navigazione da lista processi

### 🗺️ ROUTING

- [ ] **TODO-090:** Aggiornare `trasformatore-routing.module.ts`
- [ ] **TODO-091:** Aggiungere route per dettaglio processo (`/processi/:id`)
- [ ] **TODO-092:** Aggiungere route per form processo (`/processi/nuovo`, `/processi/edit/:id`)
- [ ] **TODO-093:** Configurare guards se necessario
- [ ] **TODO-094:** Testare navigazione tra le varie pagine

### 🎨 STILI E UX

- [ ] **TODO-095:** Creare stili comuni per dialog in `_dialog.scss`
- [ ] **TODO-096:** Implementare animazioni per apertura/chiusura dialog
- [ ] **TODO-097:** Implementare skeleton loaders per liste
- [ ] **TODO-098:** Implementare spinner per loading state
- [ ] **TODO-099:** Verificare responsive design su mobile/tablet
- [ ] **TODO-100:** Implementare empty state design per liste vuote
- [ ] **TODO-101:** Aggiungere icone Material appropriate
- [ ] **TODO-102:** Verificare color scheme consistente con Material Design
- [ ] **TODO-103:** Implementare hover effects su pulsanti e card

### ♿ ACCESSIBILITÀ

- [ ] **TODO-104:** Aggiungere aria-labels ai bottoni senza testo
- [ ] **TODO-105:** Verificare navigazione da tastiera in tutti i dialog
- [ ] **TODO-106:** Implementare focus trap nei dialog
- [ ] **TODO-107:** Aggiungere ruoli ARIA appropriati
- [ ] **TODO-108:** Testare con screen reader

### 🧪 TESTING E DEBUG

#### Test Funzionali

- [ ] **TODO-109:** Test end-to-end creazione processo
- [ ] **TODO-110:** Test end-to-end modifica processo
- [ ] **TODO-111:** Test end-to-end eliminazione processo
- [ ] **TODO-112:** Test end-to-end creazione fase
- [ ] **TODO-113:** Test end-to-end modifica fase
- [ ] **TODO-114:** Test end-to-end eliminazione fase
- [ ] **TODO-115:** Test filtri lista processi
- [ ] **TODO-116:** Test paginazione lista processi
- [ ] **TODO-117:** Test validazione form processo
- [ ] **TODO-118:** Test validazione form fase
- [ ] **TODO-119:** Test gestione errori API (401, 403, 404, 500)
- [ ] **TODO-120:** Test offline/rete lenta

#### Test UX

- [ ] **TODO-121:** Verificare feedback visivo per tutte le azioni
- [ ] **TODO-122:** Verificare messaggi di errore comprensibili
- [ ] **TODO-123:** Verificare snackbar con messaggi corretti
- [ ] **TODO-124:** Verificare stati vuoti (empty states)
- [ ] **TODO-125:** Verificare loading states
- [ ] **TODO-126:** Test su browser diversi (Chrome, Firefox, Safari)

### 🚀 OTTIMIZZAZIONI

- [ ] **TODO-127:** Implementare caching per lista processi (se appropriato)
- [ ] **TODO-128:** Ottimizzare chiamate API (evitare duplicazioni)
- [ ] **TODO-129:** Implementare debounce per ricerca in tempo reale
- [ ] **TODO-130:** Implementare virtual scrolling per liste lunghe (se necessario)
- [ ] **TODO-131:** Ottimizzare bundle size (lazy loading, tree shaking)

### 📚 DOCUMENTAZIONE

- [ ] **TODO-132:** Documentare componenti nuovi con JSDoc
- [ ] **TODO-133:** Aggiornare README con nuove funzionalità
- [ ] **TODO-134:** Creare guida utente per gestione processi/fasi
- [ ] **TODO-135:** Documentare API calls e modelli dati
- [ ] **TODO-136:** Creare screenshots per documentazione

### 🔄 INTEGRAZIONE E DEPLOY

- [x] **TODO-137:** Merge branch `feature/trasformatore-processi-fasi` in `frontend`
- [ ] **TODO-138:** Verificare build production (`ng build --prod`)
- [ ] **TODO-139:** Verificare nessun errore console in produzione
- [ ] **TODO-140:** Test su ambiente di staging
- [ ] **TODO-141:** Deploy su ambiente di produzione
- [ ] **TODO-142:** Verifica post-deploy funzionalità

---

## 📊 Priorità di Implementazione

### 🔴 PRIORITÀ ALTA (Critiche - Blocca funzionalità base)

1. TODO-001 a TODO-008: Preparazione e verifica
2. TODO-009 a TODO-017: Dialog Processo
3. TODO-032 a TODO-045: Gestione Processi base
4. TODO-046 a TODO-050: Template Processi

### 🟡 PRIORITÀ MEDIA (Importanti - Completa funzionalità)

5. TODO-018 a TODO-031: Dialog Fase e Conferma
6. TODO-051 a TODO-077: Gestione Fasi completa
7. TODO-090 a TODO-094: Routing
8. TODO-109 a TODO-120: Testing funzionale

### 🟢 PRIORITÀ BASSA (Nice-to-have - Migliora UX)

9. TODO-078 a TODO-089: Pagina Dettaglio
10. TODO-095 a TODO-103: Stili e UX
11. TODO-104 a TODO-108: Accessibilità
12. TODO-121 a TODO-136: Testing UX e Documentazione
13. TODO-127 a TODO-131: Ottimizzazioni
14. TODO-137 a TODO-142: Deploy

---

## 🎯 Milestone

### Milestone 1: Gestione Processi Funzionante (TODO 1-50)

**Obiettivo:** Creare, modificare, eliminare e visualizzare processi

### Milestone 2: Gestione Fasi Completa (TODO 51-77)

**Obiettivo:** Creare, modificare, eliminare e visualizzare fasi

### Milestone 3: Navigazione e Dettagli (TODO 78-94)

**Obiettivo:** Navigazione completa e pagina dettaglio processo

### Milestone 4: Polish e Testing (TODO 95-142)

**Obiettivo:** UX, accessibilità, testing, deploy

---

## 📝 Note Finali

### Dipendenze

- Backend deve essere in esecuzione su `localhost:8080`
- Token JWT valido per autenticazione trasformatore
- Database con dati di test

### Problemi Noti da Risolvere

1. ❌ Bottone "Nuovo Processo" non funzionante → TODO-032
2. ❌ Bottone "Nuova Fase" non funzionante → TODO-055
3. ❌ Mancano dialog per creazione/modifica → TODO-009, TODO-018
4. ❌ Manca integrazione tra processi e fasi → TODO-051 a TODO-054

### Suggerimenti per l'Implementazione

- Iniziare con i TODO priorità ALTA
- Testare ogni componente prima di procedere al successivo
- Usare Angular Material per consistenza UI
- Implementare error handling robusto
- Mantenere il codice DRY (Don't Repeat Yourself)
- Seguire le best practices Angular (OnPush strategy, reactive forms, etc.)

---

**Data creazione piano:** 8 ottobre 2025  
**Versione:** 1.0  
**Stato:** In attesa di implementazione
