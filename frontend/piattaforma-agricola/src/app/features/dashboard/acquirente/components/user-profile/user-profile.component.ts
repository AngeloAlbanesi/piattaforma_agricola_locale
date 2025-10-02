import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil, finalize } from 'rxjs';

import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { UserDetailDTO, UserUpdateDTO } from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-user-profile',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatIconModule
    ],
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Definite assignment assertion: inizializzato nel costruttore tramite initializeForm()
    profileForm!: FormGroup;
    isLoading = false;
    isSaving = false;
    profileData: UserDetailDTO | null = null;
    isEditMode = false;

    constructor(
        private fb: FormBuilder,
        private acquirenteService: AcquirenteService,
        private snackBar: MatSnackBar
    ) {
        this.initializeForm();
    }

    ngOnInit(): void {
        this.loadUserProfile();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeForm(): void {
        this.profileForm = this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(2)]],
            cognome: ['', [Validators.required, Validators.minLength(2)]],
            email: ['', [Validators.required, Validators.email]],
            telefono: [''],
            indirizzo: ['']
        });

        // In modalità visualizzazione, disabilita tutti i campi
        this.profileForm.disable();
    }

    // === GESTIONE PROFILO ===

    private loadUserProfile(): void {
        this.isLoading = true;

        this.acquirenteService.getProfile()
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => this.isLoading = false)
            )
            .subscribe({
                next: (profile) => {
                    this.profileData = profile;
                    this.populateForm(profile);
                },
                error: (error) => {
                    this.snackBar.open('Errore nel caricamento del profilo', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    private populateForm(profile: UserDetailDTO): void {
        this.profileForm.patchValue({
            nome: profile.nome,
            cognome: profile.cognome,
            email: profile.email,
            telefono: profile.telefono || '',
            indirizzo: profile.indirizzo || ''
        });
    }

    // === GESTIONE FORM ===

    toggleEditMode(): void {
        this.isEditMode = !this.isEditMode;

        if (this.isEditMode) {
            this.profileForm.enable();
        } else {
            this.profileForm.disable();
            // Ripristina i valori originali
            if (this.profileData) {
                this.populateForm(this.profileData);
            }
        }
    }

    cancelEdit(): void {
        this.isEditMode = false;
        this.profileForm.disable();

        if (this.profileData) {
            this.populateForm(this.profileData);
        }
    }

    saveProfile(): void {
        if (this.profileForm.invalid) {
            this.markFormGroupTouched(this.profileForm);
            return;
        }

        this.isSaving = true;
        const updateData: UserUpdateDTO = this.profileForm.value;

        this.acquirenteService.updateProfile(updateData)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => this.isSaving = false)
            )
            .subscribe({
                next: (updatedProfile) => {
                    this.profileData = updatedProfile;
                    this.isEditMode = false;
                    this.profileForm.disable();
                    this.snackBar.open('Profilo aggiornato con successo', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                },
                error: (error) => {
                    this.snackBar.open('Errore nell\'aggiornamento del profilo', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    // === UTILITIES ===

    private markFormGroupTouched(formGroup: FormGroup): void {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
            }
        });
    }

    getFormErrorMessage(field: string): string {
        const control = this.profileForm.get(field);

        if (control?.hasError('required')) {
            return 'Questo campo è obbligatorio';
        }

        if (control?.hasError('email')) {
            return 'Inserisci un\'email valida';
        }

        if (control?.hasError('minlength')) {
            return `Inserisci almeno ${control.errors?.['minlength'].requiredLength} caratteri`;
        }

        return '';
    }

    // === GETTERS PER TEMPLATE ===

    get isFormValid(): boolean {
        return this.profileForm.valid;
    }

    get hasUnsavedChanges(): boolean {
        return this.isEditMode && this.profileForm.dirty;
    }
}