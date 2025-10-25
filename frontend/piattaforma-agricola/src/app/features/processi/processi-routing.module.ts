import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProcessiOverviewComponent } from './pages/overview/processi-overview.component';

const routes: Routes = [
    {
        path: '',
        component: ProcessiOverviewComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ProcessiRoutingModule { }
