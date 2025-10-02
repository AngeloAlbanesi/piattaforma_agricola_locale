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
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { FaseLavorazioneDTO } from '../../../../../core/models/trasformatore.models';

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
    FormsModule,
    MatChipsModule,
    DatePipe
  ],
  templateUrl: './fasi-lavorazione.component.html',
  styleUrl: './fasi-lavorazione.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FasiLavorazioneComponent implements OnInit {
  // Dati di esempio per le fasi di lavorazione
  fasi: FaseLavorazioneDTO[] = [
    {
      id: 1,
      nome: 'Raccolta Materia Prima',
      descrizione: 'Raccolta delle olive fresche e mature.',
      ordine: 1,
      durataPrevista: 2, // Giorni
      stato: 'COMPLETATA',
      dataInizio: '2024-09-01',
      dataFine: '2024-09-03',
      durataEffettiva: 2,
      tecniche: ['Raccolta a mano'],
      attrezzature: ['Casse'],
      materiali: [],
      note: 'Raccolta completata in anticipo.',
    },
    {
      id: 2,
      nome: 'Frangitura',
      descrizione: 'Processo di frangitura delle olive per ottenere la pasta.',
      ordine: 2,
      durataPrevista: 1,
      stato: 'COMPLETATA',
      dataInizio: '2024-09-04',
      dataFine: '2024-09-04',
      durataEffettiva: 1,
      tecniche: ['Frangitura meccanica'],
      attrezzature: ['Frangitore'],
      materiali: [],
    },
    {
      id: 3,
      nome: 'Gramolatura',
      descrizione: 'Mescolamento lento della pasta di olive.',
      ordine: 3,
      durataPrevista: 0.5,
      stato: 'IN_CORSO',
      dataInizio: '2024-09-05',
      tecniche: ['Gramolatura a freddo'],
      attrezzature: ['Gramolatrice'],
      materiali: [],
    },
    {
      id: 4,
      nome: 'Estrazione',
      descrizione: 'Separazione dell\'olio dalla pasta.',
      ordine: 4,
      durataPrevista: 1,
      stato: 'DA_INIZIARE',
      tecniche: ['Estrazione centrifuga'],
      attrezzature: ['Decanter'],
      materiali: [],
    }
  ];

  displayedColumns: string[] = ['ordine', 'nome', 'durataPrevista', 'stato', 'dataInizio', 'dataFine', 'azioni'];
  dataSource = this.fasi;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
    // Inizializzazione o caricamento dati
  }

  /**
   * Apre un dialog per aggiungere una nuova fase.
   */
  aggiungiFase(): void {
    console.log('Aggiungi nuova fase');
    // Logica per aprire il dialog di aggiunta
  }

  /**
   * Apre un dialog per modificare una fase esistente.
   * @param fase La fase da modificare.
   */
  modificaFase(fase: FaseLavorazioneDTO): void {
    console.log('Modifica fase:', fase);
    // Logica per aprire il dialog di modifica
  }

  /**
   * Elimina una fase.
   * @param fase La fase da eliminare.
   */
  eliminaFase(fase: FaseLavorazioneDTO): void {
    console.log('Elimina fase:', fase);
    // Logica per l'eliminazione
  }

  /**
   * Aggiorna lo stato di una fase (es. da IN_ATTESA a IN_CORSO, o a COMPLETATA).
   * @param fase La fase da aggiornare.
   */
  aggiornaStato(fase: FaseLavorazioneDTO): void {
    console.log('Aggiorna stato fase:', fase);
    // Logica per l'aggiornamento dello stato
  }

  /**
   * Restituisce la classe CSS in base allo stato della fase.
   * @param stato Lo stato della fase.
   * @returns La classe CSS.
   */
  getStatoClass(stato: string): string {
    switch (stato) {
      case 'COMPLETATA':
        return 'status-completed';
      case 'IN_CORSO':
        return 'status-in-progress';
      case 'IN_ATTESA':
        return 'status-pending';
      default:
        return '';
    }
  }
}