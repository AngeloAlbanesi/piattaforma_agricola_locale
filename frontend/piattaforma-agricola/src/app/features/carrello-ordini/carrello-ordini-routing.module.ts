import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CarrelloOverviewComponent } from './pages/overview/carrello-overview.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';
import { OrdiniListComponent } from './pages/ordini-list/ordini-list.component';

const routes: Routes = [
    {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full'
    },
    {
        path: 'overview',
        component: CarrelloOverviewComponent,
    },
    {
        path: 'checkout',
        component: CheckoutComponent,
    },
    {
        path: 'ordini',
        component: OrdiniListComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CarrelloOrdiniRoutingModule { }
