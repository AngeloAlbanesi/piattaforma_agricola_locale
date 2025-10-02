import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
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
import { CertificationDTO } from '../../../../../core/models/trasformatore.models';

@Component({
  selector: 'app-certificazioni-trasformatore',
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
  templateUrl: './certificazioni-trasformatore.component.html',
  styleUrl: './certificazioni-trasformatore.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CertificazioniTrasformatoreComponent implements OnInit {
  // Dati di esempio per le certificazioni
  certificazioniData: CertificationDTO[] = [
    {
      idCertificazione: 1,
      nomeCertificazione: 'Biologico UE',
      enteRilascio: 'ICEA',
      dataRilascio: '2024-01-01',
      dataScadenza: '2025-01-01',
      idProcessoAssociato: 1,
      idAziendaAssociata: 10
    },
    {
      idCertificazione: 2,
      nomeCertificazione: 'DOP Olio EVO',
      enteRilascio: 'Consorzio DOP',
      dataRilascio: '2023-05-15',
      dataScadenza: '2026-05-15',
      idProcessoAssociato: 1,
      idAziendaAssociata: 10
    },
    {
      idCertificazione: 3,
      nomeCertificazione: 'Vegan OK',
      enteRilascio: 'Vegan Society',
      dataRilascio: '2024-03-20',
      dataScadenza: '2025-03-20',
      idProcessoAssociato: 2,
      idAziendaAssociata: 10
    }
  ];

  displayedColumns: string[] = ['idCertificazione', 'nomeCertificazione', 'enteRilascio', 'dataRilascio', 'dataScadenza', 'processoAssociato', 'azioni'];
  dataSource = new MatTableDataSource<CertificationDTO>(this.certificazioniData);

  filters = {
    search: '',
    stato: 'TUTTI'
  };

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
    // Inizializzazione o caricamento dati
  }

  /**
   * Apre un dialog per aggiungere una nuova certificazione.
   */
  aggiungiCertificazione(): void {
    console.log('Aggiungi nuova certificazione');
    // Logica per aprire il dialog di aggiunta
  }

  /**
   * Apre un dialog per modificare una certificazione esistente.
   * @param certificazione La certificazione da modificare.
   */
  modificaCertificazione(certificazione: CertificationDTO): void {
    console.log('Modifica certificazione:', certificazione);
    // Logica per aprire il dialog di modifica
  }

  /**
   * Elimina una certificazione.
   * @param certificazione La certificazione da eliminare.
   */
  eliminaCertificazione(certificazione: CertificationDTO): void {
    console.log('Elimina certificazione:', certificazione);
    // Logica per l'eliminazione
  }

  /**
   * Applica i filtri alla tabella (simulato).
   */
  applyFilter(): void {
    const filterValue = this.filters.search?.toLowerCase() || '';
    this.dataSource.filterPredicate = (data: CertificationDTO, filter: string) => {
      const searchMatch = data.nomeCertificazione.toLowerCase().includes(filter) ||
                          data.enteRilascio.toLowerCase().includes(filter);
      return searchMatch;
    };
    this.dataSource.filter = filterValue;
  }
}