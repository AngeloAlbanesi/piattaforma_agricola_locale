package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteMateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione delle fonti di materia prima.
 */
@Repository
public interface IFonteMateriaPrimaRepository extends JpaRepository<FonteMateriaPrima, Long> {
}