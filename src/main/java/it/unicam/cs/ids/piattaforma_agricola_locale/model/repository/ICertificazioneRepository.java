package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;




import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import java.util.List;

public interface ICertificazioneRepository {
        void save(Certificazione certificazione);
        Certificazione findById(Long id);
        List<Certificazione> findAll();
        List<Certificazione> findByProdottoId(Long idProdotto);
        List<Certificazione> findByAziendaId(Long idAzienda); // Assumendo che DatiAzienda abbia un ID
        void deleteById(Long id);
        void deleteByProdottoId(Long idProdotto); // Utile se un prodotto viene rimosso
        void deleteByAziendaId(Long idAzienda);   // Utile se un'azienda viene rimossa
}