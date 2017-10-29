package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.SerializerProvider;

import java.util.Locale;

/**
 * @author Petar Tahchiev
 * @since 1.5
 */
public interface LocaleProvider {

    Locale getCurrentLocale(SerializerProvider serializerProvider);
}
