import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-carrello-overview',
    templateUrl: './carrello-overview.component.html',
    styleUrls: ['./carrello-overview.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CarrelloOverviewComponent { }
