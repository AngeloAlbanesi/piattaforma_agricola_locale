package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO che rappresenta le informazioni di un processo di trasformazione.
 * Contiene i dati del processo e una lista di fasi di lavorazione semplificate.
 */
@Value
public class ProcessoTrasformazioneDTO {

    Long idProcesso;
    String nomeProcesso;
    String descrizioneProcesso;
    String noteTecniche;
    Long idTrasformatore;
    String nomeTrasformatore;
    String cognomeTrasformatore;
    String aziendaTrasformatore;
    List<FaseLavorazioneDTO> fasi;

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
}