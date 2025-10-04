/**
 * Modelli specifici per le API Admin (Gestore Piattaforma)
 */

// === DTO documentati ===
export interface AdminUserDTO {
    idUtente: number;
    nome: string;
    cognome: string;
    email: string;
    ruolo: string;
    accreditato: boolean;
    attivo: boolean;
    dataRegistrazione: string; // ISO
}

export interface ModerationDecisionDTO {
    motivazione: string;
}

export interface CompanyModerationDTO {
    id: number;
    nomeAzienda: string;
    partitaIva: string;
    indirizzo: string;
    telefono: string;
    email: string;
    sitoWeb?: string;
    statoVerifica: string; // IN_ATTESA_REVISIONE | APPROVATO | RIFIUTATO | SOSPESO
    feedbackVerifica?: string;
}

export enum AccreditamentoStato {
    IN_ATTESA_REVISIONE = 'IN_ATTESA_REVISIONE',
    APPROVATO = 'APPROVATO',
    RIFIUTATO = 'RIFIUTATO',
    SOSPESO = 'SOSPESO'
}

// === Tipi di supporto ===
export type UtenteTipo = 'ACQUIRENTE' | 'VENDITORE' | 'CURATORE' | 'ANIMATORE';
