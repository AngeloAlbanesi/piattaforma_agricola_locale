import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-gestione-utenti-dashboard',
    templateUrl: './gestione-utenti-dashboard.component.html',
    styleUrls: ['./gestione-utenti-dashboard.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GestioneUtentiDashboardComponent { }
