import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { GestioneUtentiRoutingModule } from './gestione-utenti-routing.module';
import { GestioneUtentiDashboardComponent } from './pages/dashboard/gestione-utenti-dashboard.component';

@NgModule({
    declarations: [GestioneUtentiDashboardComponent],
    imports: [
        CommonModule,
        GestioneUtentiRoutingModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTabsModule,
    ],
})
export class GestioneUtentiModule { }
