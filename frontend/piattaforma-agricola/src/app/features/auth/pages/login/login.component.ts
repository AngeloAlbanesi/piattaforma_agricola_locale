import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ErrorStateMatcher } from '@angular/material/core';
import { AuthService, LoginRequest } from '../../../../core/services/auth.service';

@Component({
    selector: 'app-auth-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
    loginForm: FormGroup;
    isLoading = false;
    hidePassword = true;

    // Error state matcher for form fields
    errorStateMatcher: ErrorStateMatcher = {
        isErrorState: (control, form) => {
            const isSubmitted = form && form.submitted;
            return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
        }
    };

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router,
        private route: ActivatedRoute,
        private snackBar: MatSnackBar,
        private dialog: MatDialog
    ) {
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });

        // Se utente già autenticato, reindirizza alla dashboard appropriata
        if (this.authService.isAuthenticated()) {
            this.redirectToDashboard();
        }
    }

    onSubmit(): void {
        if (this.loginForm.invalid) {
            this.snackBar.open('Per favore compila tutti i campi correttamente', 'Chiudi', {
                duration: 3000,
                panelClass: 'error-snackbar'
            });
            return;
        }

        this.isLoading = true;

        const loginRequest: LoginRequest = {
            email: this.loginForm.value.email!,
            password: this.loginForm.value.password!
        };

        this.authService.login(loginRequest).subscribe({
            next: (response) => {
                this.isLoading = false;
                this.snackBar.open(`Benvenuto ${response.username}!`, 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.redirectToDashboard();
            },
            error: (error) => {
                this.isLoading = false;
                let errorMessage = 'Credenziali non valide. Riprova.';

                if (error.status === 401) {
                    errorMessage = 'Email o password errati.';
                } else if (error.status === 403) {
                    errorMessage = 'Accesso negato. Utente non autorizzato.';
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

    private redirectToDashboard(): void {
        const role = this.authService.getRole();
        console.log('🔍 Redirect con ruolo:', role);

        switch (role) {
            case 'PRODUTTORE':
                this.router.navigate(['/dashboard/produttore']);
                break;
            case 'TRASFORMATORE':
                this.router.navigate(['/dashboard/trasformatore']);
                break;
            case 'DISTRIBUTORE_DI_TIPICITA':
                this.router.navigate(['/dashboard/distributore']);
                break;
            case 'CURATORE':
                this.router.navigate(['/dashboard/curatore']);
                break;
            case 'ANIMATORE_DELLA_FILIERA':
                this.router.navigate(['/dashboard/animatore']);
                break;
            case 'ACQUIRENTE':
                this.router.navigate(['/dashboard/acquirente']);
                break;
            case 'GESTORE_PIATTAFORMA':
                this.router.navigate(['/dashboard/admin']);
                break;
            default:
                console.warn('⚠️ Ruolo non riconosciuto:', role);
                this.router.navigate(['/']);
        }
    }

    goToRegistration(): void {
        this.router.navigate(['/auth/register']);
    }

    togglePasswordVisibility(): void {
        this.hidePassword = !this.hidePassword;
    }

    openForgotPasswordDialog(): void {
        this.dialog.open(ForgotPasswordDialogComponent, {
            width: '400px',
        });
    }

    getErrorMessage(field: string): string {
        const control = this.loginForm.get(field);
        if (control?.hasError('required') && control?.touched) {
            return 'Questo campo è obbligatorio';
        }
        if (control?.hasError('email') && control?.touched) {
            return 'Inserisci un indirizzo email valido';
        }
        if (control?.hasError('minlength') && control?.touched) {
            const minLength = control.errors?.['minlength']['requiredLength'];
            return `Minimo ${minLength} caratteri`;
        }
        return '';
    }
}

@Component({
    selector: 'app-forgot-password-dialog',
    standalone: false,
    template: `
        <div class="dialog-container">
            <h2 mat-dialog-title>
                <mat-icon class="dialog-icon">sentiment_satisfied_alt</mat-icon>
                Recupero Password
            </h2>
            <mat-dialog-content>
                <p class="dialog-message">
                    E da me che voi? Io al massimo te posso cantà na canzone
                </p>
            </mat-dialog-content>
            <mat-dialog-actions align="end">
                <button mat-raised-button color="primary" mat-dialog-close>
                    Va bene, grazie!
                </button>
            </mat-dialog-actions>
        </div>
    `,
    styles: [`
        .dialog-container {
            padding: 1rem;
        }

        h2 {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: #2e7d32;
            margin: 0 0 1rem 0;
        }

        .dialog-icon {
            font-size: 2rem;
            width: 2rem;
            height: 2rem;
        }

        .dialog-message {
            font-size: 1.1rem;
            line-height: 1.6;
            color: #424242;
            margin: 1.5rem 0;
            text-align: center;
        }

        mat-dialog-actions {
            padding: 1rem 0 0 0;
        }
    `]
})
export class ForgotPasswordDialogComponent { }
