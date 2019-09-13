[![Travis](https://img.shields.io/travis/Rudge/kotlin-javalin-realworld-example-app.svg)](https://travis-ci.org/Rudge/kotlin-javalin-realworld-example-app/builds)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b6503dfa3024a0dbbf173e333f80bcf)](https://app.codacy.com/app/Rudge/kotlin-javalin-realworld-example-app?utm_source=github.com&utm_medium=referral&utm_content=Rudge/kotlin-javalin-realworld-example-app&utm_campaign=Badge_Grade_Dashboard)
[![BCH compliance](https://bettercodehub.com/edge/badge/Rudge/kotlin-javalin-realworld-example-app?branch=master)](https://bettercodehub.com/)

# ![RealWorld Example App](logo.png)

> ### Kotlin + Javalin codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API

### [RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with **Kotlin + Javalin + Koin + Exposed** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Kotlin + Javalin** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# How it works

The application was made mainly to demo the functionality of Javalin framework [71](https://github.com/tipsy/javalin/issues/71).

The application was built with:

  - [Kotlin](https://github.com/JetBrains/kotlin) as programming language
  - [Javalin](https://github.com/tipsy/javalin) as web framework
  - [Koin](https://github.com/InsertKoinIO/koin) as dependency injection framework
  - [Jackson](https://github.com/FasterXML/jackson-module-kotlin) as data bind serialization/deserialization
  - [Java-jwt](https://github.com/auth0/java-jwt) for JWT spec implementation
  - [HikariCP](https://github.com/brettwooldridge/HikariCP) as datasource to abstract driver implementation
  - [H2](https://github.com/h2database/h2database) as database
  - [Exposed](https://github.com/JetBrains/Exposed) as Sql framework to persistence layer
  - [slugify](https://github.com/slugify/slugify)

Tests:

  - [junit](https://github.com/junit-team/junit4)
  - [Unirest](https://github.com/Kong/unirest-java) to call endpoints in tests

#### Structure
      + config/
          All app setups. Javalin, Koin and Database
      + domain/
        + repository/
            Persistence layer and tables definition
        + service/
            Logic layer and transformation data
      + ext/
          Extension of String for email validation
      + utils/
          Jwt and Encrypt classes
      + web/
        + controllers
            Classes and methods to mapping actions of routes
        Router definition to features and exceptions
      - App.kt <- The main class

#### Database

It uses a H2 in memory database (for now), can be changed easily in the `koin.properties` for any other database.
You'll need to add the correct dependency for the needed `Driver` in `build.gradle`.

# Getting started

You need just JVM installed.

The server is configured to start on [7000](http://localhost:7000/api) with `api` context, but you can change in `koin.properties`.

#### Build:
> ./gradlew clean build

#### Start the server:
> ./gradlew run

In the project have the [spec-api](https://github.com/Rudge/kotlin-javalin-realworld-example-app/tree/master/spec-api) with the README and collections to execute backend tests specs [realworld](https://github.com/gothinkster/realworld).

You can access the h2-console at [console](http://localhost:8082/).

#### Execute tests and start the server:

> ./gradlew run & APIURL=http://localhost:7000/api ./spec-api/run-api-tests.sh

# Help

Please fork and PR to improve the code.
