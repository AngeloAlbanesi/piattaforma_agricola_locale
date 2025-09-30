import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-catalogo-prodotti-list',
    templateUrl: './prodotti-list.component.html',
    styleUrls: ['./prodotti-list.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProdottiListComponent { }
