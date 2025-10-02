import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';
import { ROLES } from '@core/services/auth.service';
import { GestorePlatformaDashboardComponent } from './pages/gestore-platforma-dashboard/gestore-platforma-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: GestorePlatformaDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.GESTORE_PIATTAFORMA,
            title: 'Dashboard Gestore Piattaforma'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class GestorePlatformaRoutingModule { }