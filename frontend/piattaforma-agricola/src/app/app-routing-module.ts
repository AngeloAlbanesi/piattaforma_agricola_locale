import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadChildren: () => import('./features/landing/landing.module').then(m => m.LandingModule),
    },
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule),
    },
    {
        path: 'catalogo',
        loadChildren: () => import('./features/catalogo').then(m => m.CatalogoModule),
    },
    {
        path: 'carrello',
        loadChildren: () => import('./features/carrello-ordini/carrello-ordini.module').then(m => m.CarrelloOrdiniModule),
    },
    {
        path: 'eventi',
        loadChildren: () => import('./features/eventi/eventi.module').then(m => m.EventiModule),
    },
    {
        path: 'gestione-utenti',
        loadChildren: () => import('./features/gestione-utenti/gestione-utenti.module').then(m => m.GestioneUtentiModule),
    },
    {
        path: 'processi',
        loadChildren: () => import('./features/processi/processi.module').then(m => m.ProcessiModule),
    },
    {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard-routing.module').then(m => m.DashboardRoutingModule),
    },
    {
        path: '**',
        redirectTo: '',
    },
];

@NgModule({
    imports: [RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })],
    exports: [RouterModule]
})
export class AppRoutingModule { }
