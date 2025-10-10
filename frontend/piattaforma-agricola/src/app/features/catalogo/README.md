# Catalogo Pubblico - Documentazione

## Overview

Il modulo Catalogo fornisce un'interfaccia pubblica per visualizzare e cercare prodotti e pacchetti disponibili sulla piattaforma agricola locale.

## Caratteristiche Principali

### 🎯 Funzionalità

1. **Vista Unificata**: Mostra prodotti e pacchetti in un'unica interfaccia
2. **Ricerca Testuale**: Ricerca in tempo reale con debounce (300ms)
3. **Filtri Avanzati**:
   - Tipo (Prodotto/Pacchetto/Tutti)
   - Aziende (con ricerca)
   - Categorie
   - Range di prezzo
   - Certificazioni
   - Solo disponibili
4. **Ordinamento**: Nome (A-Z/Z-A), Prezzo (↑/↓), Disponibilità, Sconto
5. **Modalità Vista**: Griglia o Lista
6. **Paginazione**: Navigazione tra pagine di risultati
7. **Autenticazione Condizionale**: Pulsanti carrello visibili solo per utenti autenticati

### 🛠️ Architettura

#### Componenti

```
catalogo/
├── components/
│   ├── catalog-item-card/          # Card per visualizzare singoli item
│   ├── catalog-filters/            # Sidebar filtri
│   ├── catalog-search-bar/         # Barra di ricerca
│   └── catalog-sort-header/        # Header ordinamento e vista
├── pages/
│   └── catalog-view/               # Componente principale
├── catalogo.module.ts              # Module definition
└── catalogo-routing.module.ts     # Routing configuration
```

#### Servizi

- **CatalogService**: Aggrega dati da `PublicProdottiService` e `PublicPacchettiService`
  - Combina risultati in formato unificato
  - Gestisce cache per performance
  - Fornisce filtri e ordinamento

#### Modelli

- **CatalogItem**: Interfaccia unificata per prodotti e pacchetti
- **CatalogFilters**: Definisce i filtri applicabili
- **CatalogSearchResult**: Risultato paginato della ricerca

### 🌐 Routing

La route principale è:

```
/catalogo
```

Accessibile a tutti gli utenti (autenticati e non).

### 📱 Responsive Design

- **Desktop**: Layout a 2 colonne (filtri sidebar + contenuto)
- **Tablet**: Layout compatto
- **Mobile**:
  - Filtri in drawer slide-in
  - Vista griglia single column
  - Controlli touch-friendly

### 🔐 Gestione Autenticazione

```typescript
// Utente NON autenticato
- Può visualizzare catalogo
- Può cercare e filtrare
- Non vede pulsanti "Aggiungi al carrello"
- Vede link "Accedi per acquistare"

// Utente autenticato
- Tutte le funzionalità sopra +
- Pulsanti "Aggiungi al carrello" visibili
- Click carrello → mostra snackbar (implementazione carrello futura)
```

### 🎨 UI/UX Features

1. **Loading States**: Spinner durante il caricamento
2. **Empty States**: Messaggio quando nessun risultato
3. **Badge Visivi**:
   - Tipo (Prodotto/Pacchetto)
   - Sconto percentuale
   - Certificazioni
4. **Disponibilità**: Indicatori colorati (verde/giallo/rosso)
5. **Prezzi**: Barrati se scontati
6. **Animazioni**: Fade-in per card items
7. **Skeleton Loaders**: Durante caricamento (future enhancement)

### 🔗 Integrazione API

Il catalogo utilizza le seguenti API pubbliche:

**Prodotti**:

- `GET /api/prodotti` - Lista tutti i prodotti
- `GET /api/prodotti/cercaProdotti?query=...` - Ricerca prodotti
- `GET /api/prodotti/venditori/{id}` - Prodotti per venditore

**Pacchetti**:

- `GET /api/pacchetti` - Lista tutti i pacchetti
- `GET /api/pacchetti/cercaPacchetti?query=...` - Ricerca pacchetti
- `GET /api/pacchetti/distributori/{id}` - Pacchetti per distributore

**Aziende**:

- `GET /api/azienda/tutteLeAziende` - Lista tutte le aziende (per filtri)

### 🚀 Performance

