package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;

@Repository
public interface IPacchettoRepository extends JpaRepository<Pacchetto, Long> {

    List<Pacchetto> findByNomeContainingIgnoreCase(String nome);

    List<Pacchetto> findByDistributore(DistributoreDiTipicita distributore);

}
