# Piattaforma di Digitalizzazione e Valorizzazione della Filiera Agricola Locale

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-20-red.svg)](https://angular.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()

## Indice

- [Descrizione del Progetto](#descrizione-del-progetto)
- [Funzionalità Principali](#funzionalità-principali)
- [Architettura del Sistema](#architettura-del-sistema)
- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Attori del Sistema](#attori-del-sistema)
- [Installazione e Avvio](#installazione-e-avvio)
  - [Backend (Spring Boot)](#backend-spring-boot)
  - [Frontend (Angular)](#frontend-angular)
- [Come Usare il Progetto](#come-usare-il-progetto)
- [Struttura del Progetto](#struttura-del-progetto)
- [API Documentation](#api-documentation)
- [Design Pattern e Architettura](#design-pattern-e-architettura)
- [Testing](#testing)
- [Come Contribuire](#come-contribuire)
- [Crediti](#crediti)
- [Licenza](#licenza)

## Descrizione del Progetto

### Cosa fa il progetto?

La **Piattaforma di Digitalizzazione e Valorizzazione della Filiera Agricola Locale** è un'applicazione web full-stack moderna che permette la gestione, valorizzazione e tracciabilità dei prodotti agricoli di un territorio comunale. La piattaforma facilita la connessione tra tutti gli attori della filiera agricola locale, dalla produzione alla vendita finale, attraverso un'interfaccia web intuitiva e responsive.

### Perché è stato creato?

Il progetto nasce dall'esigenza di:

- **Promuovere il territorio** e i suoi prodotti tipici attraverso un'interfaccia web moderna
- **Garantire la tracciabilità** completa dei prodotti agricoli con visualizzazione interattiva
- **Facilitare la commercializzazione** diretta tra produttori e consumatori tramite e-commerce integrato
- **Valorizzare le tradizioni locali** e i metodi di produzione sostenibili
- **Creare una rete** tra tutti gli attori della filiera agricola
- **Offrire un'esperienza utente** ottimale su tutti i dispositivi (desktop, tablet, mobile)

### Quali tecnologie sono state usate e perché?

#### Backend

- **Java 21**: Linguaggio principale per performance, robustezza e funzionalità moderne
- **Spring Boot 3.4.6**: Framework per sviluppo rapido e configurazione automatica
- **Spring Security**: Gestione sicurezza e autenticazione con JWT
- **Spring Data JPA**: Persistenza dati e gestione database relazionale
- **H2 Database**: Database embedded per sviluppo e testing
- **MapStruct**: Mapping automatico tra DTO e entità con code generation
- **Lombok**: Riduzione boilerplate code e miglioramento leggibilità
- **Maven**: Gestione dipendenze e build automation

#### Frontend

- **Angular 20**: Framework moderno per SPA (Single Page Application) con signals e performance ottimizzate
- **TypeScript 5.7**: Type safety e developer experience migliorata
- **Angular Material**: UI component library per design coerente e accessibile
- **NgRx**: State management reattivo per gestione stato centralizzata
- **RxJS**: Programmazione reattiva per gestione asincrona
- **SCSS**: Preprocessore CSS per stili modulari e manutenibili
- **Angular Router**: Navigazione client-side con lazy loading

### Architettura del Sistema

Il progetto adotta un'architettura **client-server** con:

- **Backend REST API**: Espone endpoint RESTful per tutte le operazioni
- **Frontend SPA**: Single Page Application per esperienza utente fluida
- **Autenticazione JWT**: Token-based authentication per sicurezza stateless
- **Comunicazione asincrona**: HTTP/REST con gestione reattiva delle risposte

### Sfide affrontate e soluzioni

**Sfide principali:**

- ✅ **Sistema di tracciabilità completo**: Implementato con relazioni JPA e visualizzazione su mappa interattiva
- ✅ **Gestione ruoli e permessi complessi**: Risolto con Spring Security e guards Angular
- ✅ **State management frontend**: Implementato pattern Redux con NgRx per gestione stato prevedibile
- ✅ **Responsive design**: Utilizzato Angular Material con layout flessibili
- ✅ **Performance**: Lazy loading moduli Angular e ottimizzazione bundle size

## Funzionalità Principali

### 🛒 E-commerce e Marketplace

- Catalogo prodotti con ricerca avanzata e filtri
- Vendita diretta di prodotti agricoli
- Gestione carrello con calcolo automatico totali
- Sistema di ordini con tracking stato
- Creazione di pacchetti prodotto personalizzati
- Interfaccia responsive per shopping da qualsiasi dispositivo

### 📍 Tracciabilità e Geolocalizzazione

- Tracciamento completo della filiera produttiva
- Collegamento tra fasi di produzione e trasformazione
- Certificazioni di qualità e origine

### 👥 Gestione Multi-Ruolo

- Sistema di autenticazione JWT con refresh token
- Autenticazione e autorizzazione avanzata
- Dashboard personalizzate per ogni tipologia di utente
- Gestione permessi granulari (RBAC - Role-Based Access Control)
- Workflow di approvazione contenuti
- Interfaccia amministrazione completa

### 🏭 Processi di Trasformazione

- Documentazione completa dei processi produttivi
- Collegamento tra materie prime e prodotti finiti
- Gestione fasi di lavorazione con timeline
- Caricamento documenti e certificazioni
- Visualizzazione genealogia prodotti

### 📅 Gestione Eventi

- Calendario eventi con visualizzazione mensile/settimanale
- Organizzazione fiere e mercati locali
- Sistema di prenotazione ad eventi

### 🎨 User Experience

- Design moderno e intuitivo con Angular Material
- Accessibilità WCAG compliant
- Performance ottimizzate con lazy loading
- Notifiche in-app e feedback visivi

## Tecnologie Utilizzate

### Backend Stack

| Categoria | Tecnologia | Versione | Scopo |
|-----------|------------|----------|-------|
| **Linguaggio** | Java | 21 | Linguaggio principale con features moderne |
| **Framework** | Spring Boot | 3.4.6 | Framework applicativo enterprise |
| **Sicurezza** | Spring Security | 6.x | Autenticazione e autorizzazione |
| **Persistenza** | Spring Data JPA | 3.x | ORM e gestione database |
| **Database** | H2 Database | 2.x | Database embedded per dev/test |
| **Mapping** | MapStruct | 1.6.3 | Mapping DTO ↔ Entity |
| **Boilerplate** | Lombok | 1.18.x | Riduzione codice ripetitivo |
| **JWT** | JJWT | 0.12.6 | Token-based authentication |
| **Validation** | Jakarta Validation | 3.x | Validazione dati |
| **Build Tool** | Maven | 3.9+ | Gestione dipendenze e build |

### Frontend Stack

| Categoria | Tecnologia | Versione | Scopo |
|-----------|------------|----------|-------|
| **Framework** | Angular | 20.3 | Framework SPA moderno |
| **Linguaggio** | TypeScript | 5.7+ | Type-safe JavaScript |
| **UI Library** | Angular Material | 20.2 | Component library Material Design |
| **State Management** | NgRx | 20.0 | Redux pattern per Angular |
| **Router** | Angular Router | 20.3 | Navigazione client-side |
| **HTTP Client** | Angular HttpClient | 20.3 | Comunicazione con backend |
| **Reactive** | RxJS | 7.8 | Programmazione reattiva |
| **Forms** | Angular Forms | 20.3 | Gestione form reattivi |
| **Styling** | SCSS | - | Preprocessore CSS |
| **Build Tool** | Angular CLI | 20.3 | Build e development server |
| **Internazionalization** | ngx-translate | 17.0 | Supporto multi-lingua |

### Tools & DevOps

| Categoria | Tecnologia | Scopo |
|-----------|------------|-------|
| **Version Control** | Git | Controllo versione |
| **API Testing** | Postman / Swagger UI | Test e documentazione API |
| **Proxy** | Angular Proxy | Proxy dev per evitare CORS |
| **Package Manager** | npm | Gestione dipendenze frontend |

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

#### Per il Backend

- **Java 21** o superiore ([Download](https://adoptium.net/))
- **Maven 3.9+** (incluso con wrapper `./mvnw`)
- **Git**

#### Per il Frontend

- **Node.js 18+** ([Download](https://nodejs.org/))
- **npm 10+** (incluso con Node.js)
- **Angular CLI 20+** (installato automaticamente)

### Quick Start 🚀

Il modo più rapido per avviare l'applicazione completa:

```bash
# 1. Clona il repository
git clone https://github.com/AngeloAlbanesi/piattaforma_agricola_locale.git
cd piattaforma_agricola_locale

# 2. Avvia il backend (terminale 1)
./mvnw spring-boot:run

# 3. Avvia il frontend (terminale 2)
cd frontend/piattaforma-agricola
npm install
npm start
```

Poi apri il browser su: **<http://localhost:4200>**

---

### Backend (Spring Boot)

#### Installazione Backend

1. **Naviga nella directory principale del progetto**

```bash
cd piattaforma_agricola_locale
```

2. **Installa le dipendenze Maven**

```bash
./mvnw clean install
```

3. **Configura il database (opzionale)**

Il progetto usa H2 in-memory di default. Per personalizzare, modifica:
`src/main/resources/application.properties`

```properties
# Configurazione H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop

# Server
server.port=8080

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

#### Avvio Backend

**Modalità Development:**

```bash
./mvnw spring-boot:run
```

**Modalità Production:**

```bash
./mvnw clean package
java -jar target/piattaforma_agricola_locale-0.0.1-SNAPSHOT.jar
```

**Con profilo personalizzato:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Verifica Backend

- **API Base URL**: <http://localhost:8080>
- **H2 Console**: <http://localhost:8080/h2-console>
- **Swagger UI**: <http://localhost:8080/swagger-ui.html>
- **Health Check**: <http://localhost:8080/actuator/health>

---

### Frontend (Angular)

#### Installazione Frontend

1. **Naviga nella directory frontend**

```bash
cd frontend/piattaforma-agricola
```

2. **Installa le dipendenze npm**

```bash
npm install
```

3. **Configura il proxy (già configurato)**

Il file `proxy.conf.json` è già configurato per puntare al backend:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

#### Avvio Frontend

**Modalità Development (con proxy):**

```bash
npm start
# oppure
ng serve --proxy-config proxy.conf.json
```

**Modalità Production:**

```bash
npm run start:prod
```

**Build per Production:**

```bash
npm run build
# Output in: dist/piattaforma-agricola/browser/
```

#### Verifica Frontend

- **Applicazione**: <http://localhost:4200>
- **Dev Server**: in ascolto sulla porta 4200
- **Auto-reload**: abilitato su modifica file

---

### Configurazione Ambiente di Sviluppo

#### Environment Files (Frontend)

**Development** (`src/environments/environment.ts`):

```typescript
export const environment = {
  production: false,
  apiUrl: '/api', // Usa proxy
  apiBaseUrl: 'http://localhost:8080'
};
```

**Production** (`src/environments/environment.prod.ts`):

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-production-api.com/api',
  apiBaseUrl: 'https://your-production-api.com'
};
```

#### Scripts NPM Disponibili

```bash
npm start              # Dev server con proxy
npm run build          # Build production
npm run build:dev      # Build development
npm run watch          # Build in watch mode
npm test               # Esegui test
npm run lint           # Lint del codice
```

---

### Troubleshooting

#### Backend

**Problema**: Porta 8080 già in uso

```bash
# Cambia porta in application.properties
server.port=8081
```

**Problema**: Database H2 non accessibile

```bash
# Verifica in application.properties
spring.h2.console.enabled=true
```

#### Frontend

**Problema**: Errori CORS

```bash
# Verifica che il proxy sia configurato correttamente
# e che il backend sia in esecuzione
```

**Problema**: Moduli Node mancanti

```bash
rm -rf node_modules package-lock.json
npm install
```

**Problema**: Errori di compilazione TypeScript

```bash
# Cancella cache e ricompila
rm -rf .angular
npm start
```

## Come Usare il Progetto

### 🌐 Accesso all'Applicazione Web

Una volta avviati sia backend che frontend:

1. **Apri il browser** su: `http://localhost:4200`
2. **Homepage pubblica**: accesso a catalogo prodotti e informazioni territorio
3. **Registrazione**: crea un nuovo account selezionando il ruolo appropriato
4. **Login**: accedi con le credenziali create
5. **Dashboard**: interfaccia personalizzata in base al ruolo utente

### 👤 Ruoli e Funzionalità

#### Acquirente (Cliente)

- Browse del catalogo prodotti con filtri avanzati
- Aggiungi prodotti al carrello
- Effettua ordini e traccia lo stato
- Visualizza storico acquisti
- Prenotazione eventi e fiere

#### Produttore

- Dashboard con statistiche vendite
- Gestione prodotti (CRUD)
- Caricamento certificazioni e documenti
- Gestione inventario
- Creazione processi produttivi
- Visualizzazione ordini ricevuti

#### Trasformatore

- Gestione prodotti trasformati
- Collegamento materie prime → prodotto finito
- Documentazione processi di trasformazione
- Gestione filiera produttiva

#### Distributore/Rivenditore

- Vendita prodotti locali
- Creazione pacchetti promozionali
- Gestione inventario multi-produttore
- Dashboard ordini

#### Animatore (Organizzatore Eventi)

- Creazione e gestione eventi
- Calendario manifestazioni
- Gestione prenotazioni
- Promozione territorio

#### Curatore (Moderatore Contenuti)

- Revisione contenuti inseriti
- Approvazione/rifiuto prodotti
- Controllo qualità informazioni
- Moderazione recensioni

#### Gestore Piattaforma (Admin)

- Gestione utenti e ruoli
- Configurazioni di sistema
- Monitoring e analytics
- Gestione permessi avanzati

---

### 🔧 Testare le API Backend

Per testare le API REST del progetto:

#### 1. Swagger UI (Raccomandato) 📋

```text
http://localhost:8080/swagger-ui.html
```

- ✅ Interfaccia interattiva per testare tutte le API
- ✅ Documentazione automatica degli endpoint
- ✅ Esempi di request/response
- ✅ Test immediato senza tools esterni

#### 2. Postman / Insomnia 🚀

Importa le collection delle API dalla cartella `/Api`:

- `api pubbliche.json` - Endpoint pubblici (catalogo, info)
- `api produttore.json` - Endpoint specifici produttore
- `api acquirente.json` - Endpoint specifici acquirente
- `api trasformatore.json` - Endpoint trasformatore
- ... (altri ruoli)

---

### 🔐 Autenticazione e Sicurezza

#### Registrazione Nuovo Utente

**Via Frontend:**

1. Clicca su "Registrati" nella homepage
2. Compila il form di registrazione
3. Seleziona il ruolo desiderato
4. Conferma email (se abilitato)

**Via API (cURL):**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mario.rossi",
    "email": "mario.rossi@example.com",
    "password": "SecurePass123!",
    "nome": "Mario",
    "cognome": "Rossi",
    "ruolo": "PRODUTTORE",
    "telefono": "+39 123 456 7890",
    "indirizzo": "Via Roma 1, 12345 Città"
  }'
```

#### Login

**Via Frontend:**

1. Clicca su "Accedi"
2. Inserisci username/email e password
3. Redirect automatico alla dashboard

**Via API:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mario.rossi",
    "password": "SecurePass123!"
  }'
```

**Risposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "refreshToken": "refresh-token-here",
  "username": "mario.rossi",
  "email": "mario.rossi@example.com",
  "roles": ["ROLE_PRODUTTORE"]
}
```

#### Utilizzo Token JWT

Per chiamate API protette, includi il token nell'header:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Il frontend gestisce automaticamente l'autenticazione tramite interceptor HTTP.

---

### 📝 Esempi di Utilizzo API

#### Aggiungere un Prodotto (Produttore)

```bash
curl -X POST http://localhost:8080/api/prodotti \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Pomodori Bio del Vesuvio",
    "descrizione": "Pomodori biologici certificati, coltivati alle pendici del Vesuvio",
    "prezzo": 3.50,
    "quantitaDisponibile": 100,
    "unitaMisura": "KG",
    "categoria": "VERDURA",
    "sottocategoria": "ORTAGGI",
    "metodoColtivazione": {
      "tipo": "BIOLOGICO",
      "descrizione": "Coltivazione senza pesticidi chimici"
    },
    "certificazioni": ["BIO", "KM0", "DOP"],
    "coordinate": {
      "latitudine": 40.8218,
      "longitudine": 14.4264
    }
  }'
```

#### Ricerca Prodotti (Pubblico)

```bash
curl -X GET "http://localhost:8080/api/prodotti/ricerca?categoria=VERDURA&prezzoMax=5.00&certificazione=BIO"
```

#### Creare un Ordine (Acquirente)

```bash
curl -X POST http://localhost:8080/api/ordini \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "articoli": [
      {
        "prodottoId": 1,
        "quantita": 5
      },
      {
        "prodottoId": 3,
        "quantita": 2
      }
    ],
    "indirizzoConsegna": "Via Roma 10, 12345 Città",
    "note": "Consegna preferibilmente al mattino"
  }'
```

#### Creare un Evento (Animatore)

```bash
curl -X POST http://localhost:8080/api/eventi \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titolo": "Fiera del Biologico 2024",
    "descrizione": "Grande fiera dedicata ai prodotti biologici locali",
    "dataInizio": "2024-05-15T09:00:00",
    "dataFine": "2024-05-15T18:00:00",
    "luogo": "Piazza del Comune",
    "maxPartecipanti": 500,
    "tipoEvento": "FIERA",
    "coordinate": {
      "latitudine": 40.8218,
      "longitudine": 14.4264
    }
  }'
```

---

### 🗄️ Console H2 Database (Development)

Per ispezionare il database durante lo sviluppo:

**URL:** `http://localhost:8080/h2-console`

**Credenziali:**

```properties
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (lascia vuoto)
```

**Query utili:**

```sql
-- Visualizza tutti gli utenti
SELECT * FROM UTENTE;

-- Conta prodotti per categoria
SELECT categoria, COUNT(*) FROM PRODOTTO GROUP BY categoria;

-- Ordini recenti
SELECT * FROM ORDINE ORDER BY data_creazione DESC LIMIT 10;

-- Prodotti con certificazione BIO
SELECT * FROM PRODOTTO WHERE certificazioni LIKE '%BIO%';
```

---

### 🎨 Personalizzazione Frontend

#### Cambiare tema colori

Modifica `src/styles/_variables.scss`:

```scss
$primary-color: #4CAF50;  // Verde per tema agricolo
$secondary-color: #FF9800; // Arancione per accenti
$accent-color: #2196F3;    // Blu per link e azioni
```

#### Abilitare Dark Mode

Nel component settings o profilo utente, utilizza il toggle tema che modifica automaticamente il Material Theme.

---


## Struttura del Progetto

Il progetto segue una struttura modulare ben organizzata:

### 📂 Struttura Backend (Spring Boot)

```text
piattaforma_agricola_locale/
│
├── src/main/java/it/unicam/cs/ids/
│   └── piattaforma_agricola_locale/
│       ├── config/              # Configurazioni (Security, CORS, JWT)
│       ├── controller/          # REST Controllers
│       │   ├── auth/           # Autenticazione endpoints
│       │   ├── prodotto/       # CRUD prodotti
│       │   ├── ordine/         # Gestione ordini
│       │   ├── evento/         # Eventi e prenotazioni
│       │   └── ...
│       ├── model/              # Domain Model
│       │   ├── entity/         # JPA Entities
│       │   ├── dto/            # Data Transfer Objects
│       │   └── enums/          # Enumerazioni
│       ├── repository/         # JPA Repositories
│       ├── service/            # Business Logic
│       │   ├── impl/           # Service implementations
│       │   └── factory/        # Factory patterns
│       ├── security/           # JWT, Filters, Security Config
│       ├── exception/          # Custom Exceptions & Handlers
│       ├── mapper/             # MapStruct Mappers
│       └── util/               # Utility classes
│
├── src/main/resources/
│   ├── application.properties           # Configurazione principale
│   ├── application-dev.properties       # Profilo development
│   ├── application-prod.properties      # Profilo production
│   └── db/
│       └── performance-indexes.sql      # Ottimizzazioni DB
│
└── src/test/                            # Test unitari e integrazione
```

### 📂 Struttura Frontend (Angular)

```text
frontend/piattaforma-agricola/
│
├── src/
│   ├── app/
│   │   ├── core/                      # Servizi e componenti core
│   │   │   ├── guards/               # Route guards (Auth, Role)
│   │   │   ├── interceptors/         # HTTP Interceptors (JWT, Error)
│   │   │   ├── services/             # Servizi globali
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── api.service.ts
│   │   │   │   └── notification.service.ts
│   │   │   ├── models/               # Interfaces e Types
│   │   │   └── layout/               # Layout components
│   │   │       ├── header/
│   │   │       ├── sidebar/
│   │   │       └── footer/
│   │   │
│   │   ├── features/                  # Feature Modules (Lazy Loaded)
│   │   │   ├── auth/                 # Login, Registrazione
│   │   │   │   ├── login/
│   │   │   │   ├── register/
│   │   │   │   └── auth.routes.ts
│   │   │   ├── catalogo/             # Catalogo prodotti pubblico
│   │   │   ├── dashboard/            # Dashboard multi-ruolo
│   │   │   ├── carrello-ordini/      # Carrello e gestione ordini
│   │   │   ├── eventi/               # Eventi e prenotazioni
│   │   │   ├── processi/             # Processi trasformazione
│   │   │   ├── profilo/              # Gestione profilo utente
│   │   │   ├── gestione-utenti/      # Admin - gestione utenti
│   │   │   ├── landing/              # Homepage pubblica
│   │   │   └── public/               # Pagine pubbliche
│   │   │
│   │   ├── shared/                    # Componenti condivisi
│   │   │   ├── components/           # UI Components riusabili
│   │   │   ├── directives/           # Custom Directives
│   │   │   └── pipes/                # Custom Pipes
│   │   │
│   │   ├── store/                     # NgRx State Management
│   │   │   ├── auth/                 # Auth state
│   │   │   ├── cart/                 # Carrello state
│   │   │   ├── products/             # Prodotti state
│   │   │   └── app.state.ts          # Root state
│   │   │
│   │   ├── app.config.ts             # App configuration
│   │   ├── app.routes.ts             # Routing configuration
│   │   └── app.component.ts          # Root component
│   │
│   ├── environments/                  # Environment configs
│   │   ├── environment.ts            # Development
│   │   └── environment.prod.ts       # Production
│   │
│   ├── styles/                        # Global styles
│   │   ├── _variables.scss           # SCSS variables
│   │   ├── _mixins.scss              # SCSS mixins
│   │   └── _themes.scss              # Material themes
│   │
│   ├── assets/                        # Static assets
│   │   ├── images/
│   │   ├── icons/
│   │   └── i18n/                     # Traduzioni
│   │
│   ├── index.html                     # HTML principale
│   ├── main.ts                        # Bootstrap applicazione
│   └── styles.scss                    # Styles globali
│
├── angular.json                       # Angular CLI config
├── package.json                       # Dependencies npm
├── tsconfig.json                      # TypeScript config
└── proxy.conf.json                    # Proxy configuration

```

### 🗂️ Cartelle Principali Progetto

```text
piattaforma_agricola_locale/
├── Api/                               # Collection Postman/Insomnia
├── docs/                              # Documentazione API
├── frontend/                          # Applicazione Angular
├── src/                               # Codice sorgente backend
├── target/                            # Build output (Maven)
├── data/                              # Database file (H2 persistente)
├── logs/                              # Application logs
├── pom.xml                            # Maven configuration
├── mvnw / mvnw.cmd                    # Maven wrapper
└── README.md                          # Questo file
```

### 🎯 Pattern di Organizzazione

#### Backend

- **Package by Layer**: Organizzazione per layer architetturale
- **Separation of Concerns**: Separazione netta tra presentation, business logic, data access
- **DTO Pattern**: Trasferimento dati senza esporre entità

#### Frontend

- **Feature-Based**: Moduli organizzati per funzionalità business
- **Lazy Loading**: Caricamento moduli on-demand
- **Smart/Dumb Components**: Separazione componenti container e presentation
- **Reactive State Management**: NgRx per stato centralizzato

---

### 🔗 Swagger/OpenAPI

Swagger UI interattivo disponibile a:

```text
http://localhost:8080/swagger-ui.html
```

Con:

- Schema OpenAPI 3.0
- Try-it-out per test immediato
- Esempi request/response
- Modelli dati completi


---

## Design Pattern e Architettura

### 🏗️ Architettura del Sistema

#### Architettura Full-Stack

```text
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Angular SPA (http://localhost:4200)          │  │
│  │  Components │ Services │ Guards │ Interceptors       │  │
│  │            NgRx State Management                     │  │
│  └──────────────────────┬───────────────────────────────┘  │
└─────────────────────────┼───────────────────────────────────┘
                          │ HTTP/REST + JWT
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                      SERVER LAYER                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │     Spring Boot API (http://localhost:8080)          │  │
│  │                                                       │  │
│  │  ┌─────────────────────────────────────────────┐   │  │
│  │  │        Presentation Layer                   │   │  │
│  │  │  REST Controllers │ JWT Filter │ CORS       │   │  │
│  │  └──────────────────┬──────────────────────────┘   │  │
│  │                     │                               │  │
│  │  ┌──────────────────▼──────────────────────────┐   │  │
│  │  │          Service Layer                      │   │  │
│  │  │  Business Logic │ Validation │ Transactions│   │  │
│  │  └──────────────────┬──────────────────────────┘   │  │
│  │                     │                               │  │
│  │  ┌──────────────────▼──────────────────────────┐   │  │
│  │  │        Repository Layer                     │   │  │
│  │  │  Spring Data JPA │ Query Methods           │   │  │
│  │  └──────────────────┬──────────────────────────┘   │  │
│  │                     │                               │  │
│  │  ┌──────────────────▼──────────────────────────┐   │  │
│  │  │          Model Layer                        │   │  │
│  │  │  Entities │ DTOs │ Mappers (MapStruct)     │   │  │
│  │  └─────────────────────────────────────────────┘   │  │
│  └──────────────────────┬────────────────────────────┘  │
└─────────────────────────┼───────────────────────────────┘
                          │ JDBC
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                     DATABASE LAYER                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              H2 Database (In-Memory/File)            │  │
│  │  Tables │ Indexes │ Constraints                      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

#### Backend - Layered Architecture

```text
┌─────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                    │
│  REST Controllers - Gestione HTTP Request/Response      │
│  • @RestController                                      │
│  • Request/Response DTOs                                │
│  • Input Validation (@Valid)                            │
│  • Exception Handling (@ExceptionHandler)               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├── Dependency Injection (Spring IoC)
                     │
┌────────────────────▼────────────────────────────────────┐
│                    SERVICE LAYER                        │
│  Business Logic - Orchestrazione e Regole Business      │
│  • @Service                                             │
│  • Transaction Management (@Transactional)              │
│  • Business Validation                                  │
│  • DTO ↔ Entity Mapping (MapStruct)                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├── Dependency Injection
                     │
┌────────────────────▼────────────────────────────────────┐
│                  REPOSITORY LAYER                       │
│  Data Access - Persistenza e Query Database             │
│  • @Repository                                          │
│  • JpaRepository<Entity, ID>                            │
│  • Query Methods / @Query                               │
│  • Specification API (filtri complessi)                 │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├── JPA/Hibernate ORM
                     │
┌────────────────────▼────────────────────────────────────┐
│                     MODEL LAYER                         │
│  Domain Model - Rappresentazione Dati                   │
│  • @Entity (JPA Entities)                               │
│  • DTOs (Data Transfer Objects)                         │
│  • Enums                                                │
│  • Value Objects                                        │
└─────────────────────────────────────────────────────────┘
```

#### Frontend - Component Architecture

```text
┌─────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                     │
│  Components - UI e User Interaction                     │
│  • Smart Components (Container)                         │
│  • Dumb Components (Presentational)                     │
│  • Templates (HTML + Angular directives)                │
│  • Material UI Components                               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├── Dependency Injection
                     │
┌────────────────────▼────────────────────────────────────┐
│                    SERVICE LAYER                        │
│  Business Logic Frontend                                │
│  • API Services (HTTP Client)                           │
│  • State Management Services                            │
│  • Utility Services                                     │
│  • Authentication Service                               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├── HTTP/REST
                     │
┌────────────────────▼────────────────────────────────────┐
│                   STATE MANAGEMENT                      │
│  NgRx Store - Redux Pattern                             │
│  • Store (stato centralizzato)                          │
│  • Actions (eventi)                                     │
│  • Reducers (logica stato)                              │
│  • Effects (side effects asincroni)                     │
│  • Selectors (query stato)                              │
└─────────────────────────────────────────────────────────┘
```

---



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

- **S**ingle Responsibility Principle - Ogni classe ha una singola responsabilità
- **O**pen/Closed Principle - Aperto per estensione, chiuso per modifica
- **L**iskov Substitution Principle - Sostituibilità delle sottoclassi
- **I**nterface Segregation Principle - Interfacce specifiche e mirate
- **D**ependency Inversion Principle - Dipendenze su astrazioni

---

## Come Contribuire

Siamo felici di accogliere contributi dalla community! Ecco come puoi aiutare:

### 🚀 Processo di Contribuzione

1. **Fork** del repository su GitHub
2. **Clona** il tuo fork localmente

   ```bash
   git clone https://github.com/TUO-USERNAME/piattaforma_agricola_locale.git
   cd piattaforma_agricola_locale
   ```

3. **Crea** un branch per la tua feature/fix

   ```bash
   git checkout -b feature/nuova-funzionalita
   # oppure
   git checkout -b fix/correzione-bug
   ```

4. **Implementa** le modifiche seguendo le linee guida
5. **Testa** le tue modifiche

   ```bash
   # Backend
   ./mvnw clean verify
   
   # Frontend
   cd frontend/piattaforma-agricola
   npm test -- --watch=false
   ```

6. **Commit** con messaggi descrittivi

   ```bash
   git add .
   git commit -m "feat: aggiunge filtro avanzato catalogo prodotti"
   ```

7. **Push** del branch

   ```bash
   git push origin feature/nuova-funzionalita
   ```

8. **Apri** una Pull Request su GitHub con descrizione dettagliata

### 📋 Linee Guida Codice

#### Backend (Java/Spring Boot)

```java
// ✅ Buone pratiche
@Service
@Transactional
public class ProdottoServiceImpl implements IProdottoService {
    
    private final IProdottoRepository prodottoRepository;
    
    // Dependency Injection via costruttore
    public ProdottoServiceImpl(IProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }
    
    // JavaDoc per metodi pubblici
    /**
     * Recupera tutti i prodotti per categoria.
     * @param categoria la categoria da filtrare
     * @return lista di prodotti
     */
    @Override
    public List<ProdottoDTO> findByCategoria(Categoria categoria) {
        // Implementazione...
    }
}
```

**Convenzioni:**

- ✅ Nomi classi: `PascalCase`
- ✅ Nomi metodi: `camelCase`
- ✅ Costanti: `UPPER_SNAKE_CASE`
- ✅ Package: lowercase
- ✅ JavaDoc per API pubbliche
- ✅ Dependency Injection via costruttore
- ✅ Usare `@Override` quando appropriato
- ✅ Gestione eccezioni appropriata

#### Frontend (TypeScript/Angular)

```typescript
// ✅ Buone pratiche
@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './catalogo.component.html',
  styleUrls: ['./catalogo.component.scss']
})
export class CatalogoComponent implements OnInit {
  // Signals per stato reattivo
  products = signal<Prodotto[]>([]);
  loading = signal<boolean>(false);
  
  private prodottoService = inject(ProdottoService);
  
  ngOnInit(): void {
    this.loadProducts();
  }
  
  /**
   * Carica la lista dei prodotti dal backend
   */
  private loadProducts(): void {
    this.loading.set(true);
    this.prodottoService.getAll().subscribe({
      next: (products) => this.products.set(products),
      error: (err) => console.error('Errore caricamento', err),
      complete: () => this.loading.set(false)
    });
  }
}
```

**Convenzioni:**

- ✅ Nomi componenti: `kebab-case.component.ts`
- ✅ Classi: `PascalCase`
- ✅ Variabili/metodi: `camelCase`
- ✅ Costanti: `UPPER_SNAKE_CASE`
- ✅ Usare signals per stato reattivo (Angular 20+)
- ✅ Dependency injection con `inject()`
- ✅ Standalone components
- ✅ Template type-safe
- ✅ Unsubscribe dagli observable (o usare `async` pipe)

### 🎯 Commit Message Convention

Seguiamo la [Conventional Commits](https://www.conventionalcommits.org/):

```bash
<tipo>(<scope>): <descrizione>

[corpo opzionale]

[footer opzionale]
```

**Tipi:**

- `feat`: nuova funzionalità
- `fix`: correzione bug
- `docs`: solo documentazione
- `style`: formattazione, punto e virgola, etc
- `refactor`: refactoring codice
- `test`: aggiunta test
- `chore`: manutenzione

**Esempi:**

```bash
feat(catalogo): aggiunge filtro per certificazioni bio
fix(auth): corregge refresh token expiration
docs(readme): aggiorna sezione installazione frontend
test(prodotto): aggiunge test per validazione prezzi
refactor(service): migliora gestione errori API
```

### 📝 Pull Request Guidelines

La tua PR dovrebbe:

- ✅ Avere un titolo chiaro e descrittivo
- ✅ Includere una descrizione dettagliata delle modifiche
- ✅ Referenziare issue correlate (es. "Fixes #123")
- ✅ Passare tutti i test CI/CD
- ✅ Avere coverage >= 70% per nuovo codice
- ✅ Seguire le convenzioni di codice
- ✅ Includere documentazione aggiornata se necessario
- ✅ Screenshot/GIF per modifiche UI

**Template PR:**

```markdown
## Descrizione
Breve descrizione delle modifiche

## Tipo di modifica
- [ ] Bug fix
- [ ] Nuova feature
- [ ] Breaking change
- [ ] Documentazione

## Checklist
- [ ] Test eseguiti e passano
- [ ] Documentazione aggiornata
- [ ] Codice segue style guide
- [ ] Auto-review completata
```

### 🐛 Segnalazione Bug

Usa il template issue per bug:

**Informazioni necessarie:**

- Versione applicazione
- Browser/OS (per frontend)
- Java version (per backend)
- Passi per riprodurre
- Comportamento atteso vs effettivo
- Screenshot/logs

### 💡 Proporre Nuove Feature

Apri una issue di tipo "Feature Request":

1. Descrivi il problema che risolve
2. Proponi una soluzione
3. Discuti alternative considerate
4. Indica impatto e priorità

### 🎨 Aree di Contribuzione

#### Backend

- 🐛 **Bug Fix**: Correzione errori logica business
- ✨ **Features**: Nuovi endpoint API, servizi
- 🔒 **Security**: Miglioramenti sicurezza
- ⚡ **Performance**: Ottimizzazioni query, caching
- 📚 **Docs**: JavaDoc, API documentation

#### Frontend

- 🐛 **Bug Fix**: Correzione UI/UX
- ✨ **Features**: Nuovi componenti, pagine
- 🎨 **UI/UX**: Miglioramenti design
- ♿ **Accessibility**: WCAG compliance
- 🌐 **I18n**: Traduzioni, localizzazione
- 📱 **Responsive**: Mobile optimization

#### Documentazione

- 📖 README miglioramenti
- 📋 Guide utente
- 🎓 Tutorial e esempi
- 🗺️ Diagrammi architettura

#### Testing

- 🧪 Unit tests
- 🔗 Integration tests
- 🚀 E2E tests
- 📊 Migliorare coverage

### 👥 Codice di Condotta

Ci aspettiamo che tutti i contributori:

- Siano rispettosi e inclusivi
- Forniscano feedback costruttivo
- Accettino critiche costruttive
- Collaborino in modo professionale




---

## Crediti

### 👨‍💻 Team di Sviluppo

Questo progetto è stato sviluppato come parte del corso di **Ingegneria del Software (IDS)** presso l'**Università di Camerino**.

**Sviluppatori Principali:**

- **[Angelo Albanesi](https://github.com/AngeloAlbanesi)** - Full-Stack Development
  - Backend architecture & API design
  - Frontend implementation (Angular)
  - Database design & optimization
  
- **[Paolo Campanari](https://github.com/PaoloCampanari)** - Backend Development
  - Business logic implementation
  - Design patterns integration
  - Testing & quality assurance

- **[Lorenzo Donadio](https://github.com/Lor3Don4)** - Backend Development & Documentation
  - Service layer implementation
  - API documentation
  - Technical documentation

### 🏫 Istituzione

**Università degli Studi di Camerino**

- Scuola di Scienze e Tecnologie
- Corso di Laurea in Informatica
- A.A. 2024/2025

### 📚 Risorse e Riferimenti

#### Framework e Librerie

**Backend:**

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Security](https://spring.io/projects/spring-security) - Security framework
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data persistence
- [Hibernate ORM](https://hibernate.org/) - Object-relational mapping
- [MapStruct](https://mapstruct.org/) - Bean mapping
- [Lombok](https://projectlombok.org/) - Boilerplate reduction
- [H2 Database](https://www.h2database.com/) - Embedded database
- [JJWT](https://github.com/jwtk/jjwt) - JWT library

**Frontend:**

- [Angular](https://angular.dev/) - Web application framework
- [Angular Material](https://material.angular.io/) - UI component library
- [NgRx](https://ngrx.io/) - State management
- [RxJS](https://rxjs.dev/) - Reactive programming
- [TypeScript](https://www.typescriptlang.org/) - Programming language

#### Documentazione Tecnica

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Angular Documentation](https://angular.dev/overview)
- [Material Design Guidelines](https://m3.material.io/)
- [REST API Design Best Practices](https://restfulapi.net/)
- [JWT Introduction](https://jwt.io/introduction)

#### Servizi Esterni

- [OpenStreetMap](https://www.openstreetmap.org/) - Mappe e geolocalizzazione
- [OSM Nominatim API](https://nominatim.org/) - Geocoding


---

## Licenza

Questo progetto è rilasciato sotto la **Licenza MIT**.

```text
MIT License

Copyright (c) 2024-2025 Università di Camerino - Piattaforma Agricola Locale
Angelo Albanesi, Paolo Campanari, Lorenzo Donadio

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

---



### Repository

- **Repository GitHub**: [https://github.com/AngeloAlbanesi/piattaforma_agricola_locale](https://github.com/AngeloAlbanesi/piattaforma_agricola_locale)
- **Wiki**: [Documentazione estesa](https://github.com/AngeloAlbanesi/piattaforma_agricola_locale/wiki)

---


<div align="center">

### Made with ❤️ by Team ExIng

**Università di Camerino - Ingegneria del Software**



---

**© 2024-2025 Università di Camerino. Rilasciato sotto Licenza MIT.**

</div>
