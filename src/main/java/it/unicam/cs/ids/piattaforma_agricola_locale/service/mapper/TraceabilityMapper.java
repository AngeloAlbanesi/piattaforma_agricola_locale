package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.FaseLavorazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.TraceabilityDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per convertire ProcessoTrasformazione in TraceabilityDTO
 * con tutte le informazioni necessarie per la tracciabilità.
 */
@Component
public class TraceabilityMapper {

    /**
     * Converte un ProcessoTrasformazione in TraceabilityDTO.
     *
     * @param processo il processo di trasformazione
     * @return il DTO con le informazioni di tracciabilità
     */
    public TraceabilityDTO toTraceabilityDTO(ProcessoTrasformazione processo) {
        if (processo == null) {
            return null;
        }

        TraceabilityDTO dto = new TraceabilityDTO();
        dto.setProcessoId(processo.getId());
        dto.setNomeProcesso(processo.getNome());
        dto.setDescrizioneProcesso(processo.getDescrizione());
        dto.setMetodoProduzione(processo.getMetodoProduzione());

        // Informazioni del trasformatore
        Trasformatore trasformatore = processo.getTrasformatore();
        if (trasformatore != null) {
            dto.setNomeTrasformatore(trasformatore.getNome() + " " + trasformatore.getCognome());
            if (trasformatore.getDatiAzienda() != null) {
                dto.setAziendaTrasformatore(trasformatore.getDatiAzienda().getNomeAzienda());
            }
        }

        // Prodotto finale
        if (processo.getProdottoFinale() != null) {
            dto.setProdottoFinale(processo.getProdottoFinale().getNome());
        }

        // Fasi di lavorazione ordinate per ordine di esecuzione
        List<FaseLavorazioneDTO> fasiDTO = processo.getFasiLavorazione().stream()
                .sorted(Comparator.comparing(FaseLavorazione::getOrdineEsecuzione))
                .map(this::toFaseLavorazioneDTO)
                .collect(Collectors.toList());
        dto.setFasi(fasiDTO);

        // Genera la stringa di tracciabilità
        dto.setTracciabilita(generateTracciabilita(processo, fasiDTO));

        return dto;
    }

    /**
     * Converte una FaseLavorazione in FaseLavorazioneDTO.
     */
    private FaseLavorazioneDTO toFaseLavorazioneDTO(FaseLavorazione fase) {
        return new FaseLavorazioneDTO(
                fase.getId(),
                fase.getNome(),
                fase.getDescrizione(),
                fase.getOrdineEsecuzione(),
                fase.getMateriaPrimaUtilizzata(),
                fase.getFonte()
        );
    }

    /**
     * Genera una stringa di tracciabilità completa per il processo.
     */
    private String generateTracciabilita(ProcessoTrasformazione processo, List<FaseLavorazioneDTO> fasi) {
        StringBuilder tracciabilita = new StringBuilder();
        
        // Intestazione del processo
        tracciabilita.append("=== TRACCIABILITÀ PROCESSO DI TRASFORMAZIONE ===\n");
        tracciabilita.append("Processo: ").append(processo.getNome()).append("\n");
        tracciabilita.append("Trasformatore: ").append(processo.getTrasformatore().getNome())
                    .append(" ").append(processo.getTrasformatore().getCognome()).append("\n");
        
        if (processo.getTrasformatore().getDatiAzienda() != null) {
            tracciabilita.append("Azienda: ").append(processo.getTrasformatore().getDatiAzienda().getNomeAzienda()).append("\n");
        }
        
        if (processo.getMetodoProduzione() != null) {
            tracciabilita.append("Metodo di Produzione: ").append(processo.getMetodoProduzione()).append("\n");
        }
        
        tracciabilita.append("\n=== FASI DI LAVORAZIONE ===\n");
        
        // Dettagli delle fasi
        for (FaseLavorazioneDTO fase : fasi) {
            tracciabilita.append("\n--- Fase ").append(fase.getOrdineEsecuzione()).append(": ")
                        .append(fase.getNome()).append(" ---\n");
            tracciabilita.append("Descrizione: ").append(fase.getDescrizione()).append("\n");
            tracciabilita.append("Materia Prima: ").append(fase.getMateriaPrimaUtilizzata()).append("\n");
            tracciabilita.append("Fonte: ").append(fase.getDescrizioneFonte())
                        .append(" (").append(fase.getTipoFonte()).append(")\n");
            
            if (fase.isFonteInterna()) {
                tracciabilita.append("ID Produttore Interno: ").append(fase.getIdProduttoreInterno()).append("\n");
            }
        }
        
        // Prodotto finale
        if (processo.getProdottoFinale() != null) {
            tracciabilita.append("\n=== PRODOTTO FINALE ===\n");
            tracciabilita.append("Prodotto: ").append(processo.getProdottoFinale().getNome()).append("\n");
        }
        
        tracciabilita.append("\n=== FINE TRACCIABILITÀ ===");
        
        return tracciabilita.toString();
    }
}