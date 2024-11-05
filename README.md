# Spring boot health care application

## _OVERVIEW_
![healthcare-dark drawio](https://github.com/user-attachments/assets/c7459c8d-88af-448f-a86f-eebfa4ffec19)

This healthcare architecture overview

## _Technologies Used_
- Spring Boot
- Apache Poi
- JPA
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
application addess:
```text
Postgresql :
- Host : localhost
- Port : 5435 -> 5432
- Database : postgres(spring boot data) , keycloak(keycloak metadata)
- Username : dev
- Password : password

Keycloak (dashboard) :
- Host : localhost
- Port : 8080
- Username : admin 
_ Password : admin 

Spring Healthcare :
- Host : localhost
- Port : 8081
```

## _How To Test_
To run the test you can just use this maven command :
```text
mvn test
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

you can use this user for get the admin role token :
```text
username : admin
password : password
```

to get the token for user with admin role you can just use this curl:
```shell
curl --location 'http://localhost:8080/realms/healthcare/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=healthcare-api' \
--data-urlencode 'username=admin' \
--data-urlencode 'password=password' \
--data-urlencode 'client_secret=8qW3zcQ1UxmFicxaP4Zw9t9dMXHeylgc'
```

to get the token for user with user role you can just use this curl:
```shell
curl --location 'http://localhost:8080/realms/healthcare/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=healthcare-api' \
--data-urlencode 'username=admin' \
--data-urlencode 'password=password' \
--data-urlencode 'client_secret=8qW3zcQ1UxmFicxaP4Zw9t9dMXHeylgc'
```

to save user you can use with admin as role:
```shell
curl --location 'http://localhost:8081/api/user' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {admin-token}' \
--data-raw '{
    "username": "admin-2",
    "password": "password",
    "email": "admin2@test.com",
    "rolesName": ["admin"]
}'
```

to save patient:
```shell
curl --location 'http://localhost:8081/api/patient' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {admin-token}' \
--data '{
    "name":"felix"
}'
```

to get all patient:
```shell
curl --location 'http://localhost:8081/api/patient' \
--header 'Authorization: Bearer {user-token}'
```

on ./files/lab.xlsx as example to upload. to test you can do:
```shell
curl --location 'http://localhost:8081/api/lab-result/upload' \
--header 'Authorization: Bearer {admin-token}' \
--form 'file=@"/D:/work/Itv/lab.xlsx"'
```
