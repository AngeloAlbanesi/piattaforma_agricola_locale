import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'app-auth-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: false,
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent { }
