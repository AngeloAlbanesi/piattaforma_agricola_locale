import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';

// Fix per viewport mobile: impostiamo una variabile CSS --vh che rappresenta 1% dell'altezza visibile
function setVhCssVar() {
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
}

// Impostazione iniziale e aggiornamento su resize/orientationchange
setVhCssVar();
window.addEventListener('resize', setVhCssVar);
window.addEventListener('orientationchange', setVhCssVar);

bootstrapApplication(App, appConfig)
    .catch(err => console.error(err));
