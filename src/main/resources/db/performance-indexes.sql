-- ========================================
-- DATABASE PERFORMANCE OPTIMIZATION
-- Indici per migliorare le performance delle query più frequenti
-- ========================================

-- INDICI PER TABELLA PRODOTTI
-- Indice su categoria per ricerche filtrate
CREATE INDEX IF NOT EXISTS idx_prodotto_categoria ON Prodotto(categoria);

-- Indice su venditore_id per query di ownership e listing
CREATE INDEX IF NOT EXISTS idx_prodotto_venditore ON Prodotto(venditore_id);

-- Indice su status per filtrare prodotti attivi
CREATE INDEX IF NOT EXISTS idx_prodotto_status ON Prodotto(status);

-- Indice composito per ricerche complesse (categoria + status)
CREATE INDEX IF NOT EXISTS idx_prodotto_categoria_status ON Prodotto(categoria, status);

-- Indice per ricerche testuali su nome e descrizione
CREATE INDEX IF NOT EXISTS idx_prodotto_nome ON Prodotto(nome);

-- ========================================
-- INDICI PER TABELLA ORDINI
-- ========================================

-- Indice su acquirente per query di storico ordini
CREATE INDEX IF NOT EXISTS idx_ordine_acquirente ON Ordine(acquirente_id);

-- Indice su status per filtrare ordini per stato
CREATE INDEX IF NOT EXISTS idx_ordine_status ON Ordine(status);

-- Indice su data per ordinamenti temporali
CREATE INDEX IF NOT EXISTS idx_ordine_data ON Ordine(data_ordine);

-- Indice composito per query di dashboard venditori
CREATE INDEX IF NOT EXISTS idx_ordine_status_data ON Ordine(status, data_ordine);

-- ========================================
-- INDICI PER TABELLA UTENTI
-- ========================================

-- Indice unico su email (già dovrebbe esserci)
CREATE UNIQUE INDEX IF NOT EXISTS idx_utente_email ON Utente(email);

-- Indice su ruolo per query di autorizzazione
CREATE INDEX IF NOT EXISTS idx_utente_ruolo ON Utente(ruolo);

-- Indice su data_registrazione per analytics
CREATE INDEX IF NOT EXISTS idx_utente_data_reg ON Utente(data_registrazione);

-- ========================================
-- INDICI PER TABELLA EVENTI
-- ========================================

-- Indice su organizzatore per ownership validation
CREATE INDEX IF NOT EXISTS idx_evento_organizzatore ON Evento(organizzatore_id);

-- Indice su data per filtri temporali
CREATE INDEX IF NOT EXISTS idx_evento_data ON Evento(data_evento);

-- Indice su categoria per filtri tematici
CREATE INDEX IF NOT EXISTS idx_evento_categoria ON Evento(categoria);

-- Indice composito per ricerche eventi attivi
CREATE INDEX IF NOT EXISTS idx_evento_data_attivo ON Evento(data_evento, attivo);

-- ========================================
-- INDICI PER TABELLA AZIENDE
-- ========================================

-- Indice su proprietario per ownership validation
CREATE INDEX IF NOT EXISTS idx_azienda_proprietario ON Azienda(proprietario_id);

-- Indice su settore per filtri settoriali
CREATE INDEX IF NOT EXISTS idx_azienda_settore ON Azienda(settore);

-- Indice su status per filtrare aziende attive
CREATE INDEX IF NOT EXISTS idx_azienda_status ON Azienda(status);

-- ========================================
-- INDICI PER PERFORMANCE DI JOIN
-- ========================================

-- Indici per foreign key più utilizzate
CREATE INDEX IF NOT EXISTS idx_carrello_utente ON Carrello(utente_id);
CREATE INDEX IF NOT EXISTS idx_carrello_item_prodotto ON CarrelloItem(prodotto_id);
CREATE INDEX IF NOT EXISTS idx_ordine_item_prodotto ON OrdineItem(prodotto_id);

-- ========================================
-- INDICI PER FULL-TEXT SEARCH (H2 specifici)
-- ========================================

-- Se si vuole abilitare la ricerca full-text su H2
-- CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
-- CALL FT_INIT();
-- CALL FT_CREATE_INDEX('PUBLIC', 'PRODOTTO', 'NOME,DESCRIZIONE');

-- ========================================
-- STATISTICHE E MANUTENZIONE
-- ========================================

-- Comando per aggiornare le statistiche del database H2
-- (da eseguire periodicamente in produzione)
ANALYZE SAMPLE_SIZE 10000;

-- ========================================
-- QUERY DI VERIFICA PERFORMANCE
-- ========================================

-- Query per verificare l'utilizzo degli indici
-- SELECT * FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_SCHEMA = 'PUBLIC';

-- Query per verificare le performance (da usare con EXPLAIN PLAN)
-- EXPLAIN SELECT * FROM Prodotto WHERE categoria = 'FRUTTA' AND status = 'ATTIVO';
-- EXPLAIN SELECT * FROM Ordine WHERE acquirente_id = 1 ORDER BY data_ordine DESC;
-- EXPLAIN SELECT * FROM Evento WHERE data_evento >= CURRENT_DATE AND attivo = true;
