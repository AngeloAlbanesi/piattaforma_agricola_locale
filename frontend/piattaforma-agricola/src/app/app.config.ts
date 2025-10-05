import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { PreloadAllModules, provideRouter, Routes, withPreloading } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';

import { authGuard } from './core/guards/auth.guard';
import { authTokenInterceptor } from './core/interceptors/auth-token.interceptor';
import { PublicProdottiService } from './core/services/public-prodotti.service';
import { PublicPacchettiService } from './core/services/public-pacchetti.service';
import { PublicEventiService } from './core/services/public-eventi.service';
import { PublicAziendeService } from './core/services/public-aziende.service';
import { PublicProcessiService } from './core/services/public-processi.service';

// Le rotte sono state estratte da app-routing-module.ts
const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadChildren: () => import('./features/public/public.routes').then(m => m.PUBLIC_ROUTES),
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

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes, withPreloading(PreloadAllModules)),
        provideHttpClient(withInterceptors([authTokenInterceptor])),
        provideBrowserGlobalErrorListeners(),
        provideNoopAnimations(), // Rimosso BrowserAnimationsModule
        {
            provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
            useValue: { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top' },
        },
        // Servizi pubblici migrati da PublicModule
        PublicProdottiService,
        PublicPacchettiService,
        PublicEventiService,
        PublicAziendeService,
        PublicProcessiService,
    ]
};