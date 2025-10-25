import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { PublicEventoSummaryDTO } from '../../../../../core/models/public.models';
import { EventoRegistrazioneRequestDTO } from '../../../../../core/models/acquirente.models';

export interface EventRegistrationDialogData {
    evento: PublicEventoSummaryDTO;
    postiDisponibili: number;
}

@Component({
    selector: 'app-event-registration-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatDividerModule
    ],
    template: `
        <div class="event-registration-dialog">
            <h2 mat-dialog-title>
                <mat-icon>event_available</mat-icon>
                Iscriviti all'evento
            </h2>
            
            <mat-dialog-content>
                <!-- Event Summary -->
                <div class="event-summary">
                    <h3 class="event-name">{{ data.evento.nome }}</h3>
                    
                    <div class="event-details">
                        <div class="detail-item">
                            <mat-icon>event</mat-icon>
                            <span>{{ formatDate(data.evento.dataOraInizio) }} - {{ formatTime(data.evento.dataOraInizio) }}</span>
                        </div>
                        
                        <div class="detail-item">
                            <mat-icon>location_on</mat-icon>
                            <span>{{ data.evento.luogo }}</span>
                        </div>
                        
                        <div class="detail-item" [class.warning]="data.postiDisponibili < 5">
                            <mat-icon>group</mat-icon>
                            <span>{{ data.postiDisponibili }} posti disponibili</span>
                        </div>
                    </div>
                </div>
                
                <mat-divider></mat-divider>
                
                <!-- Registration Form -->
                <form [formGroup]="registrationForm" class="registration-form">
                    <mat-form-field appearance="outline" class="full-width">
                        <mat-label>Numero di posti</mat-label>
                        <input matInput 
                               type="number" 
                               formControlName="numeroPosti"
                               placeholder="Inserisci il numero di posti"
                               min="1"
                               [max]="data.postiDisponibili">
                        <mat-icon matPrefix>person</mat-icon>
                        <mat-hint>Massimo {{ data.postiDisponibili }} posti</mat-hint>
                        @if (registrationForm.get('numeroPosti')?.hasError('required')) {
                        <mat-error>Il numero di posti è obbligatorio</mat-error>
                        }
                        @if (registrationForm.get('numeroPosti')?.hasError('min')) {
                        <mat-error>Minimo 1 posto</mat-error>
                        }
                        @if (registrationForm.get('numeroPosti')?.hasError('max')) {
                        <mat-error>Massimo {{ data.postiDisponibili }} posti</mat-error>
                        }
                    </mat-form-field>
                    
                    <mat-form-field appearance="outline" class="full-width">
                        <mat-label>Note (opzionale)</mat-label>
                        <textarea matInput 
                                  formControlName="note"
                                  placeholder="Aggiungi eventuali note o richieste particolari"
                                  rows="3"
                                  maxlength="500"></textarea>
                        <mat-icon matPrefix>edit_note</mat-icon>
                        <mat-hint align="end">{{ registrationForm.get('note')?.value?.length || 0 }}/500</mat-hint>
                    </mat-form-field>
                </form>
            </mat-dialog-content>
            
            <mat-dialog-actions align="end">
                <button mat-button (click)="onCancel()">
                    Annulla
                </button>
                <button mat-raised-button 
                        color="primary" 
                        (click)="onConfirm()"
                        [disabled]="!registrationForm.valid">
                    <mat-icon>check</mat-icon>
                    Conferma iscrizione
                </button>
            </mat-dialog-actions>
        </div>
    `,
    styles: [`
        .event-registration-dialog {
            min-width: 400px;
            max-width: 600px;
        }
        
        h2[mat-dialog-title] {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 0;
            color: #333;
            
            mat-icon {
                color: #2196f3;
            }
        }
        
        mat-dialog-content {
            padding: 20px 24px;
        }
        
        .event-summary {
            margin-bottom: 20px;
            
            .event-name {
                font-size: 1.2rem;
                font-weight: 600;
                color: #333;
                margin: 0 0 12px 0;
            }
            
            .event-details {
                display: flex;
                flex-direction: column;
                gap: 8px;
                
                .detail-item {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    color: #666;
                    font-size: 0.9rem;
                    
                    mat-icon {
                        font-size: 20px;
                        width: 20px;
                        height: 20px;
                    }
                    
                    &.warning {
                        color: #ff9800;
                        font-weight: 500;
                    }
                }
            }
        }
        
        mat-divider {
            margin: 16px 0;
        }
        
        .registration-form {
            margin-top: 16px;
            
            .full-width {
                width: 100%;
            }
            
            mat-form-field {
                margin-bottom: 8px;
            }
        }
        
        .info-message {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            padding: 12px;
            background-color: #e3f2fd;
            border-radius: 4px;
            margin-top: 16px;
            
            mat-icon {
                color: #2196f3;
                font-size: 20px;
                width: 20px;
                height: 20px;
                margin-top: 2px;
            }
            
            p {
                margin: 0;
                font-size: 0.85rem;
                color: #1976d2;
                line-height: 1.4;
            }
        }
        
        mat-dialog-actions {
            padding: 16px 24px;
            gap: 12px;
            
            button mat-icon {
                margin-right: 6px;
                font-size: 18px;
                width: 18px;
                height: 18px;
            }
        }
        
        @media (max-width: 600px) {
            .event-registration-dialog {
                min-width: unset;
                width: 100%;
            }
        }
    `]
})
export class EventRegistrationDialogComponent implements OnInit {
    registrationForm!: FormGroup;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<EventRegistrationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: EventRegistrationDialogData
    ) { }

    ngOnInit(): void {
        this.registrationForm = this.fb.group({
            numeroPosti: [1, [
                Validators.required,
                Validators.min(1),
                Validators.max(this.data.postiDisponibili)
            ]],
            note: ['', [Validators.maxLength(500)]]
        });
    }

    formatDate(dataString: string): string {
        return new Date(dataString).toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    formatTime(dataString: string): string {
        return new Date(dataString).toLocaleTimeString('it-IT', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onConfirm(): void {
        if (this.registrationForm.valid) {
            const result: EventoRegistrazioneRequestDTO = {
                numeroPosti: this.registrationForm.value.numeroPosti,
                note: this.registrationForm.value.note || undefined
            };
            this.dialogRef.close(result);
        }
    }
}

