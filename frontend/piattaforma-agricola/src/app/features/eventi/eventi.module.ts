import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { EventiRoutingModule } from './eventi-routing.module';
import { ListaEventiComponent } from './pages/lista-eventi/lista-eventi.component';

@NgModule({
    declarations: [ListaEventiComponent],
    imports: [
        CommonModule,
        EventiRoutingModule,
        MatCardModule,
        MatTableModule,
        MatChipsModule,
        MatIconModule,
    ],
})
export class EventiModule { }
