# Jackson Datatype Money

[![Build Status](https://img.shields.io/travis/zalando/jackson-datataype-money.svg)](https://travis-ci.org/zalando/jackson-datataype-money)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-datataype-money.svg)](https://coveralls.io/r/zalando/jackson-datataype-money)
[![Release](https://img.shields.io/github/release/zalando/jackson-datataype-money.svg)](https://github.com/zalando/jackson-datataype-money/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-datatype-money.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-datatype-money)

## Dependency

    <dependency>
        <groupId>org.zalando</groupId>
        <artifactId>jackson-datatype-money</artifactId>
        <version>${jackson-datatype-money.versions}</version>
    </dependency>

## Usage

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new MoneyModule());

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
