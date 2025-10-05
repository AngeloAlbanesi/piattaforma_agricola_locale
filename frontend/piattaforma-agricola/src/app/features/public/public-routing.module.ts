import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Lazy loading dei componenti delle pagine
const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent),
    title: 'Piattaforma Agricola Locale - Home'
  },
  {
    path: 'prodotti',
    loadComponent: () => import('./pages/prodotti/prodotti-page.component').then(m => m.ProdottiPageComponent),
    title: 'Catalogo Prodotti - Piattaforma Agricola Locale'
  },
  {
    path: 'prodotti/:id',
    loadComponent: () => import('./pages/prodotto-detail/prodotto-detail.component').then(m => m.ProdottoDetailComponent),
    title: 'Dettaglio Prodotto - Piattaforma Agricola Locale'
  },
  {
    path: 'pacchetti',
    loadComponent: () => import('./pages/pacchetti/pacchetti-page.component').then(m => m.PacchettiPageComponent),
    title: 'Pacchetti - Piattaforma Agricola Locale'
  },
  {
    path: 'pacchetti/:id',
    loadComponent: () => import('./pages/pacchetto-detail/pacchetto-detail.component').then(m => m.PacchettoDetailComponent),
    title: 'Dettaglio Pacchetto - Piattaforma Agricola Locale'
  },
  {
    path: 'eventi',
    loadComponent: () => import('./pages/eventi/eventi-page.component').then(m => m.EventiPageComponent),
    title: 'Eventi - Piattaforma Agricola Locale'
  },
  {
    path: 'eventi/:id',
    loadComponent: () => import('./pages/evento-detail/evento-detail.component').then(m => m.EventoDetailComponent),
    title: 'Dettaglio Evento - Piattaforma Agricola Locale'
  },
  {
    path: 'aziende',
    loadComponent: () => import('./pages/aziende/aziende-page.component').then(m => m.AziendePageComponent),
    title: 'Aziende - Piattaforma Agricola Locale'
  },
  {
    path: 'aziende/:id',
    loadComponent: () => import('./pages/azienda-detail/azienda-detail.component').then(m => m.AziendaDetailComponent),
    title: 'Dettaglio Azienda - Piattaforma Agricola Locale'
  },
  {
    path: 'processi',
    loadComponent: () => import('./pages/processi/processi-page.component').then(m => m.ProcessiPageComponent),
    title: 'Processi di Trasformazione - Piattaforma Agricola Locale'
  },
  {
    path: 'processi/:id',
    loadComponent: () => import('./pages/processo-detail/processo-detail.component').then(m => m.ProcessoDetailComponent),
    title: 'Dettaglio Processo - Piattaforma Agricola Locale'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PublicRoutingModule {}