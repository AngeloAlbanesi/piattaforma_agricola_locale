import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ShellComponent } from './layout/shell/shell.component';

@NgModule({
    declarations: [ShellComponent],
    imports: [CommonModule, RouterModule, MatToolbarModule, MatSidenavModule, MatListModule, MatIconModule, MatSnackBarModule, MatButtonModule],
    exports: [ShellComponent],
    providers: [
        {
            provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
            useValue: { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top' },
        },
    ],
})
export class CoreModule { }
