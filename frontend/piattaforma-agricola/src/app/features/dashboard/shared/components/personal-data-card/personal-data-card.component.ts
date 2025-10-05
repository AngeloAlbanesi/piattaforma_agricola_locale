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

import { AuthService } from '../../../../../core/services/auth.service';
import { UserDetailDTO } from '../../../../../core/models/curatore.models';

/**
 * Componente riutilizzabile per visualizzare i dati personali dell'utente
 * Mostra username, nome, cognome, email, telefono, indirizzo, ruolo e ultimo accesso
 */
@Component({
    selector: 'app-personal-data-card',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatTooltipModule
    ],
    templateUrl: './personal-data-card.component.html',
    styleUrls: ['./personal-data-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PersonalDataCardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    isLoading = false;
    profile: UserDetailDTO | null = null;
    hasError = false;

    constructor(
        private authService: AuthService,
        private router: Router,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProfile();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Carica i dati del profilo utente
     */
    private loadProfile(): void {
        this.isLoading = true;
        this.hasError = false;
        this.cdr.markForCheck(); // Forza change detection per mostrare lo spinner

        this.authService.getProfile()
            .pipe(
                takeUntil(this.destroy$),
                catchError(error => {
                    console.error('Errore caricamento profilo', error);
                    this.hasError = true;
                    this.snackBar.open(
                        'Impossibile caricare i dati personali. Riprova più tardi.',
                        'Chiudi',
                        { duration: 4000, panelClass: 'error-snackbar' }
                    );
                    return of(null);
                }),
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.markForCheck(); // Forza change detection dopo il caricamento
                })
            )
            .subscribe((profile: UserDetailDTO | null) => {
                this.profile = profile;
                this.cdr.markForCheck(); // Forza change detection quando i dati sono pronti
            });
    }

    /**
     * Naviga alla pagina di modifica profilo
     */
    editProfile(): void {
        this.router.navigate(['/profilo']);
    }

    /**
     * Ricarica i dati del profilo
     */
    refreshProfile(): void {
        this.loadProfile();
    }

    /**
     * Formatta la data di ultimo accesso
     */
    formatLastAccess(date?: string): string {
        if (!date) return 'Non disponibile';

        return new Date(date).toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * Restituisce l'etichetta formattata del ruolo
     */
    getRoleLabel(role?: string): string {
        if (!role) return 'Non specificato';

        const roleLabels: Record<string, string> = {
            'PRODUTTORE': 'Produttore',
            'TRASFORMATORE': 'Trasformatore',
            'DISTRIBUTORE_DI_TIPICITA': 'Distributore di Tipicità',
            'CURATORE': 'Curatore',
            'ANIMATORE_DELLA_FILIERA': 'Animatore della Filiera',
            'ACQUIRENTE': 'Acquirente',
            'GESTORE_PIATTAFORMA': 'Gestore Piattaforma'
        };

        return roleLabels[role] || role;
    }

    /**
     * Verifica se un campo opzionale è valorizzato
     */
    hasValue(value?: string): boolean {
        return value !== null && value !== undefined && value.trim() !== '';
    }
}