import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CatalogViewComponent } from './pages/catalog-view/catalog-view.component';

const routes: Routes = [
    {
        path: '',
        component: CatalogViewComponent,
        title: 'Catalogo Prodotti e Pacchetti - Piattaforma Agricola Locale'
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CatalogoRoutingModule { }
