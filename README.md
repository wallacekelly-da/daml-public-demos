# Daml Public Demos by Wallace Kelly

Each demo is in its own Git branch.

## Purpose

This demo configures an instance of [mock-oauth2-server](https://github.com/navikt/mock-oauth2-server)
as the authorization service for a Canton participant in a Docker Compose network.

This demo will enable you to:

* Experiment with various auth-related Canton configuration options.
* Experiment with the relationship between JWT tokens and Canton User Management.
* Examine the response of the Ledger API endpoints with and without various JWT claims.
* Consider testing JWT-dependent scripts in a local environment.
* Consider building JWT-dependent, automated integration tests.

## Prerequisites

* Docker Desktop

## Steps

1. Peruse the Canton configuration.
   * [canton.conf](./configs/canton.conf) defines three nodes (`mydomain`, `participant1`, and `participant2`)
   * Notice that `participant1` does _not_ require the token; `participant2` _does_ require a token.
   * [bootstrap.canton](./configs/bootstrap.canton) allocates three parties and three users per participant node.

2. Peruse the mock-oauth2-server [configuration](./configs/mockauth.json). Notice the four mappings based on `mock_token_type`.

3. Start the Docker Compose network.
   ```
   docker compose up --detach participant1 participant2
   ```

4. Start a console for running the remaining sample commands.
   ```
   docker compose run --rm --build console
   ```

5. Create a variety of tokens:

   [Audience-based token](https://docs.daml.com/app-dev/authorization.html#audience-based-tokens):

   ```
   curl -s http://mockauth/mockissuer/token \
      -d grant_type=client_credentials \
      -d client_id=ignored \
      -d client_secret=ignored \
      -d mock_token_type=audience \
      -d participant=`cat "./configs/participant2.id"` \
      -d sub=wallace \
      | jq -r '.access_token' \
      > audience.token
   ```

   [Scope-based token](https://docs.daml.com/app-dev/authorization.html#scope-based-tokens):

   ```
   curl -s http://mockauth/mockissuer/token \
      -d grant_type=client_credentials \
      -d client_id=ignored \
      -d client_secret=ignored \
      -d mock_token_type=scope \
      -d participant=`cat "./configs/participant2.id"` \
      -d sub=david \
      | jq -r '.access_token' \
      > scope.token
   ```

   [Custom claims token](https://docs.daml.com/app-dev/authorization.html#custom-daml-claims-access-tokens):

   ```
   curl -s http://mockauth/mockissuer/token \
      -d grant_type=client_credentials \
      -d client_id=ignored \
      -d client_secret=ignored \
      -d mock_token_type=custom \
      -d actAs=`cat "./configs/participant2-operator.id"` \
      -d readAs=`cat "./configs/participant2-operator.id"` \
      | jq -r '.access_token' \
      > custom.token
   ```

   [Custom admin claims token](https://docs.daml.com/app-dev/authorization.html#custom-daml-claims-access-tokens):

   ```
   curl -s http://mockauth/mockissuer/token \
      -d grant_type=client_credentials \
      -d client_id=ignored \
      -d client_secret=ignored \
      -d mock_token_type=custom-admin \
      | jq -r '.access_token' \
      > admin.token
   ```

6. View the JWT:

   ```
   cat audience.token
   ```

   ```
   cat audience.token \
      | jq -R 'split(".") \
      | .[0], .[1] \
      | @base64d \
      | fromjson'
   ```

7. Use the tokens with [Daml Assistant](https://docs.daml.com/tools/assistant.html):

   _The following will fail:_
   ```
   daml ledger list-parties --host participant2 --port 4001
   ```

   _The following will succeed:_
   ```
   daml ledger list-parties --host participant2 --port 4001 \
      --access-token-file audience.token
   ```

8. Use the tokens with [grpcurl](https://github.com/fullstorydev/grpcurl/blob/master/README.md):

   _The following will fail:_
   ```
   TOKEN=$(cat scope.token)

   grpcurl --plaintext \
      -H "Authorization: Bearer ${TOKEN}" \
      participant2:4001 \
      com.daml.ledger.api.v1.admin.UserManagementService/ListUsers \
      | jq '.users[].id'
   ```

   _The following will succeed:_
   ```
   TOKEN=$(cat audience.token)

   grpcurl --plaintext \
      -H "Authorization: Bearer ${TOKEN}" \
      participant2:4001 \
      com.daml.ledger.api.v1.admin.UserManagementService/ListUsers \
      | jq '.users[].id'
   ```

9. Use the tokens with [Daml Script](https://docs.daml.com/daml-script/index.html):

   _The following will fail:_
   ```
   daml ledger upload-dar .daml/dist/console-1.0.0.dar \
     --host participant2 --port 4001
     --access-token-file custom.token
   ```

   _The following will succeed:_
   ```
   daml ledger upload-dar .daml/dist/console-1.0.0.dar \
     --host participant2 --port 4001 \
     --access-token-file admin.token
   ```

   _The following will fail:_
   ```
   daml script --dar .daml/dist/console-1.0.0.dar \
      --script-name Main:createContracts \
      --input-file configs/participant2-operator.json \
      --ledger-host participant2 \
      --ledger-port 4001
      --access-token-file admin.token
   ```

   _The following will succeed:_
   ```
   daml script --dar .daml/dist/console-1.0.0.dar \
      --script-name Main:createContracts \
      --input-file configs/participant2-operator.json \
      --ledger-host participant2 \
      --ledger-port 4001 \
      --access-token-file custom.token
   ```

## References

* [Canton User Manual, JWT Authorization](https://docs.daml.com/canton/usermanual/apis.html#jwt-authorization)
* [Canton App Development, Authorization](https://docs.daml.com/app-dev/authorization.html)
* [How to decode a JSON Web Token in Postman](https://medium.com/@jeff.heienickle/how-to-decode-a-json-web-token-in-postman-5312b3434462)
* [Decoding JSON Web Tokens (JWTs) from the Linux command line](https://prefetch.net/blog/2020/07/14/decoding-json-web-tokens-jwts-from-the-linux-command-line/)
