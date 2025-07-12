/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation per indicare che un metodo richiede che l'utente sia accreditato.
 * Solo venditori, curatori e animatori con stato ACCREDITATO possono accedere
 * ai metodi annotati con questa annotation.
 * 
 * Questa annotation deve essere usata insieme ai controlli di autorizzazione
 * esistenti (@PreAuthorize).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAccreditation {

    /**
     * Messaggio personalizzato per l'errore di accreditamento.
     * Se non specificato, viene usato un messaggio di default.
     */
    String message() default "";
}
