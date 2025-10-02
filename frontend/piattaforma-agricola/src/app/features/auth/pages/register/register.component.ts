import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ErrorStateMatcher } from '@angular/material/core';
import { AuthService, RegisterRequest, ROLES } from '../../../../core/services/auth.service';

@Component({
    selector: 'app-auth-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
    registerForm: FormGroup;
    isLoading = false;
    hidePassword = true;
    selectedRole: string | null = null;

    // Error state matcher for form fields
    errorStateMatcher: ErrorStateMatcher = {
        isErrorState: (control, form) => {
            const isSubmitted = form && form.submitted;
            return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
        }
    };

    // Available roles with descriptions
    availableRoles = [
        {
            value: ROLES.PRODUTTORE,
            label: 'Produttore',
            description: 'Vendi i tuoi prodotti agricoli direttamente',
            icon: 'agriculture',
            needsCompanyData: true
        },
        {
            value: ROLES.TRASFORMATORE,
            label: 'Trasformatore',
            description: 'Trasforma materie prime in prodotti finiti',
            icon: 'factory',
            needsCompanyData: true
        },
        {
            value: ROLES.DISTRIBUTORE_TIPICITA,
            label: 'Distributore',
            description: 'Crea e distribuisci pacchetti tipici',
            icon: 'store',
            needsCompanyData: true
        },
        {
            value: ROLES.CURATORE,
            label: 'Curatore',
            description: 'Verifica e valida contenuti e prodotti',
            icon: 'verified',
            needsCompanyData: false
        },
        {
            value: ROLES.ANIMATORE_FILIERA,
            label: 'Animatore',
            description: 'Organizza eventi per la filiera',
            icon: 'event',
            needsCompanyData: true
        },
        {
            value: ROLES.ACQUIRENTE,
            label: 'Acquirente',
            description: 'Acquista prodotti agricoli',
            icon: 'shopping_cart',
            needsCompanyData: false
        },
        {
            value: ROLES.GESTORE_PIATTAFORMA,
            label: 'Gestore Piattaforma',
            description: 'Gestisci l\'intera piattaforma',
            icon: 'admin_panel_settings',
            needsCompanyData: false
        }
    ];

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router,
        private snackBar: MatSnackBar
    ) {
        this.registerForm = this.createForm();
    }

    private createForm(): FormGroup {
        return this.fb.group({
            // Dati personali
            nome: ['', [Validators.required, Validators.minLength(2)]],
            cognome: ['', [Validators.required, Validators.minLength(2)]],
            username: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[a-zA-Z0-9_]+$')]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$')]],
            confirmPassword: ['', [Validators.required]],
            telefono: ['', [Validators.pattern('^[0-9]{10}$')]],
            indirizzo: [''],
            ruolo: ['', [Validators.required]],

            // Dati aziendali (condizionali)
            ragioneSociale: [''],
            partitaIva: ['', [Validators.pattern('^[0-9]{11}$')]],
            indirizzoAzienda: [''],
            descrizioneAzienda: ['']
        }, {
            validators: this.passwordMatchValidator
        });
    }


    passwordMatchValidator(form: FormGroup): { [key: string]: boolean } | null {
        const password = form.get('password');
        const confirmPassword = form.get('confirmPassword');

        if (password && confirmPassword && password.value !== confirmPassword.value) {
            confirmPassword.setErrors({ passwordMismatch: true });
            return { passwordMismatch: true };
        }

        return null;
    }

    onRoleSelect(role: string): void {
        this.selectedRole = role;
        this.registerForm.get('ruolo')?.setValue(role);

        // Reset campi aziendali quando si cambia ruolo
        this.registerForm.get('ragioneSociale')?.setValue('');
        this.registerForm.get('partitaIva')?.setValue('');
        this.registerForm.get('indirizzoAzienda')?.setValue('');
        this.registerForm.get('descrizioneAzienda')?.setValue('');

        // Aggiorna validazione campi aziendali
        this.updateCompanyFieldsValidation(role);
    }

    private updateCompanyFieldsValidation(role: string): void {
        const selectedRoleData = this.availableRoles.find(r => r.value === role);
        const needsCompanyData = selectedRoleData?.needsCompanyData || false;

        const ragioneSocialeControl = this.registerForm.get('ragioneSociale');
        const partitaIvaControl = this.registerForm.get('partitaIva');
        const indirizzoAziendaControl = this.registerForm.get('indirizzoAzienda');
        const descrizioneAziendaControl = this.registerForm.get('descrizioneAzienda');

        if (needsCompanyData) {
            ragioneSocialeControl?.setValidators([Validators.required]);
            partitaIvaControl?.setValidators([Validators.required, Validators.pattern('^[0-9]{11}$')]);
            indirizzoAziendaControl?.setValidators([Validators.required]);
            descrizioneAziendaControl?.setValidators([Validators.required]);
        } else {
            ragioneSocialeControl?.clearValidators();
            partitaIvaControl?.clearValidators();
            indirizzoAziendaControl?.clearValidators();
            descrizioneAziendaControl?.clearValidators();
        }

        ragioneSocialeControl?.updateValueAndValidity();
        partitaIvaControl?.updateValueAndValidity();
        indirizzoAziendaControl?.updateValueAndValidity();
        descrizioneAziendaControl?.updateValueAndValidity();
    }

    onSubmit(): void {
        if (this.registerForm.invalid) {
            this.snackBar.open('Per favore compila tutti i campi correttamente', 'Chiudi', {
                duration: 3000,
                panelClass: 'error-snackbar'
            });
            return;
        }

        this.isLoading = true;

        const formValue = this.registerForm.value;
        const registerRequest: RegisterRequest = {
            nome: formValue.nome,
            cognome: formValue.cognome,
            username: formValue.username,
            email: formValue.email,
            password: formValue.password,
            ruolo: formValue.ruolo,
            telefono: formValue.telefono || undefined,
            indirizzo: formValue.indirizzo || undefined
        };

        // Aggiungi dati aziendali se il ruolo li richiede
        if (this.needsCompanyData()) {
            registerRequest.datiAzienda = {
                nomeAzienda: formValue.ragioneSociale,
                partitaIva: formValue.partitaIva,
                indirizzoAzienda: formValue.indirizzoAzienda,
                descrizioneAzienda: formValue.descrizioneAzienda
            };
        }

        this.authService.register(registerRequest).subscribe({
            next: (response) => {
                this.isLoading = false;
                this.snackBar.open(`Registrazione completata! Benvenuto ${response.username}!`, 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });

                // Reindirizza alla dashboard appropriata dopo breve attesa
                setTimeout(() => {
                    this.redirectToDashboard(response.roles[0]);
                }, 1500);
            },
            error: (error) => {
                this.isLoading = false;
                let errorMessage = 'Errore durante la registrazione. Riprova.';

                if (error.status === 409) {
                    errorMessage = 'Username o email già in uso.';
                } else if (error.status === 400) {
                    errorMessage = 'Dati non validi. Controlla i campi inseriti.';
                } else if (error.status === 0) {
                    errorMessage = 'Impossibile connettersi al server. Riprova più tardi.';
                }

                this.snackBar.open(errorMessage, 'Chiudi', {
                    duration: 4000,
                    panelClass: 'error-snackbar'
                });
            }
        });
    }

    private redirectToDashboard(role: string): void {
        switch (role) {
            case ROLES.PRODUTTORE:
                this.router.navigate(['/dashboard/produttore']);
                break;
            case ROLES.TRASFORMATORE:
                this.router.navigate(['/dashboard/trasformatore']);
                break;
            case ROLES.DISTRIBUTORE_TIPICITA:
                this.router.navigate(['/dashboard/distributore']);
                break;
            case ROLES.CURATORE:
                this.router.navigate(['/dashboard/curatore']);
                break;
            case ROLES.ANIMATORE_FILIERA:
                this.router.navigate(['/dashboard/animatore']);
                break;
            case ROLES.ACQUIRENTE:
                this.router.navigate(['/catalogo']);
                break;
            case ROLES.GESTORE_PIATTAFORMA:
                this.router.navigate(['/dashboard/admin']);
                break;
            default:
                this.router.navigate(['/']);
        }
    }

    goToLogin(): void {
        this.router.navigate(['/auth/login']);
    }

    togglePasswordVisibility(): void {
        this.hidePassword = !this.hidePassword;
    }

    getErrorMessage(field: string): string {
        const control = this.registerForm.get(field);

        if (!control || !control.errors || !control.touched) {
            return '';
        }

        if (control.errors['required']) {
            return 'Questo campo è obbligatorio';
        }

        if (control.errors['email']) {
            return 'Inserisci un\'email valida';
        }

        if (control.errors['minlength']) {
            const minLength = control.errors['minlength']['requiredLength'];
            return `Minimo ${minLength} caratteri`;
        }

        if (control.errors['pattern']) {
            switch (field) {
                case 'username':
                    return 'Solo lettere, numeri e underscore';
                case 'password':
                    return 'Deve contenere almeno una maiuscola, una minuscola e un numero';
                case 'telefono':
                    return 'Inserisci un numero di telefono valido (10 cifre)';
                case 'partitaIva':
                    return 'Inserisci una Partita IVA valida (11 cifre)';
                default:
                    return 'Formato non valido';
            }
        }

        if (control.errors['passwordMismatch']) {
            return 'Le password non coincidono';
        }

        return '';
    }

    needsCompanyData(): boolean {
        if (!this.selectedRole) return false;
        const selectedRoleData = this.availableRoles.find(r => r.value === this.selectedRole);
        return selectedRoleData?.needsCompanyData || false;
    }
}