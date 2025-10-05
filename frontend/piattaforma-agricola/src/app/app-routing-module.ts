import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadChildren: () => import('./features/public/public.module').then(m => m.PublicModule),
    },
    {
        path: 'profilo',
        loadComponent: () => import('./features/profilo/pages/user-profile/user-profile.component').then(m => m.UserProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule),
    },
    // Routes removed: catalogo, carrello, eventi, gestione-utenti, processi
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
