import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '../../../core/guards/auth.guard';
import { roleGuard } from '../../../core/guards/role.guard';
import { ROLES } from '../../../core/services/auth.service';
import { AnimatoreDashboardComponent } from './pages/animatore-dashboard/animatore-dashboard.component';
import { EventParticipantsComponent } from './pages/event-participants/event-participants.component';

const routes: Routes = [
    {
        path: '',
        component: AnimatoreDashboardComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.ANIMATORE_FILIERA,
            title: 'Dashboard Animatore'
        }
    },
    {
        path: 'eventi/:id/partecipanti',
        component: EventParticipantsComponent,
        canActivate: [authGuard, roleGuard],
        data: {
            expectedRole: ROLES.ANIMATORE_FILIERA,
            title: 'Partecipanti Evento'
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AnimatoreRoutingModule { }