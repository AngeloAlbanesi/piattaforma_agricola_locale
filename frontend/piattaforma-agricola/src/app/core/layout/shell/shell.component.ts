import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { Router, RouterModule, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';

interface NavItem {
    label: string;
    icon: string;
    route: string;
    exact?: boolean;
}

@Component({
    selector: 'app-shell',
    templateUrl: './shell.component.html',
    styleUrls: ['./shell.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatToolbarModule,
        MatSidenavModule,
        MatListModule,
        MatIconModule,
        MatSnackBarModule,
        MatButtonModule,
        MatChipsModule,
        RouterOutlet,
        RouterLink,
        RouterLinkActive
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ShellComponent {
    readonly appName = signal('Piattaforma Filiera Locale');

    readonly navItems: NavItem[] = [
        { label: 'Catalogo', icon: 'storefront', route: '/catalogo', exact: true },
        { label: 'Carrello & Ordini', icon: 'shopping_cart', route: '/carrello' },
        { label: 'Eventi', icon: 'event', route: '/eventi' },
        { label: 'Processi', icon: 'sync_alt', route: '/processi' },
        { label: 'Gestione Utenti', icon: 'admin_panel_settings', route: '/gestione-utenti' },
    ];

    constructor(private router: Router) { }

    /**
     * Verifica se l'utente è sulla landing page principale
     */
    isLandingPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl === '/' || currentUrl === '';
    }

    /**
     * Verifica se l'utente è su una pagina di autenticazione
     */
    isAuthPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl.startsWith('/auth');
    }

    /**
     * Verifica se l'utente è su una dashboard
     */
    isDashboardPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl.startsWith('/dashboard');
    }

    /**
     * Verifica se l'utente è sulla pagina del catalogo
     */
    isCatalogoPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl.startsWith('/catalogo');
    }

    /**
     * Verifica se l'utente è su una pagina di dettaglio pubblica
     */
    isPublicDetailPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl.match(/^\/(prodotti|pacchetti|eventi|aziende|processi)\/\d+/) !== null;
    }

    /**
     * Verifica se l'utente è sulla pagina eventi pubblica
     */
    isEventiPage(): boolean {
        const currentUrl = this.router.url;
        return currentUrl === '/eventi' || currentUrl.startsWith('/eventi?');
    }
}
