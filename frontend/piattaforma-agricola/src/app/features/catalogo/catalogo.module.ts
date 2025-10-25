import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CatalogoRoutingModule } from './catalogo-routing.module';

/**
 * Modulo per il catalogo pubblico di prodotti e pacchetti
 * 
 * Nota: Tutti i componenti del catalogo sono standalone e vengono
 * importati direttamente nel routing module
 */
@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        CatalogoRoutingModule
    ]
})
export class CatalogoModule { }
