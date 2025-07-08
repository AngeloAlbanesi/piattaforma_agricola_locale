package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.CoordinateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.DistanzaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IVenditoreService {

    /**
     * Trova un venditore per ID.
     * 
     * @param id ID del venditore
     * @return Il venditore trovato, se esiste
     */
    Optional<Venditore> getVenditoreById(Long id);

    void aggiornaDatiAzienda(Venditore venditore,  DatiAzienda datiAggiornati);


    DatiAzienda aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva, String indirizzoAzienda,
                                    String descrizioneAzienda, String logoUrl, String sitoWebUrl);

    Certificazione aggiungiCertificazioneAdAzienda(Venditore venditore, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza);

    void stampaCertificazioniAzienda(Venditore venditore);

    List<Certificazione> getCertificazioniAzienda(Venditore venditore);

    boolean rimuoviCertificazioneDaAzienda(Venditore venditore, Long idCertificazione);
    
    /**
     * Ottiene tutte le aziende.
     * 
     * @param pageable Parametri di paginazione
     * @return Una pagina di tutte le aziende
     */
    Page<DatiAzienda> getAllAziende(Pageable pageable);
    
    /**
     * Cerca aziende in base a una query di ricerca.
     * 
     * @param query La query di ricerca
     * @param pageable Parametri di paginazione
     * @return Una pagina di risultati
     */
    Page<DatiAzienda> searchAziende(String query, Pageable pageable);
    
    /**
     * Aggiorna un venditore.
     * 
     * @param venditore Il venditore da aggiornare
     * @return Il venditore aggiornato
     */
    Venditore updateVenditore(Venditore venditore);

    Optional<CoordinateDTO> getCoordinatePerAziendaId(Long id);

    Optional<DistanzaDTO> calcolaDistanzaDaAzienda(Long id, String partenza);
}
