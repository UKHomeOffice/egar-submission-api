# API for submitting/transferring completed GARs to CBP

## Configuring the service
To run the service create an application.properties file in the same folder as the spring jar.
Additionally as this is a SpringBoot application the values can be stored as systems environment properties.
See [Springboot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) for full details.
As a short hand through out this document these are refered to as *parameters*.

## Setting the server's port

By default the service runs by default on port 8085. But to change this, add to the configuration the following:
```
--server.port=8080
```
This would put the service on port 8080.

## Monitoring the service
The service exposes a number of end-points for monitoring:
* `/health` A standard status health end-point, which will show the type of db in use.
* `/info` A standard informational endpoint which reveals the application name & version.
* `/healthz` An K8 endpoint for monitoring if the application is up. Http:200 or Http:500 if in trouble (No body content).
* `/readiness` A copy of the above; Subject to change..
* `/metrics` A K8 endpoint for monitoring the memory usage and other stats of the service.

## Running locally (in memory without calls to CBP, S3, SQS or jms)
To run the application locally using a mock CBP endpoint and a H2 database the following profile needs to be specified at startup.
```
--spring.profiles.active=h2,s3-mocks,jms-mocks,cbp-mocks
```
This will drop & create the H2 on each start. 
*This is the services default behaviour, when using H2.*

## Changing the jms queue names
To change the default file scan request and response queue names the following properties need to be configured
```
--submission.queue.name={queue_name}
--cancellation.queue.name={queue_name}
```
## Running with CBP
To run the application against the actual CBP endpoint we need to remove the cbp-mocks profile and add the following parameters:
```
--cbp.username={cbp_username}
--cbp.password={cbp_password}
--cbp.url={cbp_url}
```
## Running with S3
To run against S3 we need to remove the s3-mocks profile and add the following parameters:
```
--aws.s3.region={region}
--aws.s3.bucket={bucket}
--aws.s3.access.key={access_key}
--aws.s3.secret.key={secret_key}
```
## Running with SQS
To run against SQS we need to remove the jms-mocks profile and add the following parameters:
```
--aws.sqs.access.key={access_key}
--aws.sqs.secret.key={secret_key}
--aws.sqs.region={region}
```

## Running using Postgres 
To debug & develop the service it is acceptable to use H2. However ultimatly a Postgres database should be used. The easiest way for a developer to achieve this with no other dependencies is to put Postgres in a docker container.
To use a Postgres database, the system must be started with either the `prod` or `local` profiles as follows:
```
--spring.profiles.active=local 
```
or
```
--spring.profiles.active=prod 
```
Additionally the following parameters *need* to be specified as database connection parameters:
```
--prod.database.url={database_url}
--prod.database.username={database_username}
--prod.database.password={database_password}
```
The standard settings when using `local` are *database_username=`postgres`* and *database_password=`mysecretpassword`*. These values are built into the scripts listed here to create the docker images for Postgres.
The system will connect to a db instance on the same host with the database `dev_submission_api`.

### Create the local Postgres instance with Docker
This section details the creation of a postgress and an administration service for that database.
The first step is to initially create a docker-network to connect all the containers to:
```
docker network create dev
```
The network will be `dev` and all the containers will be in it.
A Postgres instance can be run up in a docker container as follows:
```
docker run --name postgres -d --network=dev -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 postgres:9.1
```
This starts an instance with the root password of `mysecretpassword`, on port `5432` and a container name of `postgres`.

To this can be added an administrative console (pgadmin) as follows:
```
docker run --name pgadmin -d --network=dev -p 80:80 -e "PGADMIN_DEFAULT_EMAIL=user@domain.com" -e "PGADMIN_DEFAULT_PASSWORD=SuperSecret" dpage/pgadmin4
```
Full details of this image can be found at [pgadmin4 docker image](https://hub.docker.com/r/dpage/pgadmin4/).
The pgadmin screen is available on (http:localhost:80)[http:localhost:80], login using *user@domain.com* with a password of *SuperSecret*. 

**NOTE:** With out the environment variables default user for this instance is *container@pgadmin.org* with a password of *Conta1ner*.

The user email address used when setting up the initial administrator account to login to pgAdmin.
This container will have a link to the postgres container. Which can be tested by creating a `bash` session on one of the servers and pinging the other server.
As follows:
```
docker exec -ti pdadmin bash
ping postgres
^C
exit
```

To stop the named containers use docker stop.
For instance for the pgadmin4 type:
```
docker stop pgadmin4 
```
To restart the container type:
```
docker start pgadmin4 
```
This needs to be done once the container has been named.


## Running locally (persistent database)
The application can be executed using a local Postgres instance using the following profile:
```
--spring.profiles.active=local 
```
By default the `user=postgres` and `pwd=mysecretpassword` and will validate the db on each start.
It will be necessary to create the schema against this db using the Flyway scripts in `/main/resources/db/migration`. 
this can be done in development by running the following maven command:

```
mvn -Dflyway.url=jdbc:postgresql://localhost/dev_submission_api -Dflyway.user=postgres -Dflyway.password=mysecretpassword flyway:migrate 
```

## Running against the production database
To run the application using an actual persistent database the following profile needs to be specified at startup: `--spring.profiles.active=prod`; with parameters as defined above.

**NOTE:** This performs no validation or creation and by default has flyway turned off.

### Creating and migrating the production database
For security reasons there should be two sets of database credentials: one for normal operation and the second for creation and migration of the database: an 'admin user'. 
The migration process is handled by a tool called [Flyway](https://flywaydb.org/) which is built into the micro-service.
To invoke these operations the service must be invoked with an additional set of properties:
```
--flyway.properties={flyway_command}
--flyway.database.admin.username={database_admin_username}
--flyway.database.admin.password={database_admin_password}
```

The {flyway_command} can be any of those found on the [Flyway command-line](https://flywaydb.org/documentation/commandline/) reference. However `migrate' is the most likely.
When invoked the micro-service **only** runs the Flyway operation and then stops. 

**NOTE** The migration scripts are located within the micro-service and can be found in `/main/resources/db/migration`. 

## Test and code coverage
When the following test command runs:
```
mvn test
```
The unit tests for the application will execute.

This will include generation of a Jacoco report at the following location:
```
/target/jacoco-ut/
```

As the project has used lombok there is a lot of auto generated code which would normally not be picked up under code coverage. 
The addition of the lombok.config file on the top level allows for the "@Generated" tag to be applied to lombok methods.
This will then be excluded from code coverage tests enabling us to generate a more accurate code coverage report.

##Integration Tests
To run the integration tests the following command needs to be run when the unit tests are run:
```
-Dtest.usesoap=true
```
This indicates that a soap ui integration stub is running.

To run the soap ui integration stub you need to do the following:
1. Open SoapUI application (https://www.soapui.org/downloads/soapui.html)
2. Select the import option at the top of the application
    1. Select the soap ui project found in the test resources under soap/project/CBP-wsdl-soapui-project.xml
3. This loads the project view on the left hand side
4. Right click on CBPMockService and select start
5. This will open a dialogue showing the soap ui project started.

## Running Flyway 
To run the application with Flyway the following profile can be specified at startup.
```
--flyway.properties={flyway.properties}
```
There are six available flyway properties: migrate, clean, info, validate, baseline and repair.
 
The flyway properties should be entered in the order of execution, all in lower case, separated by a comma and no white spaces E.g. cleaning the table then migrating:

```
--flyway.properties=clean,migrate
```
