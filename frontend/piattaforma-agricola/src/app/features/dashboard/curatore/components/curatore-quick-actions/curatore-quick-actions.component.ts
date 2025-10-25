import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AzioneRapidaCuratore } from '../../../../../core/models/curatore.models';

@Component({
    selector: 'app-curatore-quick-actions',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatTooltipModule
    ],
    templateUrl: './curatore-quick-actions.component.html',
    styleUrls: ['./curatore-quick-actions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CuratoreQuickActionsComponent {
    @Output() action = new EventEmitter<string>();

    quickActions: AzioneRapidaCuratore[] = [
        {
            id: 'view-approvals',
            label: 'Approvazioni in corso',
            icon: 'fact_check',
            color: 'primary',
            description: 'Gestisci le approvazioni in attesa',
            route: '/approvazioni',
            count: 0 // Sarà aggiornato dal componente padre
        },
        {
            id: 'view-history',
            label: 'Storico approvazioni',
            icon: 'history',
            color: 'accent',
            description: 'Visualizza lo storico delle approvazioni',
            route: '/approvazioni/storico'
        },
        {
            id: 'view-products',
            label: 'Prodotti',
            icon: 'inventory_2',
            color: 'primary',
            description: 'Visualizza tutti i prodotti',
            route: '/prodotti'
        },
        {
            id: 'view-companies',
            label: 'Aziende',
            icon: 'business',
            color: 'primary',
            description: 'Visualizza tutte le aziende',
            route: '/aziende'
        },
        {
            id: 'view-content',
            label: 'Contenuti',
            icon: 'article',
            color: 'warn',
            description: 'Visualizza tutti i contenuti',
            route: '/contenuti'
        },
        {
            id: 'edit-profile',
            label: 'Profilo Curatore',
            icon: 'person',
            color: 'primary',
            description: 'Gestisci il tuo profilo',
            route: '/profilo'
        }
    ];

    onActionClick(actionId: string | undefined): void {
        if (actionId) {
            this.action.emit(actionId);
        }
    }

    hasCount(action: AzioneRapidaCuratore): boolean {
        return action?.count !== undefined && action.count > 0;
    }

    getDescription(action: AzioneRapidaCuratore): string {
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