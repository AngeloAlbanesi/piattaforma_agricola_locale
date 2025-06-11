package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteMateriaPrima;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteInterna;

/**
 * DTO che rappresenta una fase di lavorazione all'interno di un processo.
 * Contiene i dettagli della fase e l'oggetto fonte completo per preservare
 * tutte le informazioni sulla provenienza della materia prima, distinguendo
 * tra fonti interne (produttori della piattaforma) ed esterne.
 */
public class FaseLavorazioneDTO {

    private final long id;
    private final String nome;
    private final String descrizione;
    private final int ordineEsecuzione;
    private final String materiaPrimaUtilizzata;
    private final FonteMateriaPrima fonteMateriaPrima; // Oggetto fonte completo

    /**
     * Costruttore per FaseLavorazioneDTO.
     *
     * @param id                     ID della fase di lavorazione
     * @param nome                   Nome della fase
     * @param descrizione            Descrizione dettagliata della fase
     * @param ordineEsecuzione       Ordine di esecuzione nel processo
     * @param materiaPrimaUtilizzata Descrizione della materia prima utilizzata
     * @param fonteMateriaPrima      Oggetto fonte (FonteInterna o FonteEsterna)
     */
    public FaseLavorazioneDTO(long id, String nome, String descrizione, int ordineEsecuzione,
                              String materiaPrimaUtilizzata, FonteMateriaPrima fonteMateriaPrima) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.ordineEsecuzione = ordineEsecuzione;
        this.materiaPrimaUtilizzata = materiaPrimaUtilizzata;
        this.fonteMateriaPrima = fonteMateriaPrima;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getOrdineEsecuzione() {
        return ordineEsecuzione;
    }

    public String getMateriaPrimaUtilizzata() {
        return materiaPrimaUtilizzata;
    }

    public FonteMateriaPrima getFonteMateriaPrima() {
        return fonteMateriaPrima;
    }

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