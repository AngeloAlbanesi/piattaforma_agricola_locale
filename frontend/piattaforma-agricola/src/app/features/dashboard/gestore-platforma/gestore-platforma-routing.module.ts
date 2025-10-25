import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';
import { ROLES } from '@core/services/auth.service';
import { GestorePlatformaDashboardComponent } from './pages/gestore-platforma-dashboard/gestore-platforma-dashboard.component';
import { AccreditamentiListComponent } from './components/accreditamenti-list/accreditamenti-list.component';
import { UtentiManagementComponent } from './components/utenti-management/utenti-management.component';

const routes: Routes = [
    {
        path: '',
        component: GestorePlatformaDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: ROLES.GESTORE_PIATTAFORMA, title: 'Dashboard Gestore Piattaforma' }
    },
    {
        path: 'utenti',
        component: UtentiManagementComponent,
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: ROLES.GESTORE_PIATTAFORMA, title: 'Gestione Utenti' }
    },
    {
        path: 'accreditamenti/venditori',
        component: AccreditamentiListComponent,
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: ROLES.GESTORE_PIATTAFORMA, tipo: 'venditori', title: 'Accreditamenti Venditori' }
    },
    {
        path: 'accreditamenti/curatori',
        component: AccreditamentiListComponent,
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: ROLES.GESTORE_PIATTAFORMA, tipo: 'curatori', title: 'Accreditamenti Curatori' }
    },
    {
        path: 'accreditamenti/animatori',
        component: AccreditamentiListComponent,
        canActivate: [authGuard, roleGuard],
        data: { expectedRole: ROLES.GESTORE_PIATTAFORMA, tipo: 'animatori', title: 'Accreditamenti Animatori' }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class GestorePlatformaRoutingModule { }