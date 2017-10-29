package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.SerializerProvider;

import java.util.Locale;

/**
 * @author Petar Tahchiev
 * @since 1.5
 */
public class DefaultLocaleProviderImpl implements LocaleProvider {
    @Override
    public Locale getCurrentLocale(SerializerProvider provider) {
        return provider.getConfig().getLocale();
    }
}
