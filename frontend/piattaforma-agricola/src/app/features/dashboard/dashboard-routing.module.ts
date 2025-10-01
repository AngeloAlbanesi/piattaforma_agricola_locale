import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';
import { roleGuard } from '../../core/guards/role.guard';
import { ROLES } from '../../core/services/auth.service';

// Import delle dashboard (lazy loading)
const routes: Routes = [
  {
    path: 'dashboard-produttore',
    loadChildren: () => import('./produttore/produttore.module').then(m => m.ProduttoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.PRODUTTORE }
  },
  {
    path: 'dashboard-trasformatore',
    loadChildren: () => import('./trasformatore/trasformatore.module').then(m => m.TrasformatoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.TRASFORMATORE }
  },
  {
    path: 'dashboard-distributore',
    loadChildren: () => import('./distributore/distributore.module').then(m => m.DistributoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.DISTRIBUTORE_TIPICITA }
  },
  {
    path: 'dashboard-curatore',
    loadChildren: () => import('./curatore/curatore.module').then(m => m.CuratoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.CURATORE }
  },
  {
    path: 'dashboard-animatore',
    loadChildren: () => import('./animatore/animatore.module').then(m => m.AnimatoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.ANIMATORE_FILIERA }
  },
  {
    path: 'dashboard-acquirente',
    loadChildren: () => import('./acquirente/acquirente.module').then(m => m.AcquirenteModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.ACQUIRENTE }
  },
  {
    path: 'dashboard-admin',
    loadChildren: () => import('./gestore/gestore.module').then(m => m.GestoreModule),
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: ROLES.GESTORE_PIATTAFORMA }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }