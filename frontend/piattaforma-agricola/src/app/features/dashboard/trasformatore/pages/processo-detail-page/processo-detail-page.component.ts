import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';

// Interface che rappresenta la risposta reale dell'API
interface ProcessoApiResponse {
    idProcesso: number;
    nomeProcesso: string;
    descrizioneProcesso: string;
    noteTecniche?: string;
    idTrasformatore: number;
    nomeTrasformatore: string;
    cognomeTrasformatore: string;
    aziendaTrasformatore: string;
    fasi: FaseApiResponse[];
}

interface FaseApiResponse {
    id: number;
    nome: string;
    descrizione: string;
    ordineEsecuzione: number;
    materiaPrimaUtilizzata: string;
    fonteMateriaPrima: {
        tipo: 'ESTERNA' | 'INTERNA';
        id: number;
        produttore?: any;
        descrizione?: string;
    };
    descrizioneFonte: string;
    tipoFonte: string;
    fonteInterna: boolean;
    idProduttoreInterno?: number;
}

@Component({
    selector: 'app-processo-detail-page',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatExpansionModule,
        MatDividerModule,
        MatSnackBarModule,
        MatTooltipModule
    ],
    templateUrl: './processo-detail-page.component.html',
    styleUrls: ['./processo-detail-page.component.scss']
})
export class ProcessoDetailPageComponent implements OnInit {
    processo: ProcessoApiResponse | null = null;
    isLoading = true;
    error = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private trasformatoreService: TrasformatoreService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.loadProcessDetails(+id);
        } else {
            this.error = true;
            this.isLoading = false;
            this.snackBar.open('ID processo non valido', 'Chiudi', { duration: 3000 });
        }
    }

    loadProcessDetails(id: number): void {
        this.isLoading = true;
        this.error = false;

        this.trasformatoreService.getProcessById(id).subscribe({
            next: (data: any) => {
                this.processo = data;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error: any) => {
                console.error('Errore durante il caricamento dei dettagli del processo:', error);
                this.snackBar.open(
                    'Errore durante il caricamento dei dettagli del processo.',
                    'Chiudi',
                    { duration: 5000 }
                );
                this.error = true;
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    goBack(): void {
        this.router.navigate(['/dashboard/trasformatore']);
    }

    getFonteTipoLabel(tipo: string): string {
        return tipo === 'ESTERNA' ? 'Esterna' : 'Interna';
    }

    getFonteName(fase: FaseApiResponse): string {
        return fase.descrizioneFonte || 'Non specificato';
    }
}

