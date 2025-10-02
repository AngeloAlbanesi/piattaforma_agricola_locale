import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';
import { ROLES } from '@core/services/auth.service';
import { AcquirenteDashboardComponent } from './pages/acquirente-dashboard/acquirente-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: AcquirenteDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.ACQUIRENTE,
            title: 'Dashboard Acquirente'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AcquirenteRoutingModule { }