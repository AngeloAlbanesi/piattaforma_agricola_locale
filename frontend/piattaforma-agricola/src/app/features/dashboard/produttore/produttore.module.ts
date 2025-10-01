import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSliderModule } from '@angular/material/slider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatStepperModule } from '@angular/material/stepper';
import { MatProgressBarModule } from '@angular/material/progress-bar';

// Feature Modules
import { ProduttoreRoutingModule } from './produttore-routing.module';
import { ProduttoreDashboardComponent } from './pages/produttore-dashboard/produttore-dashboard.component';

// Components
import { ProduttoreStatsOverviewComponent } from './components/produttore-stats-overview/produttore-stats-overview.component';
import { ProduttoreQuickActionsComponent } from './components/produttore-quick-actions/produttore-quick-actions.component';
import { ProdottiManagementComponent } from './components/prodotti-management/prodotti-management.component';
import { OrdiniManagementComponent } from './components/ordini-management/ordini-management.component';
import { CertificazioniManagementComponent } from './components/certificazioni-management/certificazioni-management.component';
import { MetodiColtivazioneComponent } from './components/metodi-coltivazione/metodi-coltivazione.component';
import { ProduttoreProductCardComponent } from './components/produttore-product-card/produttore-product-card.component';
import { ProduttoreOrderCardComponent } from './components/produttore-order-card/produttore-order-card.component';

@NgModule({
  declarations: [
    ProduttoreDashboardComponent,
    ProduttoreStatsOverviewComponent,
    ProduttoreQuickActionsComponent,
    ProdottiManagementComponent,
    OrdiniManagementComponent,
    CertificazioniManagementComponent,
    MetodiColtivazioneComponent,
    ProduttoreProductCardComponent,
    ProduttoreOrderCardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatCheckboxModule,
    MatSliderModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatBadgeModule,
    MatChipsModule,
    MatMenuModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatStepperModule,
    MatProgressBarModule,
    
    // Feature Routing
    ProduttoreRoutingModule
  ],
  providers: [
    // Services specifici del produttore verranno aggiunti qui
  ]
})
export class ProduttoreModule { }