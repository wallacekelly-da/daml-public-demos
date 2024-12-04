# PQS and mock-oauth2-server

## Purpose

This demonstrates a minimal instance of PQS up-and-running with
[mock-oauth2-server](https://github.com/navikt/mock-oauth2-server).
This demo is for initial investigation -- not a full production configuration.

```mermaid
flowchart LR
    mydomain <--> participant1
    mockauth --> participant1
    mockauth --> pqs
    participant1 --> pqs
    subgraph PQS
    pqs --> pqs_db
    end
    pqs_db --> adminer
```

## Overview

### Canton Nodes

The demo instantiates a Canton domain and participant node. The participant node's [boostrap script](./configs/participant1.canton) does the following:

* Connects to the domain.
* Uploads a DAR file.
* Creates three parties: `alice`, `bob`, `charlie`.
* Creates one user: `pqs` (with read-as permissions to `alice` and `bob`).
* Creates three contracts -- one for each party.
* Creates a configuration files for the mock-oauth2-server.
* Creates a configuration file for the PQS.

The participant node is [configured](./configs/participant1.conf) with auth-services:

```
auth-services = [
    {
        type = jwt-rs-256-jwks
        url = "http://mockauth/mockissuer/jwks"
    }
]
```

### mock-oauth2-server

The mock-oauth2-server is configured to support [three access token formats](https://docs.daml.com/app-dev/authorization.html#access-token-formats). The format is determined by the value of `mockTokenType` when the token is requested.
The audience and scope format tokens set `sub: pqs` to login as the `pqs` user. The custom access token explicitly sets the `readAs` permissions to the `alice` and `bob` parties. _Currently, the PQS does not support custom access tokens._
The mock-oauth2-server is on host port 8081.

Here is a sample `curl` command for retrieving an audience token:

```
curl --location 'http://localhost:8081/mockissuer/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=client_credentials' \
    --data-urlencode 'client_id=ignored-by-mock' \
    --data-urlencode 'client_secret=ignored-by-mock' \
    --data-urlencode 'audience=participant1::122038...' \
    --data-urlencode 'scope=daml_ledger_api' \
    --data-urlencode 'mockTokenType=audience'
```

### PQS

The demo instantiates a PQS instance configured to retrieve tokens from the mock-oauth2-server.

The demo instantiates an [Adminer](https://www.adminer.org/) instance as a simple (and optional) way to explore the PQS's Postgres database.
Adminer is served on host port 8080.

As an alternative to Adminer, you can use psql, pgAdmin, DBeaver, or whatever SQL client you prefer. The PQS's Postgres database is available on port 5432, database `postgres`, user `postgres`, password `postgres`.

Here is a sample query:

```
select payload->>'desc' as asset, signatories from active();
```

## Sample Tokens

Audience token (preferred)

```
eyJraWQiOiJtb2NraXNzdWVyIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJwcXMiLCJhdWQiOiJodHRwczovL2RhbWwuY29tL2p3dC9hdWQvcGFydGljaXBhbnQvcGFydGljaXBhbnQxOjoxMjIwMzEzMDdlY2JkMTk3NzhhZmZiZjhjMzEwM2RiM2NiOWZkNzc4ZTZlZGE4YjEzOTBlNDNlMGViYWFiNmJjNzIxNyIsIm5iZiI6MTczMjM3NzMxNCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL21vY2tpc3N1ZXIiLCJleHAiOjE3MzIzODA5MTQsImlhdCI6MTczMjM3NzMxNCwianRpIjoiOGIxYjg0OGItZWYwOC00NzNmLTg4NWYtNzBiYzk1NmZlNjhhIn0.BEnzK10xy1wS06RnZ-ewNHgmfu4OVuE8S_pLce740dvlqHBbNC_doZu9LBgKTcd83jcObBDpeOeTBat4LmxImdaKPsq0Wv1Koj3k8pE2PVuSG4NNoDZYHPDPygK_WgGJaFIR9Y42HtMWwMKr2eL6AOFd5Ws5i0rsWHbO3JjnIcOoQWTgaOG6Zbecaq93SBDeQhb3FFG4UpntCXmjSyZA41-voz471v8thYdHarusXSLyoHDEKWLsN39CcRAMZ-SI8oKNKM1cr3LFZAU83uKQUYwMInDv-Oy7_SSkFdtskRQBh1S1wkvfvq11xPQyXJUnxfd_Y-2qvqedymnZ0WgwlA
```

```
{
  "sub": "pqs",
  "aud": "https://daml.com/jwt/aud/participant/participant1::122031307ecbd19778affbf8c3103db3cb9fd778e6eda8b1390e43e0ebaab6bc7217",
  "nbf": 1732377314,
  "iss": "http://localhost:8081/mockissuer",
  "exp": 1732380914,
  "iat": 1732377314,
  "jti": "8b1b848b-ef08-473f-885f-70bc956fe68a"
}
```

Scope token

```
eyJraWQiOiJtb2NraXNzdWVyIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJwcXMiLCJhdWQiOiJwYXJ0aWNpcGFudDE6OjEyMjAzMTMwN2VjYmQxOTc3OGFmZmJmOGMzMTAzZGIzY2I5ZmQ3NzhlNmVkYThiMTM5MGU0M2UwZWJhYWI2YmM3MjE3IiwibmJmIjoxNzMyMzc3Mjc5LCJzY29wZSI6ImRhbWxfbGVkZ2VyX2FwaSIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9tb2NraXNzdWVyIiwiZXhwIjoxNzMyMzgwODc5LCJpYXQiOjE3MzIzNzcyNzksImp0aSI6IjEyYjcwMjVlLTBhZjctNGMyNy04MTYyLTY1YTg2NzhmN2M5NCJ9.Gch01BKXeIrmcn1BQTo1sg3AMKneAmvmRCImlwHab92Wj-b0l-4TlNnHHT2wDE0iEebF04YL3jL1Hj0HXJXUr8zBdtn86a1MFJzrNoZ36u5UInDq3HYWvF6DW6DBojN--dYeIcEzdeHjyn2FeUWBee8dUzDivWGzSU5eWLiHmSOwbVIdkWSKeXpJ09YTDuh8MB0r_cBe33HXEpQJ1DGx-rzIhxgel7Ij8M17eS6kmHAnsKY3tHdtUlVIw_CD0lwRJFNklhpncKF9BhX9dKDTTe5u4R4RN5mDAEEZ28uOgvMf3kZ7A5Xm-y8yn_yfnFtUf-3MgjvyVgzzks7GpUVadw
```

```
{
  "sub": "pqs",
  "aud": "participant1::122031307ecbd19778affbf8c3103db3cb9fd778e6eda8b1390e43e0ebaab6bc7217",
  "nbf": 1732377279,
  "scope": "daml_ledger_api",
  "iss": "http://localhost:8081/mockissuer",
  "exp": 1732380879,
  "iat": 1732377279,
  "jti": "12b7025e-0af7-4c27-8162-65a8678f7c94"
}
```

Custom access token (not supported by PQS)

```
eyJraWQiOiJtb2NraXNzdWVyIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJodHRwczovL2RhbWwuY29tL2xlZGdlci1hcGkiOnsiYWRtaW4iOmZhbHNlLCJhY3RBcyI6W10sInJlYWRBcyI6WyJhbGljZTo6MTIyMDMxMzA3ZWNiZDE5Nzc4YWZmYmY4YzMxMDNkYjNjYjlmZDc3OGU2ZWRhOGIxMzkwZTQzZTBlYmFhYjZiYzcyMTciLCJib2I6OjEyMjAzMTMwN2VjYmQxOTc3OGFmZmJmOGMzMTAzZGIzY2I5ZmQ3NzhlNmVkYThiMTM5MGU0M2UwZWJhYWI2YmM3MjE3Il19LCJuYmYiOjE3MzIzNzcyMTksImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9tb2NraXNzdWVyIiwiZXhwIjoxNzMyMzgwODE5LCJpYXQiOjE3MzIzNzcyMTksImp0aSI6IjYwODA2MDE1LTQ1NGItNDdhNy1hNTBhLTBiMTU5Mjg2MjA4MCJ9.IIukjMFbkYlrvznMjH3TdHVw6v1SmUf7Q_mek9k2Rivqi5Jncp1NFwHYrA0KvauMbANiHLLbXl1C6izXWyHnFOer5-Qjpb9FZgFBXnTzfzzM-6tKlDI-K5nyO7Zv1YGvGcMHVPDtwBJLmubgPL5qheM67WYW7jxGpsZsBJZLataLpManGw0rU9VJfVLt1U3btBTqGd_-wFXalMBaNTS5iMgXDOEF474f_lHIdKxU75xKJgTAZf-UkLAdTu1owV7jm15jEcR1NcHfaeUFHxUEEQeYTuYArVOxCXdGTQ2x0OOi3B0i5vRZhR8V8EILUsDknU8veYzpxITsu8MAV3g8jQ
```

```
{
  "https://daml.com/ledger-api": {
    "admin": false,
    "actAs": [],
    "readAs": [
      "alice::122031307ecbd19778affbf8c3103db3cb9fd778e6eda8b1390e43e0ebaab6bc7217",
      "bob::122031307ecbd19778affbf8c3103db3cb9fd778e6eda8b1390e43e0ebaab6bc7217"
    ]
  },
  "nbf": 1732377219,
  "iss": "http://localhost:8081/mockissuer",
  "exp": 1732380819,
  "iat": 1732377219,
  "jti": "60806015-454b-47a7-a50a-0b1592862080"
}
```

## Running the Demo

Checkout the demo:

```
git clone https://github.com/wallacekelly-da/daml-public-demos.git --branch pqs-with-mock-oauth --single-branch pqs-with-mock-oauth

cd pqs-with-mock-oauth
```

Get the required images:

```
docker login digitalasset-docker.jfrog.io

docker compose pull
```

Run the demo:

```
daml build

docker compose up --detach adminer

# wait for services to be up-and-running

# explore the database (http://localhost:8080/)
# using the screenshots below for Adminer login
# and for a sample query.

# select payload->>'desc' as asset, signatories from active();

# Notice that the contracts for alice and bob
# have been copied into the PQS database.

docker compose down
```

## Adminer Screenshots

![](adminer-login.png)

![](adminer-call.png)


## Additional Commands

Open a `psql` console to the PQS:

```
docker run -it --rm --network pqs-with-mock-oauth_default  --volume ./:/host/ postgres:15 psql --host=pqs_db --username=postgres postgres
```

(password: `postgres`)

```
postgres=# select payload->>'desc' as asset, signatories from active();

     asset     |                                  signatories
---------------+-------------------------------------------------------------------------------
 Alice's asset | {alice::122013230fcf69bd28f6d65778f11f2e841b69b066df90a606bd8bf43c2e456062f8}
 Bob's asset   | {bob::122013230fcf69bd28f6d65778f11f2e841b69b066df90a606bd8bf43c2e456062f8}
(2 rows)
```
