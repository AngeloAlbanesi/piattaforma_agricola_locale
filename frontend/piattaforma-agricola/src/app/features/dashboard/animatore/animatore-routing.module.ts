import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../../../core/guards/auth.guard';
import { roleGuard } from '../../../core/guards/role.guard';
import { ROLES } from '../../../core/services/auth.service';
import { AnimatoreDashboardComponent } from './pages/animatore-dashboard/animatore-dashboard.component';

const routes: Routes = [
    {
        path: '',
        component: AnimatoreDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.ANIMATORE_FILIERA,
            title: 'Dashboard Animatore'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AnimatoreRoutingModule { }