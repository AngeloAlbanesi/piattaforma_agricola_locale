import { Component, signal } from '@angular/core';
import { ShellComponent } from './core/layout/shell/shell.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: true,
  imports: [ShellComponent],
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('piattaforma-agricola');
}
