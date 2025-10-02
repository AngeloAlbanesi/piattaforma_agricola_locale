import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { MetodoDiColtivazioneDTO, TipoColtivazione } from '../../../../../core/models/produttore.models';

@Component({
  selector: 'app-metodi-coltivazione',
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
    MatTooltipModule,
    MatChipsModule
  ],
  templateUrl: './metodi-coltivazione.component.html',
  styleUrl: './metodi-coltivazione.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MetodiColtivazioneComponent implements OnInit {
  // Dati di esempio per i metodi di coltivazione
  metodiData: MetodoDiColtivazioneDTO[] = [
    {
      id: 1,
      nome: 'Coltivazione Biologica Olivo',
      descrizione: 'Metodo di coltivazione che esclude l\'uso di pesticidi e fertilizzanti chimici.',
      tecniche: ['Rotazione colturale', 'Concimazione organica'],
      periodoColtivazione: 'ANNUALE',
      superficie: 5.5,
      ubicazione: 'Campo Sud'
    },
    {
      id: 2,
      nome: 'Coltivazione Integrata Grano',
      descrizione: 'Utilizzo di tecniche a basso impatto ambientale, limitando l\'uso di prodotti chimici.',
      tecniche: ['Monitoraggio parassiti', 'Irrigazione a goccia'],
      periodoColtivazione: 'STAGIONALE',
      superficie: 10.0,
      ubicazione: 'Campo Nord'
    }
  ];

  displayedColumns: string[] = ['id', 'nome', 'periodoColtivazione', 'superficie', 'ubicazione', 'tecniche', 'azioni'];
  dataSource = new MatTableDataSource<MetodoDiColtivazioneDTO>(this.metodiData);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters = {
    search: '',
    tipo: 'TUTTI'
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
   * Apre un dialog per aggiungere un nuovo metodo.
   */
  aggiungiMetodo(): void {
    console.log('Aggiungi nuovo metodo');
    // Logica per aprire il dialog di aggiunta
  }

  /**
   * Apre un dialog per modificare un metodo esistente.
   * @param metodo Il metodo da modificare.
   */
  modificaMetodo(metodo: MetodoDiColtivazioneDTO): void {
    console.log('Modifica metodo:', metodo);
    // Logica per aprire il dialog di modifica
  }

  /**
   * Elimina un metodo.
   * @param metodo Il metodo da eliminare.
   */
  eliminaMetodo(metodo: MetodoDiColtivazioneDTO): void {
    console.log('Elimina metodo:', metodo);
    // Logica per l'eliminazione
  }

  /**
   * Applica i filtri alla tabella (simulato).
   */
  applyFilter(): void {
    const filterValue = this.filters.search?.toLowerCase() || '';
    this.dataSource.filterPredicate = (data: MetodoDiColtivazioneDTO, filter: string) => {
      const searchMatch = data.nome.toLowerCase().includes(filter) ||
                          data.descrizione.toLowerCase().includes(filter);
      return searchMatch;
    };
    this.dataSource.filter = filterValue;
  }
}