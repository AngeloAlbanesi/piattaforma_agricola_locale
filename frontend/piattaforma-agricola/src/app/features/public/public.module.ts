import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { ReactiveFormsModule } from '@angular/forms';

// Modulo di routing
import { PublicRoutingModule } from './public-routing.module';

// Servizi pubblici
import { PublicProdottiService } from '../../core/services/public-prodotti.service';
import { PublicPacchettiService } from '../../core/services/public-pacchetti.service';
import { PublicEventiService } from '../../core/services/public-eventi.service';
import { PublicAziendeService } from '../../core/services/public-aziende.service';
import { PublicProcessiService } from '../../core/services/public-processi.service';

@NgModule({
  declarations: [
    // I componenti verranno aggiunti qui quando verranno creati
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    
    // Angular Material
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    MatBadgeModule,
    MatTabsModule,
    MatExpansionModule,
    MatSlideToggleModule,
    MatSliderModule,
    
    // Routing
    PublicRoutingModule
  ],
  providers: [
    // Servizi pubblici
    PublicProdottiService,
    PublicPacchettiService,
    PublicEventiService,
    PublicAziendeService,
    PublicProcessiService
  ]
})
export class PublicModule {}