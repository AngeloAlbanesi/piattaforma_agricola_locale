import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const expectedRole = route.data?.['expectedRole'];

    if (!expectedRole) {
        // Se non è specificato un ruolo, permetti l'accesso
        return true;
    }

    if (!authService.isAuthenticated()) {
        router.navigate(['/auth/login']);
        return false;
    }

    if (authService.hasRole(expectedRole)) {
        return true;
    }

    // Se l'utente non ha il ruolo richiesto, reindirizza alla dashboard appropriata
    const userRole = authService.getRole();
    if (userRole) {
        switch (userRole) {
            case 'PRODUTTORE':
                router.navigate(['/dashboard/produttore']);
                break;
            case 'TRASFORMATORE':
                router.navigate(['/dashboard/trasformatore']);
                break;
            case 'DISTRIBUTORE_DI_TIPICITA':
                router.navigate(['/dashboard/distributore']);
                break;
            case 'CURATORE':
                router.navigate(['/dashboard/curatore']);
                break;
            case 'ANIMATORE_DELLA_FILIERA':
                router.navigate(['/dashboard/animatore']);
                break;
            case 'ACQUIRENTE':
                router.navigate(['/dashboard/acquirente']);
                break;
            case 'GESTORE_PIATTAFORMA':
                router.navigate(['/dashboard/admin']);
                break;
            default:
                router.navigate(['/']);
        }
    } else {
        router.navigate(['/']);
    }

    return false;
};