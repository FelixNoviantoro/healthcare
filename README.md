# Spring boot health care application

## _OVERVIEW_

This healthcare backend spring boot application designed to manage.

## _Technologies Used_
- Spring Boot
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

