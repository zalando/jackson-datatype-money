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

import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.Currency;

import static com.fasterxml.jackson.core.util.VersionUtil.mavenVersionFor;

public final class MoneyModule extends SimpleModule {

    public MoneyModule() {
        this(new MoneyFactory());
    }

    public MoneyModule(final MonetaryAmountFactory factory) {
        super(MoneyModule.class.getSimpleName(),
                mavenVersionFor(MoneyModule.class.getClassLoader(), "org.zalando", "jackson-datatype-money"));
        
        addSerializer(Currency.class, new CurrencySerializer());
        addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer());
        
        addDeserializer(Currency.class, new CurrencyDeserializer());
        addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
        addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer(factory));
    }

}
