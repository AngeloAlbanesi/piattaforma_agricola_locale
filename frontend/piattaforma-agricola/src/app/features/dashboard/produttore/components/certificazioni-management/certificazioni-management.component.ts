import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { CertificationDTO } from '../../../../../core/models/produttore.models';

@Component({
  selector: 'app-certificazioni-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
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
    DatePipe
  ],
  templateUrl: './certificazioni-management.component.html',
  styleUrl: './certificazioni-management.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CertificazioniManagementComponent implements OnInit {
  // Dati di esempio per le certificazioni
  certificazioniData: CertificationDTO[] = [
    {
      idCertificazione: 1,
      nomeCertificazione: 'Biologico UE',
      enteRilascio: 'ICEA',
      dataRilascio: '2024-01-01',
      dataScadenza: '2025-01-01',
      idProdottoAssociato: 101,
      idAziendaAssociata: 1
    },
    {
      idCertificazione: 2,
      nomeCertificazione: 'DOP Olio EVO',
      enteRilascio: 'Consorzio DOP',
      dataRilascio: '2023-05-15',
      dataScadenza: '2026-05-15',
      idProdottoAssociato: 102,
      idAziendaAssociata: 1
    },
    {
      idCertificazione: 3,
      nomeCertificazione: 'Global G.A.P.',
      enteRilascio: 'Global GAP',
      dataRilascio: '2024-03-20',
      dataScadenza: '2025-03-20',
      idProdottoAssociato: 101,
      idAziendaAssociata: 1
    }
  ];

  displayedColumns: string[] = ['idCertificazione', 'nomeCertificazione', 'enteRilascio', 'dataRilascio', 'dataScadenza', 'prodottoAssociato', 'azioni'];
  dataSource = new MatTableDataSource<CertificationDTO>(this.certificazioniData);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters = {
    search: '',
    prodottoId: 'TUTTI'
  };

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
    // Inizializzazione o caricamento dati
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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