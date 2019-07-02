# Cloud Native Microservices - User Service

## Framework

This service utilizes Micronaut with ORDS to persist users to a `user` table in an Oracle ATP instance.

## Setup

This service was created using the Micronaut CLI like so:

```bash
mn create-app codes.recursive.cnms.ords.user-service-ords
```

If you'd like support for GraalVM, create the app with the following command instead:

```bash
mn create-app codes.recursive.cnms.ords.user-service-ords --features graal-native-image
```

The DDL to create the database table looks like so:

Create the schema/user:

```sql
CREATE USER usersvc IDENTIFIED BY "STRONGPASSWORD";
GRANT create session TO usersvc;
GRANT create table TO usersvc;
GRANT create view TO usersvc;
GRANT create any trigger TO usersvc;
GRANT create any procedure TO usersvc;
GRANT create sequence TO usersvc;
GRANT create synonym TO usersvc;
GRANT CONNECT TO usersvc;
GRANT RESOURCE TO usersvc;
GRANT UNLIMITED TABLESPACE TO usersvc;
```

Create the necessary table(s):

```sql
CREATE TABLE users(
    "ID" VARCHAR2(32 BYTE) DEFAULT ON NULL SYS_GUID(), 
	"FIRST_NAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"LAST_NAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"USERNAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP" NOT NULL ENABLE, 
	"CREATED_ON" TIMESTAMP (6) DEFAULT ON NULL CURRENT_TIMESTAMP, 
	 CONSTRAINT "USER_PK" PRIMARY KEY ("ID")
);
```

_TODO: add ORDS statements..._

## Dependencies

This project has no manually installed external dependencies.

## Building

```bash
gradle assemble
```

Or, use the Gradle wrapper:

```bash
./gradlew assemble
```

## Running

```bash
java -jar build/libs/user-service-ords-0.1.jar
```

## Test Endpoints

Get User Service Endpoint (returns 200 OK):

```bash
curl -iX GET http://localhost:8080/ords                                                                                                                                            
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:04:25 GMT
content-type: application/json
content-length: 11
connection: keep-alive

{"OK":true}                                                                
```

Save a new user (ID is returned in `Location` header):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"first_name": "Tony", "last_name": "Stark", "username": "ironman"}' http://localhost:8080/ords/user                         
HTTP/1.1 201 Created
Location: http://localhost:8080/ords/user/8CA3E5278A78A1B4E0532010000A7AFF
Date: Tue, 2 Jul 2019 13:59:09 GMT
connection: keep-alive
transfer-encoding: chunked
```

Save a new user with invalid data (will return 400 and validation errors):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"first_name": "Tony", "last_name": "Stark", "username": null}' http://localhost:8080/ords/user                              
HTTP/1.1 400 Bad Request
Date: Tue, 2 Jul 2019 14:00:21 GMT
content-type: application/json
content-length: 103
connection: close

{"_links":{"self":{"href":"/ords/user","templated":false}},"message":"user.username: must not be null"}%  
```

Get the new user

```bash
curl -iX GET http://localhost:8080/ords/user/8CA3E5278A78A1B4E0532010000A7AFF                                                                                                      
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:00:49 GMT
content-type: application/json
content-length: 142
connection: keep-alive

{"id":"8CA3E5278A78A1B4E0532010000A7AFF","username":"ironman","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T13:59:07.752Z"}
```

List all users:

```bash
curl -iX GET http://localhost:8080/ords/users                                                                                                                                      
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:05:29 GMT
content-type: application/json
content-length: 1434
connection: keep-alive

{"items":[{"id":"8C561D58E856DD25E0532010000AF462","first_name":"todd","last_name":"sharp","username":"tsharp","created_on":"2019-06-27T15:31:40.385Z","links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/8C561D58E856DD25E0532010000AF462"}]},{"id":"8C561D58E857DD25E0532010000AF462","first_name":"gerald","last_name":"venzl","username":"gvenzl","created_on":"2019-06-27T15:31:40.517Z","links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/8C561D58E857DD25E0532010000AF462"}]},{"id":"8C561D58E858DD25E0532010000AF462","first_name":"jeff","last_name":"smith","username":"thatjeff","created_on":"2019-06-27T15:31:40.646Z","links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/8C561D58E858DD25E0532010000AF462"}]}],"hasMore":false,"limit":25,"offset":0,"count":3,"links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/"},{"rel":"edit","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/"},{"rel":"describedby","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/metadata-catalog/users/"},{"rel":"first","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/"}]}
```

List all users (paginated):

```bash
curl -iX GET http://localhost:8080/ords/users/0/1                                                                                                                                  
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:05:46 GMT
content-type: application/json
content-length: 973
connection: keep-alive

{"items":[{"id":"8C561D58E856DD25E0532010000AF462","first_name":"todd","last_name":"sharp","username":"tsharp","created_on":"2019-06-27T15:31:40.385Z","links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/8C561D58E856DD25E0532010000AF462"}]}],"hasMore":true,"limit":1,"offset":0,"count":1,"links":[{"rel":"self","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/"},{"rel":"edit","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/"},{"rel":"describedby","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/metadata-catalog/users/"},{"rel":"first","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/?limit=1"},{"rel":"next","href":"https://hvg9nd7xibsaegv-demodb.adb.us-phoenix-1.oraclecloudapps.com/ords/usersvc/users/?offset=1&limit=1"}]}
```

Delete a user:

```bash
curl -iX DELETE http://localhost:8080/ords/user/8CB41C8DFB2FA3F6E0532010000A42F8                                                                                                   
HTTP/1.1 204 No Content
Date: Tue, 2 Jul 2019 14:06:50 GMT
connection: keep-alive
```

Confirm delete (same GET by ID will return 404):

```bash
curl -iX GET http://localhost:8080/ords/user/8CB41C8DFB2FA3F6E0532010000A42F8                                                                                                      
HTTP/1.1 404 Not Found
Date: Tue, 2 Jul 2019 14:08:13 GMT
transfer-encoding: chunked
connection: close
```

## View Health and Metrics

Micronaut does not enable health and metrics out of the box, but they are available via an add-on project. 

For more, see [https://docs.micronaut.io/latest/guide/index.html#management](https://docs.micronaut.io/latest/guide/index.html#management)

## Dockerfile

The generated `Dockerfile` requires some changes. See the `Dockerfile` for reference, particularly the need to install the `ojdbc` dependencies to the local Maven repo so they are included in the build since these are unavailable via public Maven repos. 

## Building the Docker Image

```
docker build -t user-svc-ords .
```

## Running with Docker

Set environment variables as follows:

```bash
export CODES_RECURSIVE_CNMS_ORDS_CLIENT_ID=[CLIENT ID]
export CODES_RECURSIVE_CNMS_ORDS_CLIENT_SECRET=[CLIENT SECRET]
export CODES_RECURSIVE_CNMS_ORDS_BASE_URL=[ATP ORDS BASE URL]
```
Run with: 

```bash
docker run -d --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_ID --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_SECRET --env CODES_RECURSIVE_CNMS_ORDS_BASE_URL --rm -p 8080:8080 -t user-service-ords
```

Test the endpoints as [described above](#test-endpoints)

## Deploying to Kubernetes

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f app.yaml               # Deploy application
kubectl get service user-svc  # Verify deployed service
```