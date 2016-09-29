package org.zalando.jackson.datatype.money;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public final class FieldNamesTest {

    @Test
    public void shouldOptimizeWithMethods() {
        final FieldNames expected = FieldNames.defaults();
        final FieldNames actual = expected
                .withAmount(expected.getAmount())
                .withCurrency(expected.getCurrency())
                .withFormatted(expected.getFormatted());

        assertThat(actual, is(sameInstance(expected)));
    }

}