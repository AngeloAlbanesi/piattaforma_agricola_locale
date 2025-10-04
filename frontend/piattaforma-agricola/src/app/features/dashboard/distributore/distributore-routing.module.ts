import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../../../core/guards/auth.guard';
import { roleGuard } from '../../../core/guards/role.guard';
import { ROLES } from '../../../core/services/auth.service';
import { DistributoreDashboardComponent } from './pages/distributore-dashboard/distributore-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: DistributoreDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.DISTRIBUTORE_TIPICITA,
            title: 'Dashboard Distributore'
        }
    },
    {
        path: 'prodotti-disponibili',
        loadComponent: () => import('./components/available-products/available-products.component').then(m => m.AvailableProductsComponent),
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.DISTRIBUTORE_TIPICITA,
            title: 'Prodotti Disponibili'
        }
    },
    {
        path: 'ordini',
        loadComponent: () => import('./components/orders-management/orders-management.component').then(m => m.OrdersManagementComponent),
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.DISTRIBUTORE_TIPICITA,
            title: 'Gestione Ordini'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class DistributoreRoutingModule { }