package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteMateriaPrima;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteInterna;
import lombok.Value;

/**
 * DTO che rappresenta una fase di lavorazione all'interno di un processo.
 * Contiene i dettagli della fase e l'oggetto fonte completo per preservare
 * tutte le informazioni sulla provenienza della materia prima, distinguendo
 * tra fonti interne (produttori della piattaforma) ed esterne.
 */
@Value
public class FaseLavorazioneDTO {

    long id;
    String nome;
    String descrizione;
    int ordineEsecuzione;
    String materiaPrimaUtilizzata;
    FonteMateriaPrima fonteMateriaPrima; // Oggetto fonte completo

    /**
     * Restituisce la descrizione testuale della fonte per compatibilità.
     *
     * @return Descrizione della fonte
     */
    public String getDescrizioneFonte() {
        return fonteMateriaPrima.getDescrizione();
    }

    /**
     * Verifica se la fonte è interna alla piattaforma.
     *
     * @return true se la fonte è interna, false se esterna
     */
    public boolean isFonteInterna() {
        return fonteMateriaPrima instanceof FonteInterna;
    }

    /**
     * Restituisce l'ID del produttore se la fonte è interna.
     *
     * @return ID del produttore o null se fonte esterna
     */
    public Long getIdProduttoreInterno() {
        if (fonteMateriaPrima instanceof FonteInterna) {
            FonteInterna fonteInterna = (FonteInterna) fonteMateriaPrima;
            return fonteInterna.getProduttore().getIdUtente();
        }
        return null;
    }

    /**
     * Restituisce il tipo di fonte come stringa.
     *
     * @return "INTERNA" o "ESTERNA"
     */
    public String getTipoFonte() {
        return isFonteInterna() ? "INTERNA" : "ESTERNA";
    }
}