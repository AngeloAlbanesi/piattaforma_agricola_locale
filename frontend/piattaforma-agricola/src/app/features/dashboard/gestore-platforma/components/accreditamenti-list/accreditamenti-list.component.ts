import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AdminService } from '../../../../../core/services/admin.service';
import { AdminUserDTO, AccreditamentoStato } from '../../../../../core/models/admin.models';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CompanyDetailsDialogComponent } from '../company-details-dialog/company-details-dialog.component';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-accreditamenti-list',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatDialogModule
    ],
    templateUrl: './accreditamenti-list.component.html',
    styleUrls: ['./accreditamenti-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AccreditamentiListComponent implements OnInit {
    @Input() tipo: 'venditori' | 'curatori' | 'animatori' = 'venditori';
    readonly Stato = AccreditamentoStato;

    displayedColumns = ['idUtente', 'nome', 'cognome', 'email', 'ruolo', 'actions'];
    isLoading = false;
    data: AdminUserDTO[] = [];

    constructor(
        private adminService: AdminService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        const tipoFromRoute = this.route.snapshot.data?.['tipo'] as ('venditori' | 'curatori' | 'animatori') | undefined;
        if (tipoFromRoute) {
            this.tipo = tipoFromRoute;
        }
        this.load();
    }

    load(): void {
        this.isLoading = true;
        let obs;
        switch (this.tipo) {
            case 'venditori':
                obs = this.adminService.getPendingVenditori();
                break;
            case 'curatori':
                obs = this.adminService.getPendingCuratori();
                break;
            case 'animatori':
                obs = this.adminService.getPendingAnimatori();
                break;
        }
        obs.subscribe({
            next: (list: AdminUserDTO[]) => { this.data = list; this.isLoading = false; },
            error: () => { this.snackBar.open('Errore nel caricamento', 'Chiudi', { duration: 3000 }); this.isLoading = false; }
        });
    }

    changeState(user: AdminUserDTO, stato: AccreditamentoStato): void {
        let obs;
        switch (this.tipo) {
            case 'venditori':
                obs = this.adminService.updateAccreditamentoVenditore(user.idUtente, stato); break;
            case 'curatori':
                obs = this.adminService.updateAccreditamentoCuratore(user.idUtente, stato); break;
            case 'animatori':
                obs = this.adminService.updateAccreditamentoAnimatore(user.idUtente, stato); break;
        }
        obs.subscribe({
            next: (msg: string) => { this.snackBar.open(msg, 'Chiudi', { duration: 2500 }); this.load(); },
            error: () => this.snackBar.open('Errore nell\'aggiornamento stato', 'Chiudi', { duration: 3000 })
        });
    }

    viewCompany(user: AdminUserDTO): void {
        if (this.tipo !== 'venditori') return;
        this.dialog.open(CompanyDetailsDialogComponent, {
            width: '600px',
            data: { userId: user.idUtente }
        });
    }
}
