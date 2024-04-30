# Daml Public Demos by Wallace Kelly

Each demo is in its own Git branch.

## Purpose

This demo configures an instance of [mock-oauth2-server](https://github.com/navikt/mock-oauth2-server)
as the authorization service for a Canton instance in a Docker Compose network.

This demo will enable you to:

* Experiment with various auth-related Canton configuration options.
* Experiment with user access tokens and Canton User Management.
* Examine the response of the Ledger API endpoints with various JWT claims.
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
   docker compose up --detach canton
   ```
3. *



```
grpcurl -plaintext localhost:5003 com.daml.ledger.api.v1.admin.PartyManagementService/GetParticipantId \
  | jq -r '.participant_id'
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