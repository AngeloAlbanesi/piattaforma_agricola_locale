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
import { CatalogService } from './core/services/catalog.service';

// Le rotte sono state estratte da app-routing-module.ts
const routes: Routes = [
    // Public routes - espanse inline per permettere routing corretto
    {
        path: '',
        loadComponent: () => import('./features/public/pages/home/home.component').then(m => m.HomeComponent),
        pathMatch: 'full',
        title: 'Piattaforma Agricola Locale - Home'
    },
    {
        path: 'prodotti/:id',
        loadComponent: () => import('./features/public/pages/prodotto-detail/prodotto-detail.component').then(m => m.ProdottoDetailComponent),
        title: 'Dettaglio Prodotto - Piattaforma Agricola Locale'
    },
    {
        path: 'pacchetti',
        loadComponent: () => import('./features/public/pages/pacchetti/pacchetti-page.component').then(m => m.PacchettiPageComponent),
        title: 'Pacchetti - Piattaforma Agricola Locale'
    },
    {
        path: 'pacchetti/:id',
        loadComponent: () => import('./features/public/pages/pacchetto-detail/pacchetto-detail.component').then(m => m.PacchettoDetailComponent),
        title: 'Dettaglio Pacchetto - Piattaforma Agricola Locale'
    },
    {
        path: 'eventi',
        loadComponent: () => import('./features/public/pages/eventi/eventi-page.component').then(m => m.EventiPageComponent),
        title: 'Eventi - Piattaforma Agricola Locale'
    },
    {
        path: 'eventi/:id',
        loadComponent: () => import('./features/public/pages/evento-detail/evento-detail.component').then(m => m.EventoDetailComponent),
        title: 'Dettaglio Evento - Piattaforma Agricola Locale'
    },
    {
        path: 'aziende',
        loadComponent: () => import('./features/public/pages/aziende/aziende-page.component').then(m => m.AziendePageComponent),
        title: 'Aziende - Piattaforma Agricola Locale'
    },
    {
        path: 'aziende/:id',
        loadComponent: () => import('./features/public/pages/azienda-detail/azienda-detail.component').then(m => m.AziendaDetailComponent),
        title: 'Dettaglio Azienda - Piattaforma Agricola Locale'
    },
    {
        path: 'processi',
        loadComponent: () => import('./features/public/pages/processi/processi-page.component').then(m => m.ProcessiPageComponent),
        title: 'Processi di Trasformazione - Piattaforma Agricola Locale'
    },
    {
        path: 'processi/:id',
        loadComponent: () => import('./features/public/pages/processo-detail/processo-detail.component').then(m => m.ProcessoDetailComponent),
        title: 'Dettaglio Processo - Piattaforma Agricola Locale'
    },
    // Altri routes
    {
        path: 'profilo',
        loadComponent: () => import('./features/profilo/pages/user-profile/user-profile.component').then(m => m.UserProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule),
    },
    {
        path: 'catalogo',
        loadChildren: () => import('./features/catalogo/catalogo.module').then(m => m.CatalogoModule),
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
        // Servizio catalogo
        CatalogService,
    ]
};