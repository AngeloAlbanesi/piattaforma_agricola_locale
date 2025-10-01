import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../../../../core/guards/auth.guard';
import { roleGuard } from '../../../../core/guards/role.guard';
import { ROLES } from '../../../../core/services/auth.service';
import { ProduttoreDashboardComponent } from './pages/produttore-dashboard/produttore-dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: ProduttoreDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: {
      expectedRole: ROLES.PRODUTTORE,
      title: 'Dashboard Produttore'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProduttoreRoutingModule { }