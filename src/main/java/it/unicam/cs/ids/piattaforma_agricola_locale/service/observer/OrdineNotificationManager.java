package it.unicam.cs.ids.piattaforma_agricola_locale.service.observer;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione di esempio del pattern Observer per la gestione
 * delle notifiche degli ordini ai venditori.
 * 
 * Questa classe gestisce una mappa di observer organizzati per venditore,
 * permettendo notifiche mirate e efficaci gestione dell'inventario.
 */
@Service
public class OrdineNotificationManager implements IOrdineObservable {

    private static final Logger logger = Logger.getLogger(OrdineNotificationManager.class.getName());

    /**
     * Mappa che associa ogni venditore alla lista dei suoi observer.
     * Utilizza strutture dati thread-safe per supportare applicazioni concorrenti.
     */
    private final Map<Venditore, List<IVenditoreObserver>> observersByVenditore;

    public OrdineNotificationManager() {
        this.observersByVenditore = new ConcurrentHashMap<>();
    }

    @Override
    public void aggiungiObserver(IVenditoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }

        // Se l'observer implementa anche l'interfaccia Venditore,
        // lo associamo al venditore corrispondente
        if (observer instanceof Venditore) {
            Venditore venditore = (Venditore) observer;
            observersByVenditore
                    .computeIfAbsent(venditore, k -> new CopyOnWriteArrayList<>())
                    .add(observer);

            logger.info("Observer aggiunto per il venditore: " +
                    venditore.getDatiAzienda());
        }
    }

    @Override
    public void rimuoviObserver(IVenditoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }

        if (observer instanceof Venditore) {
            Venditore venditore = (Venditore) observer;
            List<IVenditoreObserver> observers = observersByVenditore.get(venditore);

            if (observers != null) {
                observers.remove(observer);
                if (observers.isEmpty()) {
                    observersByVenditore.remove(venditore);
                }

                logger.info("Observer rimosso per il venditore: " +
                        venditore.getDatiAzienda());
            }
        }
    }

    @Override
    public void notificaObservers(Ordine ordine, Venditore venditoreSpecifico) {
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }

        if (venditoreSpecifico != null) {
            // Notifica solo il venditore specifico
            notificaVenditore(ordine, venditoreSpecifico);
        } else {
            // Notifica tutti i venditori che hanno prodotti nell'ordine
            notificaTuttiIVenditoriCoinvolti(ordine);
        }
    }

    /**
     * Notifica un venditore specifico con le righe d'ordine di sua competenza.
     */
    private void notificaVenditore(Ordine ordine, Venditore venditore) {
        List<IVenditoreObserver> observers = observersByVenditore.get(venditore);
        if (observers == null || observers.isEmpty()) {
            return;
        }

        // Filtra le righe d'ordine che contengono prodotti del venditore
        List<RigaOrdine> righeDiCompetenza = ordine.getRigheOrdine().stream()
                .filter(riga -> venditore.getProdottiOfferti().contains(riga.getAcquistabile()))
                .toList();

        if (!righeDiCompetenza.isEmpty()) {
            for (IVenditoreObserver observer : observers) {
                try {
                    observer.update(ordine, righeDiCompetenza);
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            "Errore durante la notifica dell'observer: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Notifica tutti i venditori che hanno prodotti coinvolti nell'ordine.
     */
    private void notificaTuttiIVenditoriCoinvolti(Ordine ordine) {
        for (Venditore venditore : observersByVenditore.keySet()) {
            notificaVenditore(ordine, venditore);
        }
    }

    /**
     * Restituisce il numero di observer registrati per un determinato venditore.
     * Utile per debugging e monitoraggio.
     */
    public int getNumeroObserverPerVenditore(Venditore venditore) {
        List<IVenditoreObserver> observers = observersByVenditore.get(venditore);
        return observers != null ? observers.size() : 0;
    }

    /**
     * Restituisce il numero totale di venditori con observer registrati.
     */
    public int getNumeroVenditoriConObserver() {
        return observersByVenditore.size();
    }
}