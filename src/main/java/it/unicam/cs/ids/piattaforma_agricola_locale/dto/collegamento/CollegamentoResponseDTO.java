package it.unicam.cs.ids.piattaforma_agricola_locale.dto.collegamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO di risposta per le operazioni di collegamento processo-prodotto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollegamentoResponseDTO {
    
    private Long processoId;
    private String nomeProcesso;
    private Long prodottoId;
    private String nomeProdotto;
    private String operazione; // "COLLEGATO", "SCOLLEGATO", "AGGIORNATO"
    private String messaggio;
    
    public static CollegamentoResponseDTO collegato(Long processoId, String nomeProcesso, 
                                                   Long prodottoId, String nomeProdotto) {
        return new CollegamentoResponseDTO(
            processoId, nomeProcesso, prodottoId, nomeProdotto,
            "COLLEGATO", "Processo e prodotto collegati con successo"
        );
    }
    
    public static CollegamentoResponseDTO scollegato(Long processoId, String nomeProcesso, 
                                                    Long prodottoId, String nomeProdotto) {
        return new CollegamentoResponseDTO(
            processoId, nomeProcesso, prodottoId, nomeProdotto,
            "SCOLLEGATO", "Collegamento rimosso con successo"
        );
    }
    
    public static CollegamentoResponseDTO aggiornato(Long processoId, String nomeProcesso, 
                                                    Long prodottoId, String nomeProdotto) {
        return new CollegamentoResponseDTO(
            processoId, nomeProcesso, prodottoId, nomeProdotto,
            "AGGIORNATO", "Collegamento aggiornato con successo"
        );
    }
}