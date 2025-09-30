import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProdottiListComponent } from './pages/prodotti-list/prodotti-list.component';

const routes: Routes = [
    {
        path: '',
        component: ProdottiListComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CatalogoRoutingModule { }
