package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;


import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

import java.util.List;
import java.util.Optional;

public interface ICertificazioneRepository {

        void salva(Certificazione certificazione);

        Optional<Certificazione> trovaPerId(int id);

        List<Certificazione> trovaTutte();

        void rimuovi(int id);

        void rimuovi(Certificazione certificazione);
}

