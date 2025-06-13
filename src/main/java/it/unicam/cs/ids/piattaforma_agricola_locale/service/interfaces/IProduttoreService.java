package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;

public interface IProduttoreService extends IVenditoreService{

    /**
     * Crea un nuovo metodo di coltivazione per un prodotto del produttore.
     * 
     * @param idProduttore L'ID del produttore
     * @param idProdotto L'ID del prodotto
     * @param metodoDiColtivazione Il metodo di coltivazione da creare
     * @return Il metodo di coltivazione creato
     * @throws IllegalArgumentException se il produttore non possiede il prodotto o il prodotto non è coltivato
     */
    MetodoDiColtivazione creaMetodoDiColtivazione(long idProduttore, int idProdotto, MetodoDiColtivazione metodoDiColtivazione);

    /**
     * Aggiorna un metodo di coltivazione esistente.
     * 
     * @param idProduttore L'ID del produttore
     * @param idProdotto L'ID del prodotto
     * @param metodoDiColtivazione Il metodo di coltivazione aggiornato
     * @return Il metodo di coltivazione aggiornato
     * @throws IllegalArgumentException se il produttore non possiede il prodotto
     */
    MetodoDiColtivazione aggiornaMetodoDiColtivazione(long idProduttore, int idProdotto, MetodoDiColtivazione metodoDiColtivazione);

    /**
     * Elimina il metodo di coltivazione associato a un prodotto.
     * 
     * @param idProduttore L'ID del produttore
     * @param idProdotto L'ID del prodotto
     * @throws IllegalArgumentException se il produttore non possiede il prodotto
     */
    void eliminaMetodoDiColtivazione(long idProduttore, int idProdotto);

    /**
     * Recupera il metodo di coltivazione associato a un prodotto.
     * 
     * @param idProdotto L'ID del prodotto
     * @return Il metodo di coltivazione associato, null se non presente
     */
    MetodoDiColtivazione getMetodoDiColtivazioneByProdotto(int idProdotto);

}
