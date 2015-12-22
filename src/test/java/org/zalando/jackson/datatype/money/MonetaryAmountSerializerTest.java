package org.zalando.jackson.datatype.money;

/*
 * ⁣​
 * jackson-datatype-money
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.junit.Test;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryFormats;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MonetaryAmountSerializerTest {

    @Test
    public void shouldSerializeNoFormatter() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\",\"formattedValue\":\"EUR29.95\"}";
        final String actual = mapper.writeValueAsString(Money.of(29.95, "EUR"));

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithFormatter() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule( new MoneyModule( MonetaryFormats.getAmountFormat(
            AmountFormatQueryBuilder.of( Locale.US ).set( CurrencyStyle.SYMBOL ).build()
        )) );
        final String expected = "{\"amount\":29.95,\"currency\":\"USD\",\"formattedValue\":\"$29.95\"}";
        final String actual = mapper.writeValueAsString(Money.of(29.95, "USD"));

        assertThat(actual, is(expected));
    }


}
