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
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MonetaryAmountFastMoneySerializerTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new FastMoneyModule());

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = mapper.writeValueAsString(Money.of(29.95, "EUR"));

        assertThat(actual, is(expected));
    }
    
    
}