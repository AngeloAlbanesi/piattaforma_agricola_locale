import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ProcessiRoutingModule } from './processi-routing.module';
import { ProcessiOverviewComponent } from './pages/overview/processi-overview.component';

@NgModule({
    declarations: [ProcessiOverviewComponent],
    imports: [
        CommonModule,
        ProcessiRoutingModule,
        MatExpansionModule,
        MatListModule,
        MatProgressBarModule,
    ],
})
export class ProcessiModule { }
