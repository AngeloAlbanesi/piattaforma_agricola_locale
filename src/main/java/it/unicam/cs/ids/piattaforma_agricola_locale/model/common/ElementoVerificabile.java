package it.unicam.cs.ids.piattaforma_agricola_locale.model.common;

public interface ElementoVerificabile {
    public void setStatoVerifica(StatoVerificaValori statoVerifica);
    public StatoVerificaValori getStatoVerifica();
    public int getId();
    public String getFeedbackVerifica();
    public void setFeedbackVerifica(String feedbackVerifica);
}