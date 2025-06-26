package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
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

}
