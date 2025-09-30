import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-eventi-lista',
    templateUrl: './lista-eventi.component.html',
    styleUrls: ['./lista-eventi.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaEventiComponent { }
