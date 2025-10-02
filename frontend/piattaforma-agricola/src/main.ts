import { platformBrowser } from '@angular/platform-browser';
import { AppModule } from './app/app-module';

// Fix per viewport mobile: impostiamo una variabile CSS --vh che rappresenta 1% dell'altezza visibile
function setVhCssVar() {
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
}

// Impostazione iniziale e aggiornamento su resize/orientationchange
setVhCssVar();
window.addEventListener('resize', setVhCssVar);
window.addEventListener('orientationchange', setVhCssVar);

platformBrowser().bootstrapModule(AppModule, {
    ngZoneEventCoalescing: true,
})
    .catch(err => console.error(err));
