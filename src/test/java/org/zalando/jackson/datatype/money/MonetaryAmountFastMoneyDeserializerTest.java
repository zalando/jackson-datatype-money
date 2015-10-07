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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public final class MonetaryAmountFastMoneyDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new FastMoneyModule());

    @Test
    public void shouldDeserialize() throws IOException {
        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = mapper.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        final BigDecimal expected = new BigDecimal("29.95");

        assertThat(actual, comparesEqualTo(expected));
        assertThat(amount, is(instanceOf(FastMoney.of(0.0, "CHF").getClass())));
    }

    @Test (expected = ArithmeticException.class)
    public void shouldNotDeserialize() throws IOException {
        final String content = "{\"amount\":29.955555,\"currency\":\"EUR\"}";
         mapper.readValue(content, MonetaryAmount.class);
    }

    @Test
    public void shouldDeserializeWhenPropertiesAreInDifferentOrder() throws IOException {
        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = mapper.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        assertThat(actual, comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount, is(instanceOf(FastMoney.of(0.0,"CHF").getClass())));
    }

}
