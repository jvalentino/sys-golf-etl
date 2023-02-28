# System Foxtrot ETL

This application serves the function of taking data from the main database and using it to populate a database used for data warehousing as a part of the overall https://github.com/jvalentino/sys-foxtrot project. For details system document, please see that location.

Prerequisites

- Java
- IntelliJ
- Docker
- Docker Compose
- pgadmin
- Git

All of these you can get in one command using this installation automation (if you are on a Mac): https://github.com/jvalentino/setup-automation

## Database

You launch the database container by running:

```
docker compose up -d
```

This sill execute the container in detached mode, and leave it running in the background.

However, this only runs the warehousing database, so you also need to go into the sys-foxtrot-rest project and run its database too:

```bash
docker compose up -d
```



## IDE Testing

This imports as a standard Gradle project, in which you are able to easily execute tests:

[![01](https://github.com/jvalentino/sys-alpha-bravo/raw/main/wiki/testing.png)](https://github.com/jvalentino/sys-alpha-bravo/blob/main/wiki/testing.png)

## Runtime

You can right-click on the main application and run as a Spring Boot App, however I am also running it on port 8081 so I can run the rest app at the same time:

![01](wiki/run-as.png)

![01](wiki/server-port.png)

This will then be accessible via http://localhost:8081, which gives a status page:

![01](wiki/8081.png)

## Verification

[![01](https://github.com/jvalentino/sys-alpha-bravo/raw/main/wiki/ide_check.png)](https://github.com/jvalentino/sys-alpha-bravo/blob/main/wiki/ide_check.png)

Running check will execute both testing and static code analysis via the build.

This is otherwise the same as doing this at the command-line: `./gradlew check`

## Strategy

Codenarc is used to ensure that no common coding issues can be added.

Jacoco is used to enforce that line coverage is over 85%.

Tests that end in "IntgTest" are used for integration testing via Spring Boot Test, otherwise they are unit tests.

Every code commit triggers a Github Action pipeline that runs the entire build process.



