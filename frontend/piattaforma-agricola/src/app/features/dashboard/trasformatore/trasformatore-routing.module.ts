import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';
import { ROLES } from '@core/services/auth.service';
import { TrasformatoreDashboardComponent } from './pages/trasformatore-dashboard/trasformatore-dashboard.component';
import { ProdottiPageComponent } from './pages/prodotti-page/prodotti-page.component';
import { OrdiniPageComponent } from './pages/ordini-page/ordini-page.component';
import { ProcessoDetailPageComponent } from './pages/processo-detail-page/processo-detail-page.component';
import { OrdineVenditoreDetailComponent } from '../shared/pages/ordine-venditore-detail/ordine-venditore-detail.component';

const routes: Routes = [
    {
        path: '',
        component: TrasformatoreDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.TRASFORMATORE,
            title: 'Dashboard Trasformatore'
        }
    },
    {
        path: 'prodotti',
        component: ProdottiPageComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.TRASFORMATORE,
            title: 'Gestione Prodotti'
        }
    },
    {
        path: 'ordini',
        component: OrdiniPageComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.TRASFORMATORE,
            title: 'Gestione Ordini'
        }
    },
    {
        path: 'ordini/:id',
        component: OrdineVenditoreDetailComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.TRASFORMATORE,
            title: 'Dettaglio Ordine'
        }
    },
    {
        path: 'processi/:id',
        component: ProcessoDetailPageComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.TRASFORMATORE,
            title: 'Dettagli Processo'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TrasformatoreRoutingModule { }