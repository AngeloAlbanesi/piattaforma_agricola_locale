/**
 * Modelli specifici per le API Admin (Gestore Piattaforma)
 */

// === ENUMS ===

export enum StatoAccreditamento {
    PENDING = 'PENDING',
    ACCREDITATO = 'ACCREDITATO',
    SOSPESO = 'SOSPESO',
    RIFIUTATO = 'RIFIUTATO'
}

export enum TipoRuolo {
    ACQUIRENTE = 'ACQUIRENTE',
    PRODUTTORE = 'PRODUTTORE',
    TRASFORMATORE = 'TRASFORMATORE',
    DISTRIBUTORE = 'DISTRIBUTORE',
    CURATORE = 'CURATORE',
    ANIMATORE = 'ANIMATORE',
    GESTORE_PIATTAFORMA = 'GESTORE_PIATTAFORMA'
}

export enum AccreditamentoStato {
    IN_ATTESA_REVISIONE = 'IN_ATTESA_REVISIONE',
    APPROVATO = 'APPROVATO',
    RIFIUTATO = 'RIFIUTATO',
    SOSPESO = 'SOSPESO'
}

// === DTO documentati ===

/**
 * DTO for public user information from backend
 * Matches UserPublicDTO.java structure
 */
export interface UserPublicDTO {
    idUtente: number;
    nome: string;
    cognome: string;
    tipoRuolo: TipoRuolo | string;
    isAttivo: boolean;
    statoAccreditamento: StatoAccreditamento | string;
}

/**
 * DTO for admin user list (extended version with more details)
 */
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

// === Tipi di supporto ===
export type UtenteTipo = 'ACQUIRENTE' | 'VENDITORE' | 'CURATORE' | 'ANIMATORE';
