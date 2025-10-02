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
  // Dati di esempio per la tracciabilità
  tracciabilitaData: TracciabilitaDTO[] = [
    {
      id: 101,
      codiceUnivoco: 'TRC-2024-001',
      dataCreazione: '2024-09-05',
      processo: { id: 1, nome: 'Olio Extra Vergine di Oliva' },
      prodottiInput: [
        { id: 10, nome: 'Olive Nocellara', quantita: 500, lotto: 'OLV-2024-A', dataRicezione: '2024-09-01' }
      ],
      fasiEseguite: [
        { id: 1, nome: 'Raccolta Materia Prima', dataInizio: '2024-09-01', dataFine: '2024-09-03', durata: 2 },
        { id: 2, nome: 'Frangitura', dataInizio: '2024-09-04', dataFine: '2024-09-04', durata: 1 }
      ],
      prodottiOutput: [
        { id: 20, nome: 'Olio EVO Grezzo', quantita: 100, lotto: 'OLIO-2024-A', dataProduzione: '2024-09-05' }
      ],
      certificazioni: []
    },
    {
      id: 102,
      codiceUnivoco: 'TRC-2024-002',
      dataCreazione: '2024-09-15',
      processo: { id: 2, nome: 'Confettura di Fichi' },
      prodottiInput: [
        { id: 11, nome: 'Fichi Secchi', quantita: 50, lotto: 'FIC-2024-B', dataRicezione: '2024-09-10' }
      ],
      fasiEseguite: [
        { id: 3, nome: 'Preparazione Frutta', dataInizio: '2024-09-15', dataFine: '2024-09-15', durata: 0.5 }
      ],
      prodottiOutput: [
        { id: 21, nome: 'Confettura di Fichi', quantita: 40, lotto: 'CONF-2024-B', dataProduzione: '2024-09-15' }
      ],
      certificazioni: [{ idCertificazione: 1, nomeCertificazione: 'Biologico', enteRilascio: 'ICEA', dataRilascio: '2024-01-01', dataScadenza: '2025-01-01', idProcessoAssociato: 2, idAziendaAssociata: 1 }]
    }
  ];

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