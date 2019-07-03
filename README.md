# Cloud Native Microservices - User Service

## Framework

This service utilizes Micronaut with ORDS to persist users to a `user` table in an Oracle ATP instance.

To learn more about ORDS, refer to [this blog post](https://blogs.oracle.com/developers/rest-services-now-available-for-oracle-autonomous-database).

## Setup

---

> **Note**: It is not required to run the Micronaut CLI setup scripts below if you've cloned this repository. They are included for reference only.

---

This service was created using the Micronaut CLI like so:

```bash
mn create-app codes.recursive.cnms.ords.user-service-ords
```

If you'd like support for GraalVM, create the app with the following command instead:

```bash
mn create-app codes.recursive.cnms.ords.user-service-ords --features graal-native-image
```

---

> **Note**: You will, however, need to run these SQL scripts...

---

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

### ORDS 'Auto' REST Enable

REST enable the schema and the table:

```oracle
BEGIN
    /* enable ORDS for schema */
    ORDS.ENABLE_SCHEMA(p_enabled => TRUE,
                       p_schema => 'USERSVC',
                       p_url_mapping_type => 'BASE_PATH',
                       p_url_mapping_pattern => 'usersvc',
                       p_auto_rest_auth => FALSE);
     /* enable ORDS for table */         
    ORDS.ENABLE_OBJECT(p_enabled => TRUE,
                       p_schema => 'USERSVC',
                       p_object => 'USERS',
                       p_object_type => 'TABLE',
                       p_object_alias => 'users',
                       p_auto_rest_auth => FALSE);
    COMMIT;
END;
```

**Note**: The argument `p_auto_rest_auth` being set to `FALSE` means that all unauthenticated requests will return a `401 Unauthorized` meaning we'll have to send credentials with each REST call.

Create a new privilege:

```oracle
DECLARE
 l_roles     OWA.VC_ARR;
 l_modules   OWA.VC_ARR;
 l_patterns  OWA.VC_ARR;
BEGIN
 l_roles(1)   := 'SQL Developer';
 l_patterns(1) := '/users/*';
 ORDS.DEFINE_PRIVILEGE(
     p_privilege_name => 'rest_privilege',
     p_roles          => l_roles,
     p_patterns       => l_patterns,
     p_modules        => l_modules,
     p_label          => '',
     p_description    => '',
     p_comments       => NULL);
 COMMIT;

END;
```

Create an oauth client associated with the privilege:

```oracle
BEGIN
  OAUTH.create_client(
    p_name            => '[Descriptive Name For Client]',
    p_grant_type      => 'client_credentials',
    p_owner           => '[Owner Name]',
    p_description     => '[Client Description]',
    p_support_email   => '[Email Address]',
    p_privilege_names => 'rest_privilege'
  );

  COMMIT;
END;
```

Grant the `SQL Developer` role to the client application:

```oracle
BEGIN
  OAUTH.grant_client_role(
    p_client_name => 'Rest Client',
    p_role_name   => 'SQL Developer'
  );
  
  COMMIT;
END;
```

You can now grab the `client_id` and `client_secret` with:

```oracle
SELECT id, name, client_id, client_secret
FROM   user_ords_clients;
```

The `client_id` and `client_secret` can be used to generate an auth token for REST calls (this microservice handles this for you). To run the microservice, you'll need the `client_id` and `client_secret` set as environment variables to run the application. See [setting environment variables](#setting-environment-variables).

### Custom ORDS Services

You can create custom ORDS services like so:

```oracle
BEGIN
  ORDS.define_service(
    p_module_name    => 'users',
    p_base_path      => 'users/',
    p_pattern        => 'user/:username',
    p_method         => 'GET',
    p_source_type    => ORDS.source_type_collection_feed,
    p_source         => 'SELECT id, first_name, last_name, created_on FROM users WHERE username = :username OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY');
  COMMIT;
END;
```

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

## Setting environment variables

Set environment variables as follows:

```bash
export CODES_RECURSIVE_CNMS_ORDS_CLIENT_ID=[CLIENT ID]
export CODES_RECURSIVE_CNMS_ORDS_CLIENT_SECRET=[CLIENT SECRET]
export CODES_RECURSIVE_CNMS_ORDS_BASE_URL=[ATP ORDS BASE URL]
```

## Running

```bash
java -jar build/libs/user-service-ords-0.1.jar
```

## Test Endpoints

Get User Service Endpoint (returns 200 OK):

```bash
curl -iX GET http://localhost:8080/user                                                                                                                                      
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:04:25 GMT
content-type: application/json
content-length: 11
connection: keep-alive

{"OK":true}                                                                
```

Save a new user (ID is returned in `Location` header):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"first_name": "Tony", "last_name": "Stark", "username": "ironman"}' http://localhost:8080/user
HTTP/1.1 201 Created
Location: http://localhost:8080/user/user/8CA3E5278A78A1B4E0532010000A7AFF
Date: Tue, 2 Jul 2019 13:59:09 GMT
connection: keep-alive
transfer-encoding: chunked
```

Save a new user with invalid data (will return 400 and validation errors):

```bash
curl -iX POST -H "Content-Type: application/json" -d '{"first_name": "Tony", "last_name": "Stark", "username": null}' http://localhost:8080/user
HTTP/1.1 400 Bad Request
Date: Wed, 3 Jul 2019 20:45:15 GMT
content-type: application/json
content-length: 98
connection: close

{"_links":{"self":{"href":"/user","templated":false}},"message":"user.username: must not be null"}%
```

Get the new user

```bash
curl -iX GET http://localhost:8080/user/8CB931BBDA2ABCF7E0532010000A09C7
HTTP/1.1 200 OK
Date: Tue, 2 Jul 2019 14:00:49 GMT
content-type: application/json
content-length: 142
connection: keep-alive

{"id":"8CA3E5278A78A1B4E0532010000A7AFF","username":"ironman","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T13:59:07.752Z"}
```

List all users:

```bash
curl -iX GET http://localhost:8080/user/users
HTTP/1.1 200 OK
Date: Wed, 3 Jul 2019 18:19:03 GMT
content-type: application/json
content-length: 1016
connection: keep-alive

{"users":[{"id":"8C561D58E856DD25E0532010000AF462","username":"tsharp","first_name":"todd","last_name":"sharp","created_on":"2019-06-27T15:31:40.385Z"},{"id":"8C561D58E857DD25E0532010000AF462","username":"gvenzl","first_name":"gerald","last_name":"venzl","created_on":"2019-06-27T15:31:40.517Z"},{"id":"8C561D58E858DD25E0532010000AF462","username":"thatjeff","first_name":"jeff","last_name":"smith","created_on":"2019-06-27T15:31:40.646Z"},{"id":"8CB931BBDA2ABCF7E0532010000A09C7","username":"ironman","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T20:00:02.049Z"},{"id":"8CB92D3E1E53BCE6E0532010000A5B6D","username":"ironman","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T19:59:03.883Z"},{"id":"8CB66A3C911BC2EAE0532010000A57FB","username":"ironman","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T16:41:22.353Z"},{"id":"8CCBD5FBBB5D98EDE0532010000A7B85","username":"ironman","first_name":"Anthony","last_name":"Stark","created_on":"2019-07-03T18:14:44.638Z"}]}
```

List all users (paginated):

```bash
curl -iX GET http://localhost:8080/user/users/0/1
HTTP/1.1 200 OK
Date: Wed, 3 Jul 2019 18:20:05 GMT
content-type: application/json
content-length: 199
connection: keep-alive

{"offset":0,"count":1,"hasMore":true,"limit":1,"users":[{"id":"8C561D58E856DD25E0532010000AF462","username":"tsharp","first_name":"todd","last_name":"sharp","created_on":"2019-06-27T15:31:40.385Z"}]}
```

Delete a user:

```bash
curl -iX DELETE http://localhost:8080/user/8CB41C8DFB2FA3F6E0532010000A42F8
HTTP/1.1 204 No Content
Date: Tue, 2 Jul 2019 14:06:50 GMT
connection: keep-alive
```

Confirm delete (same GET by ID will return 404):

```bash
curl -iX GET http://localhost:8080/user/8CB41C8DFB2FA3F6E0532010000A42F8
HTTP/1.1 404 Not Found
Date: Tue, 2 Jul 2019 14:08:13 GMT
transfer-encoding: chunked
connection: close
```

Get user by username:

```bash
curl -iX GET http://localhost:8080/user/username/ironman
HTTP/1.1 200 OK
Date: Wed, 3 Jul 2019 19:23:55 GMT
content-type: application/json
content-length: 121
connection: keep-alive

{"id":"8CB931BBDA2ABCF7E0532010000A09C7","first_name":"Tony","last_name":"Stark","created_on":"2019-07-02T20:00:02.049Z"}
```

## View Health and Metrics

Micronaut does not enable health and metrics out of the box, but they are available via an add-on project. 

For more, see [https://docs.micronaut.io/latest/guide/index.html#management](https://docs.micronaut.io/latest/guide/index.html#management)


## Dockerfile

The generated `Dockerfile` requires some changes. See the `Dockerfile` for reference.

## Building the Docker Image

```
docker build -t user-svc-micronaut-native .
```

## Running with Docker

Run with: 

```bash
docker run -d --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_ID --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_SECRET --env CODES_RECURSIVE_CNMS_ORDS_BASE_URL --rm -p 8080:8080 -t user-svc-micronaut
```

Test the endpoints as [described above](#test-endpoints)

## Building a Docker Image That Runs a Graal Native Image

```bash
docker build -f Dockerfile-graal -t user-svc-micronaut-native . 
``` 

## Running the Graal Based Docker Image

```bash
docker run -d --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_ID --env CODES_RECURSIVE_CNMS_ORDS_CLIENT_SECRET --env CODES_RECURSIVE_CNMS_ORDS_BASE_URL --rm -p 8080:8080 -t user-service-ords-native
```

## Building for Deployment

```bash
docker build -f Dockerfile-graal -t [region].ocir.io/[tenancy]/[repo]/user-svc-micronaut-native .   
docker push [region].ocir.io/[tenancy]/[repo]/user-svc-micronaut-native
```


Test the endpoints as [described above](#test-endpoints)

## Deploying to Kubernetes

### Create Secret

Base64 encode the secret values before creating the YAML file.

On *nix systems, use something like this to accomplish for each value:

```bash
echo -n "client_id.." | base64
```

Then create a YAML file:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: user-svc-micronaut-native-secrets
data:
  clientId: [ORDS Client ID Base64 Encoded]
  clientSecret: [ORDS Client Secret Base64 Encoded]
  baseUrl: [ORDS Base URL Base64 Encoded]
---
```

Deploy secret and app via:

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f secret.yaml               # Deploy secret
kubectl create -f app.yaml               # Deploy application
kubectl get service user-svc-micronaut-native  # Verify deployed service
```