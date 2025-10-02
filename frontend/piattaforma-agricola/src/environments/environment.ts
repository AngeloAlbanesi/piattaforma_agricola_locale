export const environment = {
    production: false,
    environmentName: 'development',
    // In sviluppo usiamo il proxy dev server per evitare problemi CORS.
    // Lasciare vuoto permette di usare il proxy.conf.json che mappa '/api/**' a http://localhost:8080
    apiBaseUrl: '',
    apiPrefix: '/api',
    hmr: false,
};
