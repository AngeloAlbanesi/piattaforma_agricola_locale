package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.Value;

/**
 * DTO per la risposta delle operazioni sui processi di trasformazione
 * che non devono includere le fasi (come l'aggiunta di una singola fase).
 */
@Value
public class ProcessoTrasformazioneResponseDTO {

    Long idProcesso;
    String nomeProcesso;
    String descrizioneProcesso;
    String noteTecniche;
    Long idTrasformatore;
    String nomeTrasformatore;
    String cognomeTrasformatore;
    String aziendaTrasformatore;

    public ProcessoTrasformazioneResponseDTO(Long idProcesso, String nomeProcesso, String descrizioneProcesso,
                                           String noteTecniche, Long idTrasformatore, String nomeTrasformatore,
                                           String cognomeTrasformatore, String aziendaTrasformatore) {
        this.idProcesso = idProcesso;
        this.nomeProcesso = nomeProcesso;
        this.descrizioneProcesso = descrizioneProcesso;
        this.noteTecniche = noteTecniche;
        this.idTrasformatore = idTrasformatore;
        this.nomeTrasformatore = nomeTrasformatore;
        this.cognomeTrasformatore = cognomeTrasformatore;
        this.aziendaTrasformatore = aziendaTrasformatore;
    }
}