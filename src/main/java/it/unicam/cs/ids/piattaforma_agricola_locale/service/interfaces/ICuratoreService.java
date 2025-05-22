package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;

public interface ICuratoreService {

    /**
     * Recupera tutti i DatiAzienda in attesa di revisione.
     * 
     * @return Lista di DatiAzienda.
     */
    List<DatiAzienda> getDatiAziendaInAttesaRevisione();

    /**
     * Approva i dati di un'azienda.
     * 
     * @param idDatiAzienda L'ID dei dati azienda da approvare.
     * @param idCuratore    L'ID del curatore che esegue l'azione.
     * @param feedback      Eventuale feedback per il venditore.
     * @return true se l'operazione ha avuto successo.
     * @throws DatiAziendaNotFoundException se i dati azienda non vengono trovati.
     * @throws IllegalStateException        se i dati azienda non sono in uno stato
     *                                      valido per l'approvazione.
     */
    boolean approvaDatiAzienda(String idDatiAzienda, String idCuratore, String feedback);

    /**
     * Respinge i dati di un'azienda.
     * 
     * @param idDatiAzienda L'ID dei dati azienda da respingere.
     * @param idCuratore    L'ID del curatore che esegue l'azione.
     * @param feedback      Motivazione del respingimento.
     * @return true se l'operazione ha avuto successo.
     */
    boolean respingiDatiAzienda(String idDatiAzienda, String idCuratore, String feedback);

    /**
     * Recupera tutti i Prodotti in attesa di revisione.
     * 
     * @return Lista di Prodotti (o DTO rappresentativi).
     */
    List<Prodotto> getProdottiInAttesaRevisione();

    /**
     * Approva un prodotto.
     * 
     * @param idProdotto L'ID del prodotto da approvare.
     * @param idCuratore L'ID del curatore che esegue l'azione.
     * @param feedback   Eventuale feedback.
     * @return true se l'operazione ha avuto successo.
     */
    boolean approvaProdotto(String idProdotto, String idCuratore, String feedback);

    /**
     * Respinge un prodotto.
     * 
     * @param idProdotto L'ID del prodotto da respingere.
     * @param idCuratore L'ID del curatore che esegue l'azione.
     * @param feedback   Motivazione del respingimento.
     * @return true se l'operazione ha avuto successo.
     */
    boolean respingiProdotto(String idProdotto, String idCuratore, String feedback);

    /**
     * Verifica la conformità locale degli ingredienti di un prodotto trasformato.
     * (Questa potrebbe essere un'azione più complessa che aggiorna un flag
     * sull'ingrediente o sul prodotto)
     * 
     * @param idProdottoTrasformato L'ID del prodotto trasformato.
     * @param idCuratore            L'ID del curatore.
     * @return true se tutti gli ingredienti sono stati verificati come locali (o
     *         stato aggiornato).
     */
    boolean verificaConformitaIngredientiLocali(String idProdottoTrasformato, String idCuratore);

}
