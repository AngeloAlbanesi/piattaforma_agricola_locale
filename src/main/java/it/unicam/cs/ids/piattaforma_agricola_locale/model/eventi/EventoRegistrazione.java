package it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "evento_registrazioni", uniqueConstraints = @UniqueConstraint(columnNames = { "id_evento", "id_utente" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoRegistrazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registrazione")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    @Column(name = "data_registrazione", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistrazione;

    @Column(name = "numero_posti", nullable = false)
    private int numeroPosti;

    @Column(name = "note", length = 500)
    private String note;

    public EventoRegistrazione(Evento evento, Utente utente, int numeroPosti) {
        this.evento = evento;
        this.utente = utente;
        this.dataRegistrazione = new Date();
        this.numeroPosti = numeroPosti;
    }
}