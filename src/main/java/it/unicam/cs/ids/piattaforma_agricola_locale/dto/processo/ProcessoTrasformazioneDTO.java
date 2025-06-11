package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO che rappresenta le informazioni di un processo di trasformazione.
 * Contiene i dati del processo e una lista di fasi di lavorazione semplificate.
 */
public class ProcessoTrasformazioneDTO {

    private final Long idProcesso;
    private final String nomeProcesso;
    private final String descrizioneProcesso;
    private final String noteTecniche;
    private final Long idTrasformatore;
    private final String nomeTrasformatore;
    private final String cognomeTrasformatore;
    private final String aziendaTrasformatore;
    private final List<FaseLavorazioneDTO> fasi;

    public ProcessoTrasformazioneDTO(Long idProcesso, String nomeProcesso, String descrizioneProcesso,
                                     LocalDateTime dataInizioProcesso, LocalDateTime dataFineProcesso,
                                     String noteTecniche, Long idTrasformatore, String nomeTrasformatore,
                                     String cognomeTrasformatore, String aziendaTrasformatore,
                                     List<FaseLavorazioneDTO> fasi) {
        this.idProcesso = idProcesso;
        this.nomeProcesso = nomeProcesso;
        this.descrizioneProcesso = descrizioneProcesso;
        this.noteTecniche = noteTecniche;
        this.idTrasformatore = idTrasformatore;
        this.nomeTrasformatore = nomeTrasformatore;
        this.cognomeTrasformatore = cognomeTrasformatore;
        this.aziendaTrasformatore = aziendaTrasformatore;
        this.fasi = fasi;
    }

    // Getters
    public long getIdProcesso() {
        return idProcesso;
    }

    public String getNomeProcesso() {
        return nomeProcesso;
    }

    public String getDescrizioneProcesso() {
        return descrizioneProcesso;
    }

    public String getNoteTecniche() {
        return noteTecniche;
    }

    public Long getIdTrasformatore() {
        return idTrasformatore;
    }

    public String getNomeTrasformatore() {
        return nomeTrasformatore;
    }

    public String getCognomeTrasformatore() {
        return cognomeTrasformatore;
    }

    public String getAziendaTrasformatore() {
        return aziendaTrasformatore;
    }

    public List<FaseLavorazioneDTO> getFasi() {
        return fasi;
    }
}