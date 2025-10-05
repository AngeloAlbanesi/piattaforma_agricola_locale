import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError, of, finalize } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';

import { AziendaService } from '../../../../../core/services/azienda.service';
import { AziendaDetailDTO } from '../../../../../core/models/trasformatore.models';
import { AuthService, ROLES } from '../../../../../core/services/auth.service';

/**
 * Componente riutilizzabile per visualizzare i dati aziendali
 * Mostra nome azienda, partita IVA, indirizzo, descrizione e stato accreditamento
 */
@Component({
    selector: 'app-company-data-card',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatTooltipModule,
        MatChipsModule
    ],
    templateUrl: './company-data-card.component.html',
    styleUrls: ['./company-data-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CompanyDataCardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    isLoading = false;
    company: AziendaDetailDTO | null = null;
    hasError = false;
    isCompanyUser = false;

    constructor(
        private aziendaService: AziendaService,
        private authService: AuthService,
        private router: Router,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.checkUserRole();
        if (this.isCompanyUser) {
            this.loadCompanyData();
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Verifica se l'utente ha un ruolo aziendale
     */
    private checkUserRole(): void {
        const userRole = this.authService.getRole();
        this.isCompanyUser = [
            ROLES.PRODUTTORE,
            ROLES.TRASFORMATORE,
            ROLES.DISTRIBUTORE_TIPICITA
        ].includes(userRole as any);

        console.log('🏢 Verifica ruolo aziendale:', { userRole, isCompanyUser: this.isCompanyUser });
        this.cdr.markForCheck(); // Forza change detection dopo la verifica del ruolo
    }

    /**
     * Carica i dati dell'azienda
     */
    private loadCompanyData(): void {
        this.isLoading = true;
        this.hasError = false;
        this.cdr.markForCheck(); // Forza change detection per mostrare lo spinner

        console.log('🔄 Inizio caricamento dati azienda...');

        this.aziendaService.getMyCompany()
            .pipe(
                takeUntil(this.destroy$),
                catchError(error => {
                    console.error('❌ Errore caricamento dati azienda:', error);
                    this.hasError = true;

                    // Non mostrare snackbar - l'errore è già visibile nella card
                    // Questo evita notifiche bloccate che non si chiudono

                    return of(null);
                }),
                finalize(() => {
                    this.isLoading = false;
                    console.log('✅ Caricamento dati azienda completato');
                    this.cdr.markForCheck(); // Forza change detection dopo il caricamento
                })
            )
            .subscribe(company => {
                this.company = company;
                console.log('📊 Dati azienda ricevuti:', company);
                this.cdr.markForCheck(); // Forza change detection quando i dati sono pronti
            });
    }

    /**
     * Naviga alla pagina di gestione azienda
     */
    manageCompany(): void {
        // TODO: Implementare navigazione a pagina gestione azienda quando disponibile
        this.snackBar.open(
            'Funzionalità di gestione azienda in sviluppo.',
            'Chiudi',
            { duration: 3000, panelClass: 'info-snackbar' }
        );
    }

    /**
     * Ricarica i dati dell'azienda
     */
    refreshCompanyData(): void {
        this.loadCompanyData();
    }

    /**
     * Restituisce l'etichetta formattata dello stato accreditamento
     */
    getAccreditationStatusLabel(status: string): string {
        return this.aziendaService.getStatoAccreditamentoLabel(status);
    }

    /**
     * Restituisce il colore dello stato accreditamento
     */
    getAccreditationStatusColor(status: string): 'primary' | 'accent' | 'warn' | undefined {
        return this.aziendaService.getStatoAccreditamentoColor(status);
    }

    /**
     * Restituisce l'etichetta formattata della tipologia azienda
     */
    getCompanyTypeLabel(type: string): string {
        return this.aziendaService.getTipologiaAziendaLabel(type);
    }

    /**
     * Formatta l'indirizzo completo
     */
    formatFullAddress(company: AziendaDetailDTO): string {
        if (!company.indirizzo) return 'Non disponibile';

        const address = company.indirizzo;
        const parts = [
            address.via,
            address.civico,
            address.cap,
            address.citta,
            address.provincia,
            address.paese
        ].filter(part => part && part.trim() !== '');

        return parts.join(', ');
    }

    /**
     * Formatta la data
     */
    formatDate(date: string): string {
        return this.aziendaService.formatDate(date);
    }

    /**
     * Verifica se un campo opzionale è valorizzato
     */
    hasValue(value?: string): boolean {
        return value !== null && value !== undefined && value.trim() !== '';
    }
}