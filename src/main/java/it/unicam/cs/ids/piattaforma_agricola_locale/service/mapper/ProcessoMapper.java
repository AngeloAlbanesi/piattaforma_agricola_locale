package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.FaseLavorazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.ProcessoTrasformazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper per mappare le entità del dominio del processo di
 * trasformazione ai corrispondenti DTO (Data Transfer Objects).
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
@Component
public interface ProcessoMapper {

    /**
     * Converte un'entità ProcessoTrasformazione nel suo DTO corrispondente.
     *
     * @param processo l'entità ProcessoTrasformazione da convertire.
     * @return il ProcessoTrasformazioneDTO risultante.
     */
    @Mapping(target = "idProcesso", source = "id")
    @Mapping(target = "nomeProcesso", source = "nome")
    @Mapping(target = "descrizioneProcesso", source = "descrizione")
    @Mapping(target = "noteTecniche", source = "note")
    @Mapping(target = "idTrasformatore", source = "trasformatore.id")
    @Mapping(target = "nomeTrasformatore", source = "trasformatore.nome")
    @Mapping(target = "cognomeTrasformatore", source = "trasformatore.cognome")
    @Mapping(target = "aziendaTrasformatore", source = "trasformatore.datiAzienda.nomeAzienda", defaultValue = "N/D")
    @Mapping(target = "fasi", source = "fasiLavorazione")
    @Mapping(target = "dataInizioProcesso", ignore = true) // non è nel modello
    @Mapping(target = "dataFineProcesso", ignore = true) // non è nel modello
    ProcessoTrasformazioneDTO toDto(ProcessoTrasformazione processo);

    /**
     * Converte un'entità FaseLavorazione nel suo DTO corrispondente.
     * Mantiene l'oggetto FonteMateriaPrima completo per preservare tutte le
     * informazioni.
     *
     * @param fase l'entità FaseLavorazione da convertire.
     * @return il FaseLavorazioneDTO risultante.
     */
    @Mapping(target = "id", source = "id", defaultValue = "0L")
    @Mapping(target = "fonteMateriaPrima", source = "fonte")
    FaseLavorazioneDTO toFaseDto(FaseLavorazione fase);
}