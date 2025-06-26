package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

@Repository
public interface ICertificazioneRepository extends JpaRepository<Certificazione, Long> {
        List<Certificazione> findByIdProdottoAssociato(Long idProdotto);
        List<Certificazione> findByIdAziendaAssociata(Long idAzienda); // Assumendo che DatiAzienda abbia un ID
        void deleteByIdProdottoAssociato(Long idProdotto); // Utile se un prodotto viene rimosso
        void deleteByIdAziendaAssociata(Long idAzienda);   // Utile se un'azienda viene rimossa
}