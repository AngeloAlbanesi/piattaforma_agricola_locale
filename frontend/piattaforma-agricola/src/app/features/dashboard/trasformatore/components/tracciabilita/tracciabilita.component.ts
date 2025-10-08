import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { TracciabilitaDTO, ProcessoFilters } from '../../../../../core/models/trasformatore.models';
import { MatTableDataSource } from '@angular/material/table';

@Component({
    selector: 'app-tracciabilita',
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
        MatOptionModule,
        MatTooltipModule,
        FormsModule,
        MatChipsModule,
        DatePipe
    ],
    templateUrl: './tracciabilita.component.html',
    styleUrl: './tracciabilita.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TracciabilitaComponent implements OnInit {
    // Dati della tracciabilità
    tracciabilitaData: TracciabilitaDTO[] = [];

    displayedColumns: string[] = ['id', 'codiceUnivoco', 'processo', 'dataCreazione', 'prodottiOutput', 'certificazioni', 'azioni'];
    dataSource = new MatTableDataSource<TracciabilitaDTO>(this.tracciabilitaData);

    filters: ProcessoFilters = {
        search: '',
        stato: 'TUTTI'
    };

    constructor(public dialog: MatDialog) { }

    ngOnInit(): void {
        // Inizializzazione o caricamento dati
    }

    /**
     * Apre un dialog per visualizzare i dettagli completi della tracciabilità.
     * @param tracciabilita L'oggetto TracciabilitaDTO da visualizzare.
     */
    viewDetails(tracciabilita: TracciabilitaDTO): void {
        console.log('Visualizza dettagli tracciabilità:', tracciabilita);
        // Logica per aprire il dialog di visualizzazione dettagli
    }

    /**
     * Apre un dialog per creare un nuovo record di tracciabilità.
     */
    creaNuovaTracciabilita(): void {
        console.log('Crea nuova tracciabilità');
        // Logica per aprire il dialog di creazione
    }

    /**
     * Applica i filtri alla tabella (simulato, in un'applicazione reale si chiamerebbe l'API).
     */
    applyFilter(): void {
        // Logica di filtro simulata
        const filterValue = this.filters.search?.toLowerCase() || '';
        this.dataSource.filterPredicate = (data: TracciabilitaDTO, filter: string) => {
            const searchMatch = data.codiceUnivoco.toLowerCase().includes(filter) ||
                data.processo.nome.toLowerCase().includes(filter);
            return searchMatch;
        };
        this.dataSource.filter = filterValue;
    }
}