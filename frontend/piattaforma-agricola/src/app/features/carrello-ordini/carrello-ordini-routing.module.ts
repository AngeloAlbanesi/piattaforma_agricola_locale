import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CarrelloOverviewComponent } from './pages/overview/carrello-overview.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';

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
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class CarrelloOrdiniRoutingModule { }
