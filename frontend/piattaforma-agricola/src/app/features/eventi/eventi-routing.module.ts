import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListaEventiComponent } from './pages/lista-eventi/lista-eventi.component';

const routes: Routes = [
    {
        path: '',
        component: ListaEventiComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class EventiRoutingModule { }
