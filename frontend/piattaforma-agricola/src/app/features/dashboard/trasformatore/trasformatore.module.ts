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
import { MatTimelineModule } from '@angular/material/timeline';

// Feature Modules
import { TrasformatoreRoutingModule } from './trasformatore-routing.module';
import { TrasformatoreDashboardComponent } from './pages/trasformatore-dashboard/trasformatore-dashboard.component';

// Components
import { TrasformatoreStatsOverviewComponent } from './components/trasformatore-stats-overview/trasformatore-stats-overview.component';
import { TrasformatoreQuickActionsComponent } from './components/trasformatore-quick-actions/trasformatore-quick-actions.component';
import { ProcessiManagementComponent } from './components/processi-management/processi-management.component';
import { FasiLavorazioneComponent } from './components/fasi-lavorazione/fasi-lavorazione.component';
import { TracciabilitaComponent } from './components/tracciabilita/tracciabilita.component';
import { CertificazioniTrasformatoreComponent } from './components/certificazioni-trasformatore/certificazioni-trasformatore.component';

@NgModule({
  declarations: [
    TrasformatoreDashboardComponent,
    TrasformatoreStatsOverviewComponent,
    TrasformatoreQuickActionsComponent,
    ProcessiManagementComponent,
    FasiLavorazioneComponent,
    TracciabilitaComponent,
    CertificazioniTrasformatoreComponent
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
    MatTimelineModule,
    
    // Feature Routing
    TrasformatoreRoutingModule
  ],
  providers: [
    // Services specifici del trasformatore verranno aggiunti qui
  ]
})
export class TrasformatoreModule { }