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

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.io.IOException;
import java.math.BigDecimal;

public final class MonetaryAmountSerializer extends JsonSerializer<MonetaryAmount> {

    private final MonetaryAmountFormat format;

    public MonetaryAmountSerializer() {
        this(null);
    }
    
    public MonetaryAmountSerializer(@Nullable final MonetaryAmountFormat format) {
        this.format = format;
    }

    @Override
    public void serialize(final MonetaryAmount value, final JsonGenerator generator, final SerializerProvider provider)
            throws IOException {

        final BigDecimal amount = value.getNumber().numberValueExact(BigDecimal.class);
        final CurrencyUnit currency = value.getCurrency();
        final String formatted = format == null ? null : format.format(value);

        generator.writeObject(new MoneyNode(amount, currency, formatted));
    }

}