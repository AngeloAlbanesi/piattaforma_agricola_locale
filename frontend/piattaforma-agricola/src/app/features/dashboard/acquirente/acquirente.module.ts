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

// Feature Modules
import { AcquirenteRoutingModule } from './acquirente-routing.module';
import { AcquirenteDashboardComponent } from './pages/acquirente-dashboard/acquirente-dashboard.component';

// Components
import { StatsOverviewComponent } from './components/stats-overview/stats-overview.component';
import { ProductCatalogComponent } from './components/product-catalog/product-catalog.component';
import { CartSummaryComponent } from './components/cart-summary/cart-summary.component';
import { RecentOrdersComponent } from './components/recent-orders/recent-orders.component';
import { UpcomingEventsComponent } from './components/upcoming-events/upcoming-events.component';
import { QuickActionsComponent } from './components/quick-actions/quick-actions.component';
import { ProductFiltersComponent } from './components/product-filters/product-filters.component';
import { ProductCardComponent } from './components/product-card/product-card.component';
import { CartItemComponent } from './components/cart-item/cart-item.component';
import { OrderCardComponent } from './components/order-card/order-card.component';
import { EventCardComponent } from './components/event-card/event-card.component';

@NgModule({
  declarations: [
    AcquirenteDashboardComponent,
    StatsOverviewComponent,
    ProductCatalogComponent,
    CartSummaryComponent,
    RecentOrdersComponent,
    UpcomingEventsComponent,
    QuickActionsComponent,
    ProductFiltersComponent,
    ProductCardComponent,
    CartItemComponent,
    OrderCardComponent,
    EventCardComponent
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
    
    // Feature Routing
    AcquirenteRoutingModule
  ],
  providers: [
    // Services specifici dell'acquirente verranno aggiunti qui
  ]
})
export class AcquirenteModule { }