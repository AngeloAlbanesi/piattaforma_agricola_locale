package it.unicam.cs.ids.piattaforma_agricola_locale.service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.TipoAcquistabile;

public interface AcquistabileService {
    
    /**
     * Recupera un oggetto Acquistabile per tipo e ID
     * 
     * @param tipo Il tipo di acquistabile
     * @param id L'ID dell'oggetto
     * @return L'oggetto Acquistabile o null se non trovato
     */
    Acquistabile findByTipoAndId(TipoAcquistabile tipo, Long id);
    
    /**
     * Recupera un oggetto Acquistabile per tipo stringa e ID
     * 
     * @param tipoString Il tipo come stringa
     * @param id L'ID dell'oggetto
     * @return L'oggetto Acquistabile o null se non trovato
     */
    Acquistabile findByTipoStringAndId(String tipoString, Long id);
}