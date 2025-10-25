import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';
import { ROLES } from '@core/services/auth.service';
import { CuratoreDashboardComponent } from './pages/curatore-dashboard/curatore-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: CuratoreDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.CURATORE,
            title: 'Dashboard Curatore'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class CuratoreRoutingModule { }