1. **Lazy Loading**: Modulo caricato on-demand
2. **Cache**: Lista aziende cached con `shareReplay(1)`
3. **Debounce**: Ricerca debounced a 300ms
4. **Change Detection**: OnPush strategy per performance
5. **Query Params**: Stato filtri salvato nell'URL per condivisione/bookmark

### 📊 Stato e Gestione Dati

Il componente principale gestisce lo stato tramite:

```typescript
interface CatalogState {
  filters: CatalogFilters;
  viewMode: CatalogViewMode;
  isLoading: boolean;
  error?: string;
  results?: CatalogSearchResult;
}
```

### 🎯 URL Query Parameters

I filtri vengono sincronizzati con l'URL:

```
/catalogo?q=olio&tipo=PRODOTTO&aziende=1,2&prezzoMin=5&prezzoMax=20&sort=prezzo_asc&page=0&view=grid
```

Questo permette:

- Condivisione link con filtri
- Bookmark
- Navigazione back/forward del browser

### 🔄 Flusso Utente

1. Utente visita `/catalogo`
2. Sistema carica opzioni filtri (aziende, categorie, certificazioni)
3. Sistema esegue ricerca iniziale (primi 20 item)
4. Utente può:
   - Cercare per testo
   - Applicare filtri
   - Ordinare risultati
   - Cambiare modalità vista
   - Navigare tra pagine
   - Cliccare su azienda per filtrare
   - Vedere dettagli item
   - (Se autenticato) Aggiungere al carrello

### 🎨 Personalizzazione

#### Categorie e Certificazioni

Le etichette sono configurabili in `catalog.models.ts`:

```typescript
export const CATEGORY_LABELS: Record<string, string> = {
  'FRUTTA': 'Frutta',
  'VERDURA': 'Verdura',
  // ...
};

export const CERTIFICATION_ICONS: Record<string, string> = {
  'BIOLOGICO': 'eco',
  'DOP': 'verified',
  // ...
};
```

#### Configurazione Catalogo

```typescript
export const DEFAULT_CATALOG_CONFIG: CatalogConfig = {
  defaultPageSize: 20,
  defaultViewMode: 'grid',
  defaultSortBy: 'nome_asc',
  enableCache: true,
  cacheDuration: 5 * 60 * 1000, // 5 minuti
  showOutOfStock: true
};
```

### 🚧 Implementazioni Future

1. **Carrello**: Implementare gestione carrello completa
2. **Virtual Scrolling**: Per liste molto lunghe (>100 items)
3. **Infinite Scroll**: Alternativa alla paginazione
4. **Preferiti**: Salvare prodotti preferiti
5. **Confronto**: Confrontare prodotti side-by-side
6. **Filtri Avanzati**: Range date, km da posizione, ecc.
7. **SEO**: Meta tags dinamici per prodotti
8. **PWA**: Cache offline per catalogo
9. **Analytics**: Tracciamento ricerche e click

### 🧪 Testing

Per testare la funzionalità:

1. Avvia il backend: `mvn spring-boot:run`
2. Avvia il frontend: `ng serve`
3. Naviga a `http://localhost:4200/catalogo`
4. Testa varie combinazioni di filtri
5. Verifica comportamento autenticato/non autenticato

### 📝 Note Tecniche

- Tutti i componenti sono **standalone** per miglior tree-shaking
- Usa **OnPush** change detection per performance
- Gestione errori con **catchError** e fallback
- **TypeScript strict mode** enabled
- Accessibilità: ARIA labels e keyboard navigation
- Material Design seguendo le linee guida

### 🐛 Troubleshooting

**Problema**: Filtri non funzionano

- Verifica che il backend sia avviato
- Controlla console per errori API
- Verifica formato dati ritornati dalle API

**Problema**: Immagini non caricate

- Le immagini usano placeholder se `immagineUrl` è vuoto
- Verifica path placeholder in `assets/images/placeholders/`

**Problema**: Autenticazione non funziona

- Verifica `AuthService.authState()` ritorna stato corretto
- Controlla che token sia presente in localStorage

## Conclusione

Il modulo Catalogo fornisce un'interfaccia completa, moderna e user-friendly per esplorare prodotti e pacchetti della piattaforma agricola locale, con supporto completo per utenti autenticati e non.
