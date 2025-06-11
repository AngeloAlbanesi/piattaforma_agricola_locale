package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.FaseLavorazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.ProcessoTrasformazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe di utilità per mappare le entità del dominio del processo di
 * trasformazione
 * ai corrispondenti DTO (Data Transfer Objects).
 */
public class ProcessoMapper {

    /**
     * Converte un'entità ProcessoTrasformazione nel suo DTO corrispondente.
     *
     * @param processo l'entità ProcessoTrasformazione da convertire.
     * @return il ProcessoTrasformazioneDTO risultante.
     */
    public ProcessoTrasformazioneDTO toDto(ProcessoTrasformazione processo) {
        if (processo == null) {
            return null;
        }

        Trasformatore trasformatore = processo.getTrasformatore();
        DatiAzienda datiAzienda = trasformatore.getDatiAzienda();

        List<FaseLavorazioneDTO> fasiDto = processo.getFasiLavorazione().stream()
                .map(this::toFaseDto)
                .collect(Collectors.toList());

        return new ProcessoTrasformazioneDTO(
                processo.getId(),
                processo.getNome(),
                processo.getDescrizione(),
                null, // dataInizioProcesso non è nel modello
                null, // dataFineProcesso non è nel modello
                processo.getNote(),
                trasformatore.getId(),
                trasformatore.getNome(),
                trasformatore.getCognome(),
                datiAzienda != null ? datiAzienda.getNomeAzienda() : "N/D",
                fasiDto);
    }

    /**
     * Converte un'entità FaseLavorazione nel suo DTO corrispondente.
     * Mantiene l'oggetto FonteMateriaPrima completo per preservare tutte le
     * informazioni.
     *
     * @param fase l'entità FaseLavorazione da convertire.
     * @return il FaseLavorazioneDTO risultante.
     */
    private FaseLavorazioneDTO toFaseDto(FaseLavorazione fase) {
        if (fase == null) {
            return null;
        }

        return new FaseLavorazioneDTO(
                fase.getId() != null ? fase.getId() : 0L,
                fase.getNome(),
                fase.getDescrizione(),
                fase.getOrdineEsecuzione(),
                fase.getMateriaPrimaUtilizzata(),
                fase.getFonte()); // Passa l'oggetto FonteMateriaPrima completo
    }
}