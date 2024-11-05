# Spring boot health care application

## _OVERVIEW_

This healthcare backend spring boot application designed to manage.

## _Technologies Used_
- Spring Boot
- Apache Poi
- Postgresql Database (16.2)
- Keycloak (21.1.1)
## _Prerequisites_
- Docker

make sure the docker installation is fine by using :
```shell
docker ps
```
- jdk 17 or above

make sure the java installation is fine by using :
```shell
java -version
```
- maven 3.6.3 or above

make sure the maven installation is fine by using :
```shell
mvn --version
```
## _How To Run_

- build the application using maven :
```shell
mvn clean package -DskipTest
```
After this build you should have the target directory generated.
If you get into the target you will found the jar application.
- run using docker compose file :
```shell
docker compose -p api-core up -d
```

## _Check Application Health_
You can open it using this endpoint:
```text
http://localhost:8081/actuator
```
You can also to see the api documentation here:
```text
http://localhost:8081/swagger-ui/index.html
```

## _Api Call_
this application has initial roles
- admin
- user

and also has initial user for call endpoint which needs the admin role:

you can use this user for get the admin token :
```text
username : admin
password : password
```

and to get the token you can just use this curl:
```shell
curl --location 'http://localhost:8080/realms/healthcare/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=healthcare-api' \
--data-urlencode 'username=admin' \
--data-urlencode 'password=password' \
--data-urlencode 'client_secret=8qW3zcQ1UxmFicxaP4Zw9t9dMXHeylgc'
```

