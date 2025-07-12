package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

/**
 * Classe astratta che rappresenta l'origine di una materia prima,
 * astraendo se la fonte è interna (un produttore della piattaforma)
 * o esterna.
 */
@Entity
@Table(name = "fonti_materia_prima")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_fonte", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FonteEsterna.class, name = "ESTERNA"),
    @JsonSubTypes.Type(value = FonteInterna.class, name = "INTERNA")
})
public abstract class FonteMateriaPrima {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    public FonteMateriaPrima() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Restituisce una descrizione testuale della fonte.
     *
     * @return una stringa che descrive la fonte.
     */
    public abstract String getDescrizione();
}