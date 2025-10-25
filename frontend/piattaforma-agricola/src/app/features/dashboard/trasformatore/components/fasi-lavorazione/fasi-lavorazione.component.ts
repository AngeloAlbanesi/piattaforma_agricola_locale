import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { FaseLavorazioneDTO, ProcessoTrasformazioneSummaryDTO } from '../../../../../core/models/trasformatore.models';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';
import { FaseDialogComponent } from '../fase-dialog/fase-dialog.component';
import { DeleteConfirmationDialogComponent } from '../delete-confirmation-dialog/delete-confirmation-dialog.component';

@Component({
    selector: 'app-fasi-lavorazione',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatTooltipModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        FormsModule,
        MatChipsModule
    ],
    templateUrl: './fasi-lavorazione.component.html',
    styleUrl: './fasi-lavorazione.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FasiLavorazioneComponent implements OnInit {
    // Dati delle fasi di lavorazione
    fasi: FaseLavorazioneDTO[] = [];
    processi: ProcessoTrasformazioneSummaryDTO[] = [];
    selectedProcessoId: number | null = null;
    isLoading = false;
    isLoadingFasi = false;

    displayedColumns: string[] = ['ordineEsecuzione', 'nome', 'descrizione', 'materiaPrimaUtilizzata', 'fonte', 'azioni'];
    dataSource = this.fasi;

    constructor(
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private trasformatoreService: TrasformatoreService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProcessi();
    }

    loadProcessi(): void {
        this.isLoading = true;
        this.trasformatoreService.getMyProcesses({ pagina: 0, elementiPerPagina: 100 }).subscribe({
            next: (response: any) => {
                this.processi = response.content;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error: any) => {
                console.error('Errore durante il caricamento dei processi:', error);
                this.snackBar.open('Errore durante il caricamento dei processi.', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    onProcessoSelected(): void {
        if (this.selectedProcessoId) {
            this.loadFasi(this.selectedProcessoId);
        } else {
            this.fasi = [];
            this.dataSource = this.fasi;
            this.cdr.markForCheck();
        }
    }

    loadFasi(processoId: number): void {
        this.isLoadingFasi = true;
        this.trasformatoreService.getProcessPhases(processoId).subscribe({
            next: (fasi: FaseLavorazioneDTO[]) => {
                this.fasi = fasi.sort((a, b) => a.ordineEsecuzione - b.ordineEsecuzione);
                this.dataSource = this.fasi;
                this.isLoadingFasi = false;
                this.cdr.markForCheck();
            },
            error: (error: any) => {
                console.error('Errore durante il caricamento delle fasi:', error);
                this.snackBar.open('Errore durante il caricamento delle fasi.', 'Chiudi', { duration: 3000 });
                this.isLoadingFasi = false;
                this.cdr.markForCheck();
            }
        });
    }

    /**
     * Apre un dialog per aggiungere una nuova fase.
     */
    aggiungiFase(): void {
        if (!this.selectedProcessoId) {
            this.snackBar.open('Seleziona prima un processo.', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(FaseDialogComponent, {
            width: '700px',
            maxWidth: '95vw',
            disableClose: false,
            data: {
                mode: 'create',
                processoId: this.selectedProcessoId
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && this.selectedProcessoId) {
                this.isLoadingFasi = true;
                this.cdr.markForCheck();

                this.trasformatoreService.createPhase(this.selectedProcessoId, result).subscribe({
                    next: () => {
                        this.snackBar.open('Fase creata con successo!', 'Chiudi', {
                            duration: 3000,
                            panelClass: ['success-snackbar']
                        });
                        this.loadFasi(this.selectedProcessoId!);
                    },
                    error: (error: any) => {
                        console.error('Errore durante la creazione della fase:', error);
                        this.snackBar.open(
                            error.error?.message || 'Errore durante la creazione della fase.',
                            'Chiudi',
                            { duration: 5000, panelClass: ['error-snackbar'] }
                        );
                        this.isLoadingFasi = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    /**
     * Apre un dialog per modificare una fase esistente.
     * @param fase La fase da modificare.
     */
    modificaFase(fase: FaseLavorazioneDTO): void {
        if (!this.selectedProcessoId) {
            this.snackBar.open('Errore: processo non selezionato.', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(FaseDialogComponent, {
            width: '700px',
            maxWidth: '95vw',
            disableClose: false,
            data: {
                mode: 'edit',
                fase: fase,
                processoId: this.selectedProcessoId
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && this.selectedProcessoId) {
                this.isLoadingFasi = true;
                this.cdr.markForCheck();

                this.trasformatoreService.updatePhase(this.selectedProcessoId, fase.id, result).subscribe({
                    next: () => {
                        this.snackBar.open('Fase aggiornata con successo!', 'Chiudi', {
                            duration: 3000,
                            panelClass: ['success-snackbar']
                        });
                        this.loadFasi(this.selectedProcessoId!);
                    },
                    error: (error: any) => {
                        console.error('Errore durante l\'aggiornamento della fase:', error);
                        this.snackBar.open(
                            error.error?.message || 'Errore durante l\'aggiornamento della fase.',
                            'Chiudi',
                            { duration: 5000, panelClass: ['error-snackbar'] }
                        );
                        this.isLoadingFasi = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    /**
     * Elimina una fase.
     * @param fase La fase da eliminare.
     */
    eliminaFase(fase: FaseLavorazioneDTO): void {
        if (!this.selectedProcessoId) {
            this.snackBar.open('Errore: processo non selezionato.', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
            width: '400px',
            data: {
                title: 'Conferma Eliminazione',
                message: `Sei sicuro di voler eliminare la fase "${fase.nome}"? Questa azione non può essere annullata.`,
                confirmText: 'Elimina',
                cancelText: 'Annulla'
            }
        });

        dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed && this.selectedProcessoId) {
                this.isLoadingFasi = true;
                this.cdr.markForCheck();

                this.trasformatoreService.deletePhase(this.selectedProcessoId, fase.id).subscribe({
                    next: () => {
                        this.snackBar.open('Fase eliminata con successo!', 'Chiudi', {
                            duration: 3000,
                            panelClass: ['success-snackbar']
                        });
                        this.loadFasi(this.selectedProcessoId!);
                    },
                    error: (error: any) => {
                        console.error('Errore durante l\'eliminazione della fase:', error);
                        this.snackBar.open(
                            error.error?.message || 'Errore durante l\'eliminazione della fase.',
                            'Chiudi',
                            { duration: 5000, panelClass: ['error-snackbar'] }
                        );
                        this.isLoadingFasi = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    /**
     * Restituisce la label leggibile per il tipo di fonte.
     */
    getFonteLabel(fonte: any): string {
        if (!fonte) return 'N/A';
        if (fonte.tipo === 'INTERNA') {
            return 'Interna';
        } else if (fonte.tipo === 'ESTERNA' && fonte.nomeFornitore) {
            return `Esterna - ${fonte.nomeFornitore}`;
        }
        return fonte.tipo;
    }
}