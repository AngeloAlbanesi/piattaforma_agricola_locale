/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */

import { StatoAccreditamento, TipoRuolo } from '../../../../core/models/admin.models';

/**
 * Utilities for Gestore Piattaforma functionality
 */

/**
 * Maps StatoAccreditamento to Material chip color
 */
export function mapStatoAccreditamentoToChipColor(stato: StatoAccreditamento | string): string {
    switch (stato) {
        case StatoAccreditamento.ACCREDITATO:
        case 'ACCREDITATO':
            return 'success';
        case StatoAccreditamento.PENDING:
        case 'PENDING':
            return 'warn';
        case StatoAccreditamento.SOSPESO:
        case 'SOSPESO':
            return 'default';
        case StatoAccreditamento.RIFIUTATO:
        case 'RIFIUTATO':
            return 'error';
        default:
            return 'default';
    }
}

/**
 * Maps StatoAccreditamento to icon
 */
export function mapStatoAccreditamentoToIcon(stato: StatoAccreditamento | string): string {
    switch (stato) {
        case StatoAccreditamento.ACCREDITATO:
        case 'ACCREDITATO':
            return 'check_circle';
        case StatoAccreditamento.PENDING:
        case 'PENDING':
            return 'pending';
        case StatoAccreditamento.SOSPESO:
        case 'SOSPESO':
            return 'pause_circle';
        case StatoAccreditamento.RIFIUTATO:
        case 'RIFIUTATO':
            return 'cancel';
        default:
            return 'help_outline';
    }
}

/**
 * Maps StatoAccreditamento to Italian display name
 */
export function mapStatoAccreditamentoToDisplayName(stato: StatoAccreditamento | string): string {
    switch (stato) {
        case StatoAccreditamento.ACCREDITATO:
        case 'ACCREDITATO':
            return 'Accreditato';
        case StatoAccreditamento.PENDING:
        case 'PENDING':
            return 'In Attesa';
        case StatoAccreditamento.SOSPESO:
        case 'SOSPESO':
            return 'Sospeso';
        case StatoAccreditamento.RIFIUTATO:
        case 'RIFIUTATO':
            return 'Rifiutato';
        default:
            return 'Sconosciuto';
    }
}

/**
 * Maps TipoRuolo to Italian display name
 */
export function mapTipoRuoloToDisplayName(tipo: TipoRuolo | string): string {
    switch (tipo) {
        case TipoRuolo.ACQUIRENTE:
        case 'ACQUIRENTE':
            return 'Acquirente';
        case TipoRuolo.PRODUTTORE:
        case 'PRODUTTORE':
            return 'Produttore';
        case TipoRuolo.TRASFORMATORE:
        case 'TRASFORMATORE':
            return 'Trasformatore';
        case TipoRuolo.DISTRIBUTORE:
        case 'DISTRIBUTORE':
            return 'Distributore';
        case TipoRuolo.CURATORE:
        case 'CURATORE':
            return 'Curatore';
        case TipoRuolo.ANIMATORE:
        case 'ANIMATORE':
            return 'Animatore';
        case TipoRuolo.GESTORE_PIATTAFORMA:
        case 'GESTORE_PIATTAFORMA':
            return 'Gestore Piattaforma';
        default:
            return tipo;
    }
}

/**
 * Maps TipoRuolo to icon
 */
export function mapTipoRuoloToIcon(tipo: TipoRuolo | string): string {
    switch (tipo) {
        case TipoRuolo.ACQUIRENTE:
        case 'ACQUIRENTE':
            return 'shopping_cart';
        case TipoRuolo.PRODUTTORE:
        case 'PRODUTTORE':
            return 'agriculture';
        case TipoRuolo.TRASFORMATORE:
        case 'TRASFORMATORE':
            return 'factory';
        case TipoRuolo.DISTRIBUTORE:
        case 'DISTRIBUTORE':
            return 'local_shipping';
        case TipoRuolo.CURATORE:
        case 'CURATORE':
            return 'verified';
        case TipoRuolo.ANIMATORE:
        case 'ANIMATORE':
            return 'event';
        case TipoRuolo.GESTORE_PIATTAFORMA:
        case 'GESTORE_PIATTAFORMA':
            return 'admin_panel_settings';
        default:
            return 'person';
    }
}

/**
 * Maps TipoRuolo to color
 */
export function mapTipoRuoloToColor(tipo: TipoRuolo | string): string {
    switch (tipo) {
        case TipoRuolo.ACQUIRENTE:
        case 'ACQUIRENTE':
            return 'primary';
        case TipoRuolo.PRODUTTORE:
        case 'PRODUTTORE':
            return 'accent';
        case TipoRuolo.TRASFORMATORE:
        case 'TRASFORMATORE':
            return 'warn';
        case TipoRuolo.DISTRIBUTORE:
        case 'DISTRIBUTORE':
            return 'primary';
        case TipoRuolo.CURATORE:
        case 'CURATORE':
            return 'accent';
        case TipoRuolo.ANIMATORE:
        case 'ANIMATORE':
            return 'warn';
        case TipoRuolo.GESTORE_PIATTAFORMA:
        case 'GESTORE_PIATTAFORMA':
            return 'primary';
        default:
            return 'default';
    }
}

/**
 * Gets user full name from nome and cognome
 */
export function getUserFullName(nome: string, cognome: string): string {
    return `${nome} ${cognome}`.trim();
}

/**
 * Formats date to Italian locale
 */
export function formatDateToItalian(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('it-IT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Formats datetime to Italian locale
 */
export function formatDateTimeToItalian(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('it-IT', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

