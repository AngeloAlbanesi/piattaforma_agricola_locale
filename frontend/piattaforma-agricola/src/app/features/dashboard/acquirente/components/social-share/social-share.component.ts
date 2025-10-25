import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { ShareRequestDTO, ShareResponseDTO, PiattaformaSocial } from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-social-share',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatIconModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './social-share.component.html',
    styleUrls: ['./social-share.component.scss']
})
export class SocialShareComponent implements OnInit {
    @Input() productId: number | null = null;
    @Input() productName: string = '';
    @Input() productDescription: string = '';
    @Input() productImage: string = '';

    shareForm: FormGroup = new FormGroup({});
    isSharing = false;
    shareResult: ShareResponseDTO | null = null;

    // Enum per accesso nel template
    PiattaformaSocial = PiattaformaSocial;

    constructor(
        private fb: FormBuilder,
        private acquirenteService: AcquirenteService,
        private snackBar: MatSnackBar
    ) {
        this.initializeForm();
    }

    ngOnInit(): void {
        if (this.productId) {
            this.shareForm.patchValue({
                idProdotto: this.productId,
                messaggio: `Scopri questo fantastico prodotto agricolo: ${this.productName}`
            });
        }
    }

    private initializeForm(): void {
        this.shareForm = this.fb.group({
            idProdotto: [this.productId, Validators.required],
            piattaforma: [PiattaformaSocial.FACEBOOK, Validators.required],
            messaggio: ['', [Validators.required, Validators.maxLength(500)]],
            visibilita: ['PUBBLICO']
        });
    }

    onPlatformChange(): void {
        const piattaforma = this.shareForm.value.piattaforma;
        let defaultMessage = '';

        switch (piattaforma) {
            case PiattaformaSocial.FACEBOOK:
                defaultMessage = `Scopri questo fantastico prodotto agricolo: ${this.productName}`;
                break;
            case PiattaformaSocial.INSTAGRAM:
                defaultMessage = `🌱 ${this.productName} - Prodotti agricoli di qualità!`;
                break;
            case PiattaformaSocial.TWITTER:
                defaultMessage = `Scopri ${this.productName} - Prodotti agricoli locali di qualità 🌱 #agricoltura #prodottilocali`;
                break;
            case PiattaformaSocial.WHATSAPP:
                defaultMessage = `Ti consiglio questo prodotto agricolo: ${this.productName}. ${this.productDescription}`;
                break;
        }

        this.shareForm.patchValue({
            messaggio: defaultMessage
        });
    }

    shareProduct(): void {
        if (this.shareForm.invalid || !this.productId) {
            this.markFormGroupTouched(this.shareForm);
            return;
        }

        this.isSharing = true;
        this.shareResult = null;

        const shareRequest: ShareRequestDTO = {
            nickname: 'Acquirente', // Potrebbe essere dinamico in futuro
            piattaforma: this.shareForm.value.piattaforma,
            messaggio: this.shareForm.value.messaggio
        };

        this.acquirenteService.shareProductOnSocial(this.productId!, shareRequest).subscribe({
            next: (response) => {
                this.shareResult = response;
                this.showSuccess('Prodotto condiviso con successo!');
                this.isSharing = false;
            },
            error: (error) => {
                console.error('Errore durante la condivisione:', error);
                this.showError('Errore durante la condivisione: ' + (error.message || 'Errore sconosciuto'));
                this.isSharing = false;
            }
        });
    }

    resetForm(): void {
        this.shareForm.reset({
            idProdotto: this.productId,
            piattaforma: PiattaformaSocial.FACEBOOK,
            messaggio: '',
            visibilita: 'PUBBLICO'
        });
        this.shareResult = null;
    }

    getPlatformIcon(platform: PiattaformaSocial): string {
        switch (platform) {
            case PiattaformaSocial.FACEBOOK:
                return 'facebook';
            case PiattaformaSocial.INSTAGRAM:
                return 'instagram';
            case PiattaformaSocial.TWITTER:
                return 'twitter';
            case PiattaformaSocial.WHATSAPP:
                return 'whatsapp';
            default:
                return 'share';
        }
    }

    getPlatformColor(platform: PiattaformaSocial): string {
        switch (platform) {
            case PiattaformaSocial.FACEBOOK:
                return '#1877f2';
            case PiattaformaSocial.INSTAGRAM:
                return '#e4405f';
            case PiattaformaSocial.TWITTER:
                return '#1da1f2';
            case PiattaformaSocial.WHATSAPP:
                return '#25d366';
            default:
                return '#666';
        }
    }

    getShareUrl(): string {
        if (!this.shareResult) {
            return '';
        }

        // Costruisci URL di condivisione basato sulla piattaforma selezionata nel form
        const piattaforma = this.shareForm.value.piattaforma;
        const baseUrl = window.location.origin;
        const productUrl = `${baseUrl}/prodotto/${this.productId}`;
        const message = this.shareForm.value.messaggio;

        switch (piattaforma) {
            case PiattaformaSocial.FACEBOOK:
                return `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(productUrl)}`;
            case PiattaformaSocial.TWITTER:
                return `https://twitter.com/intent/tweet?url=${encodeURIComponent(productUrl)}&text=${encodeURIComponent(message)}`;
            case PiattaformaSocial.INSTAGRAM:
                return `https://www.instagram.com/`; // Instagram non supporta condivisione diretta URL
            case PiattaformaSocial.WHATSAPP:
                return `https://wa.me/?text=${encodeURIComponent(message + ' ' + productUrl)}`;
            default:
                return productUrl;
        }
    }

    openShareWindow(): void {
        if (this.shareResult) {
            const url = this.getShareUrl();
            if (url) {
                window.open(url, '_blank', 'width=600,height=400');
            }
        }
    }

    copyShareLink(): void {
        const shareUrl = this.getShareUrl();
        if (shareUrl) {
            navigator.clipboard.writeText(shareUrl).then(() => {
                this.showSuccess('Link copiato negli appunti!');
            }).catch(() => {
                this.showError('Impossibile copiare il link');
            });
        }
    }

    private markFormGroupTouched(formGroup: FormGroup): void {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            control.updateValueAndValidity();
        });
    }

    private showSuccess(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 3000,
            panelClass: 'success-snackbar'
        });
    }

    private showError(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 3000,
            panelClass: 'error-snackbar'
        });
    }

    // Getter per validazione nel template
    get messaggioControl() {
        return this.shareForm.get('messaggio');
    }

    get piattaformaControl() {
        return this.shareForm.get('piattaforma');
    }
}