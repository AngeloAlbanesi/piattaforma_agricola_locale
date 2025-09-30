import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CarrelloOverviewComponent } from './pages/overview/carrello-overview.component';

const routes: Routes = [
    {
        path: '',
        component: CarrelloOverviewComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CarrelloOrdiniRoutingModule { }
