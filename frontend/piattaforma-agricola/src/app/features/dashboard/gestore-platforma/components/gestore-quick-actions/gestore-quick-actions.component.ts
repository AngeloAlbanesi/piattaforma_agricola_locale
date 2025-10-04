import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AzioneRapidaGestore } from '../../../../../core/models/gestore-platforma.models';

@Component({
    selector: 'app-gestore-quick-actions',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatTooltipModule
    ],
    templateUrl: './gestore-quick-actions.component.html',
    styleUrls: ['./gestore-quick-actions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class GestoreQuickActionsComponent {
    @Output() action = new EventEmitter<string>();

    quickActions: AzioneRapidaGestore[] = [
        {
            id: 'view-users',
            label: 'Gestisci Utenti',
            icon: 'people',
            color: 'primary',
            description: 'Visualizza e gestisci tutti gli utenti della piattaforma',
            route: '/dashboard/admin/utenti'
        },
        {
            id: 'view-products',
            label: 'Gestisci Prodotti',
            icon: 'inventory_2',
            color: 'accent',
            description: 'Visualizza e gestisci tutti i prodotti',
            route: '/dashboard/admin/prodotti'
        },
        {
            id: 'view-companies',
            label: 'Gestisci Aziende',
            icon: 'business',
            color: 'warn',
            description: 'Visualizza e gestisci tutte le aziende registrate',
            route: '/dashboard/admin/aziende'
        },
        {
            id: 'view-events',
            label: 'Gestisci Eventi',
            icon: 'event',
            color: 'primary',
            description: 'Visualizza e gestisci tutti gli eventi',
            route: '/dashboard/admin/eventi'
        },
        {
            id: 'view-reports',
            label: 'Visualizza Report',
            icon: 'analytics',
            color: 'accent',
            description: 'Accedi ai report e alle statistiche della piattaforma',
            route: '/dashboard/admin/report'
        },
        {
            id: 'view-settings',
            label: 'Impostazioni Piattaforma',
            icon: 'settings',
            color: 'warn',
            description: 'Configura le impostazioni generali della piattaforma',
            route: '/dashboard/admin/impostazioni'
        }
    ];

    onActionClick(actionId: string | undefined): void {
        if (actionId) {
            this.action.emit(actionId);
        }
    }

    getDescription(action: AzioneRapidaGestore): string {
        return action?.description || '';
    }

    getActionIconColor(color: 'primary' | 'accent' | 'warn' | undefined): string {
        switch (color) {
            case 'primary':
                return '#3498db';
            case 'accent':
                return '#2980b9';
            case 'warn':
                return '#e67e22';
            default:
                return '#3498db';
        }
    }

    getActionBgColor(color: 'primary' | 'accent' | 'warn' | undefined): string {
        switch (color) {
            case 'primary':
                return 'rgba(52, 152, 219, 0.1)';
            case 'accent':
                return 'rgba(41, 128, 185, 0.1)';
            case 'warn':
                return 'rgba(230, 126, 34, 0.1)';
            default:
                return 'rgba(52, 152, 219, 0.1)';
        }
    }
}