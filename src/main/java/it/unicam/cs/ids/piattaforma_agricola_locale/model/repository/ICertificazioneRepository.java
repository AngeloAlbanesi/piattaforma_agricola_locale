package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;




import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import java.util.List;

public interface ICertificazioneRepository {
        void save(Certificazione certificazione);
        Certificazione findById(int id);
        List<Certificazione> findAll();
        List<Certificazione> findByProdottoId(int idProdotto);
        List<Certificazione> findByAziendaId(int idAzienda); // Assumendo che DatiAzienda abbia un ID
        void deleteById(int id);
        void deleteByProdottoId(int idProdotto); // Utile se un prodotto viene rimosso
        void deleteByAziendaId(int idAzienda);   // Utile se un'azienda viene rimossa
}