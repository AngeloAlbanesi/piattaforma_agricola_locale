import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';

import { CatalogItem, CERTIFICATION_LABELS, CERTIFICATION_ICONS } from '../../../../core/models/catalog.models';

/**
 * Componente card per visualizzare un item del catalogo (prodotto o pacchetto)
 */
@Component({
    selector: 'app-catalog-item-card',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatTooltipModule,
        MatBadgeModule
    ],
    templateUrl: './catalog-item-card.component.html',
    styleUrls: ['./catalog-item-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogItemCardComponent {
    @Input() item!: CatalogItem;
    @Input() isAuthenticated = false;
    @Input() viewMode: 'grid' | 'list' = 'grid';

    @Output() addToCart = new EventEmitter<CatalogItem>();
    @Output() viewDetails = new EventEmitter<CatalogItem>();
    @Output() clickAzienda = new EventEmitter<number>();

    /**
     * Gestisce il click sul pulsante aggiungi al carrello
     */
    onAddToCart(event: Event): void {
        event.stopPropagation();
        this.addToCart.emit(this.item);
    }

    /**
     * Gestisce il click sulla card per vedere i dettagli
     */
    onViewDetails(): void {
        this.viewDetails.emit(this.item);
    }

    /**
     * Gestisce il click sull'azienda
     */
    onClickAzienda(event: Event): void {
        event.stopPropagation();
        this.clickAzienda.emit(this.item.azienda.id);
    }

    /**
     * Ottiene l'URL dell'immagine o un placeholder SVG inline
     */
    get imageUrl(): string {
        if (this.item.immagineUrl) {
            return this.item.immagineUrl;
        }
        // Placeholder SVG inline basato sul tipo
        const color = this.item.tipo === 'PRODOTTO' ? '%234caf50' : '%232196f3';
        const icon = this.item.tipo === 'PRODOTTO' ? 'shopping_basket' : 'inventory_2';
        return `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'%3E%3Cdefs%3E%3ClinearGradient id='grad' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:${color};stop-opacity:0.3' /%3E%3Cstop offset='100%25' style='stop-color:${color};stop-opacity:0.1' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='400' height='300' fill='url(%23grad)'/%3E%3Ctext x='50%25' y='50%25' font-family='Arial' font-size='24' fill='${color}' text-anchor='middle' dominant-baseline='middle'%3E${this.item.tipo}%3C/text%3E%3C/svg%3E`;
    }

    /**
     * Verifica se l'item è disponibile
     */
    get isAvailable(): boolean {
        return this.item.quantitaDisponibile > 0;
    }

    /**
     * Verifica se l'item ha uno sconto
     */
    get hasDiscount(): boolean {
        return !!(this.item.sconto && this.item.sconto > 0);
    }

    /**
     * Ottiene il badge del tipo
     */
    get typeBadge(): string {
        return this.item.tipo === 'PRODOTTO' ? 'Prodotto' : 'Pacchetto';
    }

    /**
     * Ottiene la classe CSS per il badge del tipo
     */
    get typeBadgeClass(): string {
        return this.item.tipo === 'PRODOTTO' ? 'badge-prodotto' : 'badge-pacchetto';
    }

    /**
     * Formatta il prezzo
     */
    formatPrice(price: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(price);
    }

    /**
     * Ottiene l'etichetta per una certificazione
     */
    getCertificationLabel(cert: string): string {
        return CERTIFICATION_LABELS[cert] || cert;
    }

    /**
     * Ottiene l'icona per una certificazione
     */
    getCertificationIcon(cert: string): string {
        return CERTIFICATION_ICONS[cert] || 'verified';
    }

    /**
     * Ottiene il messaggio di disponibilità
     */
    get availabilityMessage(): string {
        if (this.item.quantitaDisponibile === 0) {
            return 'Non disponibile';
        }
        if (this.item.quantitaDisponibile < 10) {
            return `Solo ${this.item.quantitaDisponibile} disponibili`;
        }
        return 'Disponibile';
    }

    /**
     * Ottiene la classe CSS per la disponibilità
     */
    get availabilityClass(): string {
        if (this.item.quantitaDisponibile === 0) {
            return 'availability-none';
        }
        if (this.item.quantitaDisponibile < 10) {
            return 'availability-low';
        }
        return 'availability-ok';
    }
}

