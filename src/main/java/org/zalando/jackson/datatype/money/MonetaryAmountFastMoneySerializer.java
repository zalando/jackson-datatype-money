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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

public final class MonetaryAmountFastMoneySerializer extends JsonSerializer<MonetaryAmount> {

    @Override
    public void serialize(MonetaryAmount value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {

        final Double amount = value.getNumber().numberValueExact(Double.class);
        final CurrencyUnit currency = value.getCurrency();

        generator.writeObject(new FastMoneyNode(amount, currency));
    }

}