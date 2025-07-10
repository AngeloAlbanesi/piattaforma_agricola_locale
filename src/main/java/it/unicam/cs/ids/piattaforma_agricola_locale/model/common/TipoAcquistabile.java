package it.unicam.cs.ids.piattaforma_agricola_locale.model.common;

public enum TipoAcquistabile {
    PRODOTTO("PRODOTTO", "it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto"),
    PACCHETTO("PACCHETTO", "it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto"),
    EVENTO("EVENTO", "it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento");

    private final String nome;
    private final String className;

    TipoAcquistabile(String nome, String className) {
        this.nome = nome;
        this.className = className;
    }

    public String getNome() {
        return nome;
    }

    public String getClassName() {
        return className;
    }

    public static TipoAcquistabile fromAcquistabile(Acquistabile acquistabile) {
        if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) {
            return PRODOTTO;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) {
            return PACCHETTO;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) {
            return EVENTO;
        } else {
            throw new IllegalArgumentException("Tipo Acquistabile non supportato: " + acquistabile.getClass().getSimpleName());
        }
    }

    public static TipoAcquistabile fromString(String nome) {
        for (TipoAcquistabile tipo : values()) {
            if (tipo.nome.equals(nome)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo Acquistabile non trovato: " + nome);
    }
}