# Jackson Datatype Money

[Jackson](http://jackson.codehaus.org) module (jar)
to support JSON serialization and deserialization of
[javax.money](https://github.com/JavaMoney/jsr354-api) data types.

[![Build Status](https://img.shields.io/travis/zalando/jackson-datatype-money.svg)](https://travis-ci.org/zalando/jackson-datatype-money)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-datatype-money.svg)](https://coveralls.io/r/zalando/jackson-datatype-money)
[![Release](https://img.shields.io/github/release/zalando/jackson-datatype-money.svg)](https://github.com/zalando/jackson-datatype-money/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-datatype-money.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-datatype-money)

## Dependency

```xml
    <dependency>
        <groupId>org.zalando</groupId>
        <artifactId>jackson-datatype-money</artifactId>
        <version>${jackson-datatype-money.version}</version>
    </dependency>
```

## Usage

```java
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new MoneyModule());
```
    
The module supports de/serialization of the following types:

### [`java.util.Currency`](https://docs.oracle.com/javase/8/docs/api/java/util/Currency.html)
[ISO-4217](http://en.wikipedia.org/wiki/ISO_4217) currency code

### [`javax.money.CurrencyUnit`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/CurrencyUnit.java)
[ISO-4217](http://en.wikipedia.org/wiki/ISO_4217) currency code

```json
```

### [`javax.money.MonetaryAmount`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/MonetaryAmount.java)

```
Money.of(29.95, "EUR");
```

```json
{
  "amount": 29.95, 
  "currency": "EUR"
}
```

## License

Copyright [2015] Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
