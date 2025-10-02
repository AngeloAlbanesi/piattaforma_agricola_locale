import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { Router } from '@angular/router';

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
    standalone: false,
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
}
