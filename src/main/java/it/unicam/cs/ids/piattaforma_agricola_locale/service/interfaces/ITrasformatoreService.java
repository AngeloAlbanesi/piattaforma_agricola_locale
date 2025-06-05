package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.ProcessoDiTrasformazione;

import java.util.List;

public interface ITrasformatoreService extends IVenditoreService {

    // Aggiungi metodi specifici per il servizio Trasformatore, se necessario
    // Ad esempio, puoi aggiungere metodi per gestire i prodotti trasformati o altre funzionalità specifiche del trasformatore
    public ProcessoDiTrasformazione creaProcessoTrasformazione(int idTrasforamtore, String nomeProcesso, String descrizione, int prodottoOutput);
    public void aggiungiFaseAlProcesso(int idProcesso, String descrizioneFase, int ordine);
    public void rimuoviFaseDalProcesso(int idProcesso, int idFase);
    public void aggiungiInputAFase(int idFase, int idProdottoInput, int idProduttoreOridigine, int quantita); //aggiungere unità di misura?
    public void rimuoviInputAFase(int idFase, int idProdotto);
    public List<ProcessoDiTrasformazione> getProcessiByTrasformatore(int idTrasformatore);
    public List<ProcessoDiTrasformazione> getProcessiByProdotto(int idProdotto);
}
