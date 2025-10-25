/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { finalize } from 'rxjs/operators';

import { AdminService } from '../../../../../core/services/admin.service';
import { UserPublicDTO } from '../../../../../core/models/admin.models';
import { AccreditamentoDialogComponent } from '../accreditamento-dialog/accreditamento-dialog.component';
import { CompanyDetailsDialogComponent } from '../company-details-dialog/company-details-dialog.component';
import {
    mapStatoAccreditamentoToChipColor,
    mapStatoAccreditamentoToDisplayName,
    mapStatoAccreditamentoToIcon,
    mapTipoRuoloToDisplayName,
    getUserFullName
} from '../../utils/gestore.utils';

@Component({
    selector: 'app-accreditamenti-management',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatTabsModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        MatSnackBarModule
    ],
    templateUrl: './accreditamenti-management.component.html',
    styleUrls: ['./accreditamenti-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AccreditamentiManagementComponent implements OnInit {
    displayedColumns: string[] = ['nome', 'cognome', 'statoAccreditamento', 'actions'];
    displayedColumnsVenditori: string[] = ['nome', 'cognome', 'statoAccreditamento', 'azienda', 'actions'];

    venditori = signal<UserPublicDTO[]>([]);
    curatori = signal<UserPublicDTO[]>([]);
    animatori = signal<UserPublicDTO[]>([]);

    isLoadingVenditori = signal(false);
    isLoadingCuratori = signal(false);
    isLoadingAnimatori = signal(false);

    selectedTabIndex = signal(0);

    constructor(
        private adminService: AdminService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadAllPendingAccreditations();
    }

    loadAllPendingAccreditations(): void {
        this.loadPendingVenditori();
        this.loadPendingCuratori();
        this.loadPendingAnimatori();
    }

    loadPendingVenditori(): void {
        this.isLoadingVenditori.set(true);
        this.adminService.getPendingVenditori()
            .pipe(finalize(() => {
                this.isLoadingVenditori.set(false);
                this.cdr.markForCheck();
            }))
            .subscribe({
                next: (data) => {
                    this.venditori.set(data);
                },
                error: (error) => {
                    console.error('Error loading pending venditori:', error);
                    this.snackBar.open('Errore nel caricamento dei venditori in attesa', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    loadPendingCuratori(): void {
        this.isLoadingCuratori.set(true);
        this.adminService.getPendingCuratori()
            .pipe(finalize(() => {
                this.isLoadingCuratori.set(false);
                this.cdr.markForCheck();
            }))
            .subscribe({
                next: (data) => {
                    this.curatori.set(data);
                },
                error: (error) => {
                    console.error('Error loading pending curatori:', error);
                    this.snackBar.open('Errore nel caricamento dei curatori in attesa', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    loadPendingAnimatori(): void {
        this.isLoadingAnimatori.set(true);
        this.adminService.getPendingAnimatori()
            .pipe(finalize(() => {
                this.isLoadingAnimatori.set(false);
                this.cdr.markForCheck();
            }))
            .subscribe({
                next: (data) => {
                    this.animatori.set(data);
                },
                error: (error) => {
                    console.error('Error loading pending animatori:', error);
                    this.snackBar.open('Errore nel caricamento degli animatori in attesa', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    openAccreditamentoDialog(user: UserPublicDTO, tipo: 'venditore' | 'curatore' | 'animatore'): void {
        const dialogRef = this.dialog.open(AccreditamentoDialogComponent, {
            width: '500px',
            data: {
                displayName: getUserFullName(user.nome, user.cognome),
                ruolo: mapTipoRuoloToDisplayName(user.tipoRuolo)
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.handleAccreditamento(user.idUtente, result.azione, tipo, result.nota);
            }
        });
    }

    handleAccreditamento(userId: number, azione: string, tipo: 'venditore' | 'curatore' | 'animatore', nota?: string): void {
        let observable;

        switch (tipo) {
            case 'venditore':
                observable = this.adminService.updateAccreditamentoVenditore(userId, azione);
                break;
            case 'curatore':
                observable = this.adminService.updateAccreditamentoCuratore(userId, azione);
                break;
            case 'animatore':
                observable = this.adminService.updateAccreditamentoAnimatore(userId, azione);
                break;
        }

        observable.subscribe({
            next: (message) => {
                this.snackBar.open(message || 'Accreditamento aggiornato con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.refreshList(tipo);
            },
            error: (error) => {
                console.error('Error updating accreditation:', error);
                this.snackBar.open('Errore nell\'aggiornamento dell\'accreditamento', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            }
        });
    }

    viewCompanyDetails(userId: number): void {
        this.adminService.getVendorCompanyData(userId).subscribe({
            next: (companyData) => {
                this.dialog.open(CompanyDetailsDialogComponent, {
                    width: '600px',
                    data: companyData
                });
            },
            error: (error) => {
                console.error('Error loading company data:', error);
                this.snackBar.open('Errore nel caricamento dei dati aziendali', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            }
        });
    }

    refreshList(tipo: 'venditore' | 'curatore' | 'animatore'): void {
        switch (tipo) {
            case 'venditore':
                this.loadPendingVenditori();
                break;
            case 'curatore':
                this.loadPendingCuratori();
                break;
            case 'animatore':
                this.loadPendingAnimatori();
                break;
        }
    }

    refreshAll(): void {
        this.loadAllPendingAccreditations();
        this.snackBar.open('Dati aggiornati', 'Chiudi', { duration: 2000 });
    }

    // Utility methods for template
    getStatoChipColor(stato: string): string {
        return mapStatoAccreditamentoToChipColor(stato);
    }

    getStatoDisplayName(stato: string): string {
        return mapStatoAccreditamentoToDisplayName(stato);
    }

    getStatoIcon(stato: string): string {
        return mapStatoAccreditamentoToIcon(stato);
    }

    getUserFullName(nome: string, cognome: string): string {
        return getUserFullName(nome, cognome);
    }

    get hasVenditori(): boolean {
        return this.venditori().length > 0;
    }

    get hasCuratori(): boolean {
        return this.curatori().length > 0;
    }

    get hasAnimatori(): boolean {
        return this.animatori().length > 0;
    }
}

