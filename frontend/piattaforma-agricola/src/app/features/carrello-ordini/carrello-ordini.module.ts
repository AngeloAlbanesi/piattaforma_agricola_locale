import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatStepperModule } from '@angular/material/stepper';
import { CarrelloOrdiniRoutingModule } from './carrello-ordini-routing.module';
import { CarrelloOverviewComponent } from './pages/overview/carrello-overview.component';

@NgModule({
    declarations: [CarrelloOverviewComponent],
    imports: [
        CommonModule,
        CarrelloOrdiniRoutingModule,
        MatListModule,
        MatDividerModule,
        MatStepperModule,
        MatButtonModule,
    ],
})
export class CarrelloOrdiniModule { }
