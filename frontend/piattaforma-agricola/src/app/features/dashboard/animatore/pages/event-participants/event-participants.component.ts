import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AnimatoreService } from '../../../../../core/services/animatore.service';
import { EventoPartecipanteDTO } from '../../../../../core/models/animatore.models';

@Component({
    selector: 'app-event-participants',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatTableModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatSnackBarModule
    ],
    template: `
    <div class="participants-container">
      <mat-card appearance="outlined">
        <mat-card-header>
          <button mat-icon-button (click)="goBack()" class="back-button">
            <mat-icon>arrow_back</mat-icon>
          </button>
          <mat-card-title>Partecipanti Evento</mat-card-title>
          <mat-card-subtitle>Lista completa dei partecipanti utenti</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <div class="loading-container" *ngIf="isLoading">
            <mat-spinner diameter="50"></mat-spinner>
          </div>

          <div *ngIf="!isLoading && partecipanti.length === 0" class="empty-state">
            <mat-icon>people_outline</mat-icon>
            <h3>Nessun partecipante</h3>
            <p>Non ci sono ancora partecipanti registrati per questo evento.</p>
          </div>

          <div *ngIf="!isLoading && partecipanti.length > 0" class="participants-list">
            <div class="stats-summary">
              <div class="stat-card">
                <mat-icon>people</mat-icon>
                <div class="stat-info">
                  <span class="stat-label">Totale Partecipanti</span>
                  <span class="stat-value">{{ partecipanti.length }}</span>
                </div>
              </div>
              <div class="stat-card">
                <mat-icon>event_seat</mat-icon>
                <div class="stat-info">
                  <span class="stat-label">Posti Prenotati</span>
                  <span class="stat-value">{{ getTotalPosti() }}</span>
                </div>
              </div>
            </div>

            <table mat-table [dataSource]="partecipanti" class="participants-table">
              <!-- Nome Column -->
              <ng-container matColumnDef="nome">
                <th mat-header-cell *matHeaderCellDef> Nome Completo </th>
                <td mat-cell *matCellDef="let partecipante"> 
                  {{ partecipante.nome }} {{ partecipante.cognome }} 
                </td>
              </ng-container>

              <!-- Email Column -->
              <ng-container matColumnDef="email">
                <th mat-header-cell *matHeaderCellDef> Email </th>
                <td mat-cell *matCellDef="let partecipante"> 
                  <a [href]="'mailto:' + partecipante.email">{{ partecipante.email }}</a>
                </td>
              </ng-container>

              <!-- Posti Column -->
              <ng-container matColumnDef="posti">
                <th mat-header-cell *matHeaderCellDef> Posti </th>
                <td mat-cell *matCellDef="let partecipante"> 
                  {{ partecipante.numeroPosti }} 
                </td>
              </ng-container>

              <!-- Data Registrazione Column -->
              <ng-container matColumnDef="dataRegistrazione">
                <th mat-header-cell *matHeaderCellDef> Data Registrazione </th>
                <td mat-cell *matCellDef="let partecipante"> 
                  {{ partecipante.dataRegistrazione | date:'short':'':'it-IT' }} 
                </td>
              </ng-container>

              <!-- Note Column -->
              <ng-container matColumnDef="note">
                <th mat-header-cell *matHeaderCellDef> Note </th>
                <td mat-cell *matCellDef="let partecipante"> 
                  {{ partecipante.note || '-' }} 
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
    styles: [`
    .participants-container {
      padding: 20px;
    }

    mat-card-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      .back-button {
        margin-right: 10px;
      }
    }

    .loading-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 300px;
    }

    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #999;

      mat-icon {
        font-size: 72px;
        width: 72px;
        height: 72px;
        margin-bottom: 20px;
        opacity: 0.5;
      }

      h3 {
        margin: 10px 0;
        color: #666;
      }

      p {
        margin: 0;
        font-size: 14px;
      }
    }

    .stats-summary {
      display: flex;
      gap: 20px;
      margin-bottom: 30px;

      .stat-card {
        flex: 1;
        display: flex;
        align-items: center;
        gap: 15px;
        padding: 20px;
        background: #f5f5f5;
        border-radius: 8px;

        mat-icon {
          font-size: 40px;
          width: 40px;
          height: 40px;
          color: #1976d2;
        }

        .stat-info {
          display: flex;
          flex-direction: column;

          .stat-label {
            font-size: 12px;
            color: #666;
            text-transform: uppercase;
          }

          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #333;
          }
        }
      }
    }

    .participants-table {
      width: 100%;
      
      a {
        color: #1976d2;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  `]
})
export class EventParticipantsComponent implements OnInit {
    eventoId!: number;
    partecipanti: EventoPartecipanteDTO[] = [];
    isLoading = true;
    displayedColumns: string[] = ['nome', 'email', 'posti', 'dataRegistrazione', 'note'];

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private animatoreService: AnimatoreService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.eventoId = Number(this.route.snapshot.paramMap.get('id'));
        this.loadPartecipanti();
    }

    loadPartecipanti(): void {
        this.isLoading = true;
        this.animatoreService.getEventParticipants(this.eventoId).subscribe({
            next: (data) => {
                this.partecipanti = data;
                this.isLoading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Error loading partecipanti:', err);
                this.snackBar.open(
                    'Errore durante il caricamento dei partecipanti: ' + (err.error?.message || 'Errore sconosciuto'),
                    'Chiudi',
                    { duration: 5000 }
                );
                this.isLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    getTotalPosti(): number {
        return this.partecipanti.reduce((sum, p) => sum + p.numeroPosti, 0);
    }

    goBack(): void {
        this.router.navigate(['/dashboard/animatore']);
    }
}
