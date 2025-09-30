import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { CatalogoRoutingModule } from './catalogo-routing.module';
import { ProdottiListComponent } from './pages/prodotti-list/prodotti-list.component';

@NgModule({
    declarations: [ProdottiListComponent],
    imports: [
        CommonModule,
        CatalogoRoutingModule,
        MatCardModule,
        MatToolbarModule,
        MatIconModule,
        MatProgressSpinnerModule,
    ],
})
export class CatalogoModule { }
