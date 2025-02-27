# mock-oauth2-sandbox

## Purpose

Start Sandbox pointed at an instance of
[mock-oauth2-server](https://github.com/navikt/mock-oauth2-server).
This is helpful for testing and diagnostics of auth-related issues.

## Steps

1. **Clone** this code.

   ```
   git clone \
     https://github.com/wallacekelly-da/daml-public-demos.git \
       --single-branch \
       --depth 1 \
       --branch mock-oauth2-sandbox \
       mock-oauth2-sandbox
   ```

1. **Start** the mock-oauth2-server:

    ```
    docker run -it --rm \
      --publish 8080:8080 \
      --env LOG_LEVEL=DEBUG \
      --env JSON_CONFIG_PATH=/host/mockauth.json \
      --volume ./:/host/ \
      ghcr.io/navikt/mock-oauth2-server:2.1.10
    ```

    Confirm its health:

    ```
    curl http://localhost:8080/isalive
    ```

1. **Start** Sandbox:

    ```
    daml sandbox \
      --log-level-canton DEBUG \
      --config sandbox.conf
    ```

    Confirm its health:

    ```
    grpcurl -plaintext localhost:6865 grpc.health.v1.Health.Check
    ```

1. **Get** the participant id from the Admin API:

    <!-- Canton 2.x -->
    ```
    export PARTICIPANT_ID=$( \
      grpcurl -plaintext localhost:6866 \
      com.digitalasset.canton.health.admin.v0.StatusService.Status \
        | jq -r '.success.id' )
    echo $PARTICIPANT_ID
    ```

    <!-- Canton 3.x -->
    <!-- ```
    export PARTICIPANT_ID=$( \
      grpcurl -plaintext localhost:6866 \
        com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus \
          | jq -r '.status.commonStatus.uid' )
    echo $PARTICIPANT_ID
    ``` -->

1. **Show** that a token is required to list the packages:

    ```
    daml canton-console \
      -C canton.features.enable-testing-commands=yes
    ```

    The following will fail with `UNAUTHENTICATED`.

    ```
    sandbox.ledger_api.packages.list()
    ```

    ```
    exit
    ```

1. **Get** a JWT token for the `participant_admin` user:

    ```
    export ADMIN_TOKEN=$(\
      curl --silent \
        --location localhost:8080/mockauth/token \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'grant_type=client_credentials' \
        --data-urlencode 'client_id=participant_admin' \
        --data-urlencode 'client_secret=secret' \
        --data-urlencode 'participant_id='"$PARTICIPANT_ID" \
          | jq -r '.access_token')
    echo $ADMIN_TOKEN
    ```

1. **List** the packages successfully _with a token_:

    ```
    daml canton-console \
      -C canton.features.enable-testing-commands=yes \
      -C canton.remote-participants.sandbox.token=$ADMIN_TOKEN
    ```

    The following now succeeds:

    ```
    sandbox.ledger_api.packages.list()
    ```
