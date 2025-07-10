package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProdottoRepository extends JpaRepository<Prodotto, Long> {

    // Metodi ereditati da JpaRepository: save, findById, findAll, deleteById

    // Metodi personalizzati usando Spring Data JPA naming conventions
    List<Prodotto> findByVenditore(Venditore venditore);
    
    /**
     * Trova prodotti di un venditore specifico con paginazione.
     * 
     * @param venditore Il venditore
     * @param pageable Parametri di paginazione
     * @return Una pagina di prodotti
     */
    Page<Prodotto> findByVenditore(Venditore venditore, Pageable pageable);

    /**
     * Verifica se esistono prodotti associati a un processo di trasformazione
     * specifico.
     * 
     * @param processoId L'ID del processo di trasformazione
     * @return true se esistono prodotti che fanno riferimento al processo, false
     *         altrimenti
     */
    @Query("SELECT COUNT(p) > 0 FROM Prodotto p WHERE p.idProcessoTrasformazioneOriginario = :processoId")
    boolean existsByProcessoId(@Param("processoId") Long processoId);

    // Questo metodo è stato sostituito da findAll() di JpaRepository

    // Metodo standard di Spring Data JPA
    List<Prodotto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Trova prodotti per stato di verifica con paginazione.
     * 
     * @param statoVerifica Lo stato di verifica (IN_REVISIONE, APPROVATO, RESPINTO)
     * @param pageable Parametri di paginazione
     * @return Una pagina di prodotti filtrati per stato
     */
    Page<Prodotto> findByStatoVerifica(StatoVerificaValori statoVerifica, Pageable pageable);

    /**
     * Trova prodotti di un venditore specifico con stato di verifica specifico.
     * 
     * @param venditore Il venditore
     * @param statoVerifica Lo stato di verifica
     * @param pageable Parametri di paginazione
     * @return Una pagina di prodotti filtrati per venditore e stato
     */
    Page<Prodotto> findByVenditoreAndStatoVerifica(Venditore venditore, StatoVerificaValori statoVerifica, Pageable pageable);

}
