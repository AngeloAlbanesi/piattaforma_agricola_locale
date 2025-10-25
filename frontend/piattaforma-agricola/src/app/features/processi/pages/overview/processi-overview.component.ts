import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-processi-overview',
    templateUrl: './processi-overview.component.html',
    styleUrls: ['./processi-overview.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcessiOverviewComponent { }
