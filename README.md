# Daml Public Demos by Wallace Kelly

Each demo is in its own Git branch.

## Purpose

This demo configures an instance of [mock-oauth2-server](https://github.com/navikt/mock-oauth2-server)
as the authorization service for a Canton instance in a Docker Compose network.

This demo will enable you to:

* Experiment with various auth-related Canton configuration options.
* Experiment with the relationship between JWT tokens and Canton User Management.
* Examine the response of the Ledger API endpoints with and without various JWT claims.
* Test JWT-dependent scripts in a local environment.
* Build JWT-dependent, automated integration tests.

## Prerequisites

* Docker Desktop
* 

## Steps

1. Peruse the Canton configuration.
   * [canton.conf](./configs/canton.conf) defines three nodes (`mydomain`, `participant1`, and `participant2`)
   * [bootstrap.canton](./configs/bootstrap.canton) allocates three parties and three users per participant node.
2. Start the Docker Compose network.
   ```
   docker compose up --detach participant1 participant2
   ```
3. Start a console.
   ```
   docker compose run --rm console
   ```

To get a token:

```
curl -s http://mockauth/mockissuer/token \
  -d grant_type=client_credentials \
  -d client_id=ignored \
  -d client_secret=ignored \
  -d mock_token_type=audience \
  -d participant=`cat "./configs/participant2.id"` \
  -d sub=wallace \
  | jq -r '.access_token' \
  > token.tmp
```

To view the payload:

```
cat token.tmp | jq -R 'split(".") | .[0], .[1] | @base64d | fromjson'
```

To use a token:

```
daml ledger list-parties --host participant1 --port 4001

daml ledger list-parties --host participant2 --port 4001 --access-token-file token.tmp
```

To call a Ledger API endpoint:

```
grpcurl --plaintext participant1:4001 com.daml.ledger.api.v1.admin.UserManagementService/ListUsers | jq '.users[].id'

TOKEN=$(cat token.tmp)

grpcurl --plaintext -H "Authorization: Bearer ${TOKEN}" participant2:4001 com.daml.ledger.api.v1.admin.UserManagementService/ListUsers | jq '.users[].id'
```

To call a Daml script

```
daml script --dar .daml/dist/console-1.0.0.dar --all \
  --ledger-host participant2
  --ledger-port 4001
  --access-token-file token.tmp
```

```
docker run -it --rm console

canton -c configs/console.conf

participant1.ledger_api.users.list().users.map(u => u.id)

participant2.ledger_api.users.list().users.map(u => u.id)
```

## References

* [Canton User Manual, JWT Authorization](https://docs.daml.com/canton/usermanual/apis.html#jwt-authorization)
* [Canton App Development, Authorization](https://docs.daml.com/app-dev/authorization.html)
* [How to decode a JSON Web Token in Postman](https://medium.com/@jeff.heienickle/how-to-decode-a-json-web-token-in-postman-5312b3434462)
* [Decoding JSON Web Tokens (JWTs) from the Linux command line](https://prefetch.net/blog/2020/07/14/decoding-json-web-tokens-jwts-from-the-linux-command-line/)







```
docker compose up --detach canton mockauth
```

```
docker compose up --detach --force-recreate mockauth
```

```
docker compose run --rm console
```

https://github.com/navikt/mock-oauth2-server/issues/674


## TODO

* Add a script to get tokens for all six users, writing them to files and environment variables.
* Write the README instructions.
