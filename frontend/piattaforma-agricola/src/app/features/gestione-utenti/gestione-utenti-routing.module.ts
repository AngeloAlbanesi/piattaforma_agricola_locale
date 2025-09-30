import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GestioneUtentiDashboardComponent } from './pages/dashboard/gestione-utenti-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: GestioneUtentiDashboardComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class GestioneUtentiRoutingModule { }
