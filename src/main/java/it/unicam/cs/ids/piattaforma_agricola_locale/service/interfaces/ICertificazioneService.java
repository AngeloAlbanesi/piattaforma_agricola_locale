package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;



import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

import java.util.Date;

public interface ICertificazioneService {

    Certificazione creaCertificazione(String nome, String enteRilascio, Date dataRilascio, Date dataScadenza);

    Certificazione trovaCertificazione(int id);

    void rimuoviCertificazione(int id);
}

