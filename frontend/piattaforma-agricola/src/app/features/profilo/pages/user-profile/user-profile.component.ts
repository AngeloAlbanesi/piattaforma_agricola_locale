import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../../core/services/auth.service';
import { UserDetailDTO, UserUpdateDTO } from '../../../../core/models/curatore.models';

@Component({
    selector: 'app-user-profile',
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatSnackBarModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
    isLoading = false;
    profile: UserDetailDTO | null = null;
    // inizializzato in ngOnInit per evitare uso di fb prima dell'iniezione
    form: any;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        // Inizializza il form dopo l'iniezione di fb
        this.form = this.fb.nonNullable.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            email: ['', [Validators.required, Validators.email]],
            nome: [''],
            cognome: [''],
            telefono: [''],
            indirizzo: ['']
        });
        this.loadProfile();
    }

    private loadProfile(): void {
        this.isLoading = true;
        this.authService.getProfile().subscribe({
            next: (p) => {
                this.profile = p;
                this.form.patchValue({
                    username: p.username,
                    email: p.email,
                    nome: p.nome || '',
                    cognome: p.cognome || '',
                    telefono: p.telefono || '',
                    indirizzo: p.indirizzo || ''
                });
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Errore caricamento profilo', err);
                this.snackBar.open('Impossibile caricare il profilo', 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                this.isLoading = false;
            }
        });
    }

    save(): void {
        if (this.form.invalid) {
            this.snackBar.open('Controlla i campi evidenziati', 'Chiudi', { duration: 3000, panelClass: 'warning-snackbar' });
            this.form.markAllAsTouched();
            return;
        }
        this.isLoading = true;
        const payload: UserUpdateDTO = this.form.getRawValue();
        this.authService.updateProfile(payload).subscribe({
            next: (updated) => {
                this.profile = updated;
                this.snackBar.open('Profilo aggiornato', 'Chiudi', { duration: 2500, panelClass: 'success-snackbar' });
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Errore aggiornamento profilo', err);
                this.snackBar.open('Errore durante l\'aggiornamento del profilo', 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                this.isLoading = false;
            }
        });
    }
}
