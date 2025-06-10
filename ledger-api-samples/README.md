# Ledger API Samples

A collection of `grpcurl`, `curl`, and `websocat` commands.

## Prequisites

* [jq](https://jqlang.org/download/)
* [grpcurl](https://github.com/fullstorydev/grpcurl)
* [websocat](https://github.com/vi/websocat)

## References

* [Ledger API](https://docs.digitalasset.com/build/3.3/reference/lapi-proto-docs/)
* [Ledger JSON API](https://docs.digitalasset.com/build/3.3/explanations/json-api/)

## Sandbox Setup

**Get** the collection (bash, pwsh):

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch ledger-api-samples \
  ledger-api-samples
```

```
git clone `
  https://github.com/wallacekelly-da/daml-public-demos.git `
  --single-branch `
  --depth 1 `
  --branch ledger-api-samples `
  ledger-api-samples
```

**Change** into the working folder.

```
cd ledger-api-samples/ledger-api-samples
```


Optionally **start** the mock OAuth service (bash, pwsh):

```
docker run -it --rm \
  --publish 8080:8080 \
  --env LOG_LEVEL=DEBUG \
  --env JSON_CONFIG_PATH=/host/mockauth.json \
  --volume ./configs/:/host/ \
  ghcr.io/navikt/mock-oauth2-server:2.1.10
```

```
docker run -it --rm `
  --publish 8080:8080 `
  --env LOG_LEVEL=DEBUG `
  --env JSON_CONFIG_PATH=/host/mockauth.json `
  --volume ./configs:/host/ `
  ghcr.io/navikt/mock-oauth2-server:2.1.10
```

Optionally **confirm** the mock OAuth service is working (bash, pwsh):

```
curl --silent \
  --location localhost:8080/mockauth/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=client_credentials' \
  --data-urlencode 'client_id=participant_admin' \
  --data-urlencode 'client_secret=secret' \
  --data-urlencode 'participant_id=participant_id_here' \
| jq --raw-output \
  '.access_token
  | split(".")
  | .[1]
  | @base64d' \
| jq
```

```
curl --silent `
  --location localhost:8080/mockauth/token `
  --header 'Content-Type: application/x-www-form-urlencoded' `
  --data-urlencode 'grant_type=client_credentials' `
  --data-urlencode 'client_id=participant_admin' `
  --data-urlencode 'client_secret=secret' `
  --data-urlencode 'participant_id=participant_id_here' `
| jq --raw-output `
  '.access_token
  | split(".")
  | .[1]
  | @base64d' `
| jq
```

The result should look something like this:

```json
{
  "sub": "participant_admin",
  "aud": "https://daml.com/jwt/aud/participant/participant_id_here",
  "nbf": 1744033119,
  "iss": "http://localhost:8080/mockauth",
  "exp": 317320036719,
  "iat": 1744033119,
  "jti": "501e80b2-74eb-46ee-858b-152e63859160"
}
```

**Start** the ledger:

```
daml sandbox --config configs/sandbox3.conf --log-level-canton DEBUG
```


## Environment Variables

**Set** environment variables (bash, pwsh):

```
export LEDGER_HOST=localhost
export LEDGER_PORT=6865
export LEDGER_ADMIN=6866
export LEDGER_JSON=7575
export ALICE_USERID=alice
export BOB_USERID=bob
```

```
$LEDGER_HOST = "localhost"
$LEDGER_PORT = "6865"
$LEDGER_ADMIN = "6866"
$LEDGER_JSON = "7575"
$ALICE_USERID = "alice"
$BOB_USERID = "bob"
```

**Get** the participant id from the Admin API (bash, pwsh):

```
export PARTICIPANT_ID=$(
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" \
    com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus \
  | jq --raw-output '.status.commonStatus.uid'
); echo ${PARTICIPANT_ID}
```

```
$PARTICIPANT_ID=$( `
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
    com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus `
  | jq --raw-output '.status.commonStatus.uid' `
); echo ${PARTICIPANT_ID}
```

**Set** tokens to a default, Sandbox token (bash, pwsh):

```
export ADMIN_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJleHAiOm51bGwsImlzcyI6bnVsbCwic2NvcGUiOiJFeHBlY3RlZFRhcmdldFNjb3BlIiwic3ViIjoicGFydGljaXBhbnRfYWRtaW4ifQ.8bABNm1t718TuJXwRQOF2gXOclrL38t0uCmWkIT7Pcg
export ALICE_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJleHAiOm51bGwsImlzcyI6bnVsbCwic2NvcGUiOiJFeHBlY3RlZFRhcmdldFNjb3BlIiwic3ViIjoicGFydGljaXBhbnRfYWRtaW4ifQ.8bABNm1t718TuJXwRQOF2gXOclrL38t0uCmWkIT7Pcg
```

```
$ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJleHAiOm51bGwsImlzcyI6bnVsbCwic2NvcGUiOiJFeHBlY3RlZFRhcmdldFNjb3BlIiwic3ViIjoicGFydGljaXBhbnRfYWRtaW4ifQ.8bABNm1t718TuJXwRQOF2gXOclrL38t0uCmWkIT7Pcg"
$ALICE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJleHAiOm51bGwsImlzcyI6bnVsbCwic2NvcGUiOiJFeHBlY3RlZFRhcmdldFNjb3BlIiwic3ViIjoicGFydGljaXBhbnRfYWRtaW4ifQ.8bABNm1t718TuJXwRQOF2gXOclrL38t0uCmWkIT7Pcg"
```

**Generate** a mocked JWT token for the `participant_admin` user (bash, pwsh):

```
export ADMIN_TOKEN=$( \
  curl --silent \
    --location localhost:8080/mockauth/token \
    --header "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "grant_type=client_credentials" \
    --data-urlencode "client_id=participant_admin" \
    --data-urlencode "client_secret=secret" \
    --data-urlencode "participant_id=${PARTICIPANT_ID}" \
  | jq --raw-output '.access_token' \
);

echo ${ADMIN_TOKEN} \
| jq --raw-input --raw-output \
    'split(".")
    | .[1]
    | @base64d' \
| jq
```

```
$ADMIN_TOKEN=$( `
  curl --silent `
    --location localhost:8080/mockauth/token `
    --header "Content-Type: application/x-www-form-urlencoded" `
    --data-urlencode "grant_type=client_credentials" `
    --data-urlencode "client_id=participant_admin" `
    --data-urlencode "client_secret=secret" `
    --data-urlencode "participant_id=${PARTICIPANT_ID}" `
  | jq --raw-output '.access_token' `
);

echo ${ADMIN_TOKEN} `
| jq --raw-input --raw-output `
    'split(".")
    | .[1]
    | @base64d' `
| jq
```

## Service health

**Check** the health of the Ledger API (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
  grpc.health.v1.Health.Check
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  grpc.health.v1.Health.Check
```

**Check** the readiness of the Ledger JSON API:

```
curl "http://${LEDGER_HOST}:${LEDGER_JSON}/readyz"
```

## Parties and users

**Create** a party with the Ledger API: (bash, pwsh)

```
echo '
{
  "party_id_hint": "'${ALICE_USERID}'"
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.admin.PartyManagementService.AllocateParty \
  | jq
```

```
@"
{
  "party_id_hint": "${ALICE_USERID}"
}
"@ | grpcurl -plaintext -d `@ `
      -H "Authorization: Bearer ${ADMIN_TOKEN}" `
      "${LEDGER_HOST}:${LEDGER_PORT}" `
      com.daml.ledger.api.v2.admin.PartyManagementService.AllocateParty `
   | jq
```

**Create** a party with the Ledger JSON API: (bash, pwsh)

```
echo '
{
  "partyIdHint": "'${BOB_USERID}'",
  "displayName": "'${BOB_USERID}'",
  "identityProviderId": ""
}
' | jq --compact-output \
  | curl --silent --json @- \
      --oauth2-bearer ${ADMIN_TOKEN} \
      "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" \
  | jq
```

```
@"
{
  "partyIdHint": "${BOB_USERID}",
  "displayName": "${BOB_USERID}",
  "identityProviderId": ""
}
"@ | jq --compact-output `
   | curl --silent --json `@- `
      --oauth2-bearer ${ADMIN_TOKEN} `
      "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" `
   | jq
```

**Get** the local parties using Ledger API (bash, pwsh):

```
grpcurl -plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  ${LEDGER_HOST}:${LEDGER_PORT} \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
| jq \
    '.party_details[]
    | select(.is_local)'
```

```
grpcurl -plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  ${LEDGER_HOST}:${LEDGER_PORT} \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
| jq `
    '.party_details[]
    | select(.is_local)'
```

**Get** the local parties using Ledger JSON API (bash, pwsh):

```
curl --silent \
  --oauth2-bearer ${ADMIN_TOKEN} \
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" \
| jq --raw-output \
    '.partyDetails[]
    | select (.isLocal == true)
    | .party'
```

```
curl --silent `
  --oauth2-bearer ${ADMIN_TOKEN} `
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" `
| jq --raw-output `
    '.partyDetails[]
    | select (.isLocal == true)
    | .party'
```

**List** local parties using Daml Assistant (bash, pwsh):

```
echo ${ADMIN_TOKEN} > admin.jwt; \
daml ledger list-parties \
  --host ${LEDGER_HOST} \
  --port ${LEDGER_PORT} \
  --access-token-file admin.jwt \
  | grep 'isLocal = True'
```

```
daml ledger list-parties --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | Select-String -Pattern 'isLocal = True'
```

**Get** a specific party using Ledger API (bash, pwsh):

```
export ALICE_PARTY=$(
  grpcurl -plaintext \
    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
    ${LEDGER_HOST}:${LEDGER_PORT} \
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
  | jq --raw-output \
      '.party_details[]
      | select(.is_local)
      | select(.party | startswith("'${ALICE_USERID}::'"))
      | .party' \
); echo ${ALICE_PARTY}
```

```
export ALICE_PARTY=$(
  grpcurl -plaintext `
    -H "Authorization: Bearer ${ADMIN_TOKEN}" `
    ${LEDGER_HOST}:${LEDGER_PORT} `
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties `
  | jq --raw-output `
      '.party_details[]
      | select(.is_local)
      | select(.party | startswith("${ALICE_USERID}::"))
      | .party' `
  ); echo ${ALICE_PARTY}
```

**Get** a specific party using Ledger JSON API (bash, pwsh):

```
export BOB_PARTY=$(
  curl --silent \
    --oauth2-bearer ${ADMIN_TOKEN} \
    "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" \
    | jq --raw-output \
        '.partyDetails[] | select(.isLocal) | select(.party | startswith("'${BOB_USERID}'::")) | .party'
); echo ${BOB_PARTY}
```

```
$BOB_PARTY=$( `
  curl --silent `
    --oauth2-bearer ${ADMIN_TOKEN} `
    "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" `
    | jq --raw-output ".partyDetails[] | select(.isLocal) | select(.party | startswith(`"${ALICE_USERID}::`")) | .party" `
); echo ${BOB_PARTY}
```

**Create** a user with the Ledger API (bash, pwsh):

```
echo '
{
  "user": {
    "id": "'${ALICE_USERID}'",
    "primary_party": "'${ALICE_PARTY}'"
  }
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.admin.UserManagementService.CreateUser \
  | jq
```

```
@"
{
  "user": {
    "id": "${ALICE_USERID}",
    "primary_party": "${ALICE_PARTY}"
  }
}
"@ | grpcurl -plaintext -d `@ `
      -H "Authorization: Bearer ${ADMIN_TOKEN}" `
      "${LEDGER_HOST}:${LEDGER_PORT}" `
      com.daml.ledger.api.v2.admin.UserManagementService.CreateUser `
   | jq
```

**Create** a user with the Ledger JSON API (bash, pwsh):

```
echo '
{
  "rights" : [
    {
      "kind": {
        "CanActAs" : {
          "value": {
            "party" : "'${BOB_PARTY}'"
          }
        }
      }
    }
  ],
  "user" : {
    "id" : "'${BOB_USERID}'",
    "identityProviderId" : "",
    "isDeactivated" : false,
    "metadata" : null,
    "primaryParty" : "'${BOB_PARTY}'"
  }
}
' | jq --compact-output \
  | curl --silent --json @- \
      --oauth2-bearer ${ADMIN_TOKEN} \
      "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users" \
  | jq
```

TODO: pwsh

**List** the users with Ledger API (bash, pwsh):

```
grpcurl --plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  ${LEDGER_HOST}:${LEDGER_PORT} \
  com.daml.ledger.api.v2.admin.UserManagementService/ListUsers \
| jq --raw-output '
    .users[]
    | { id, primary_party }'
```

TODO: pwsh

**List** the users with Ledger JSON API (bash, pwsh):

```
curl --silent \
  --oauth2-bearer ${ADMIN_TOKEN} \
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users" \
  | jq --raw-output '.users[] | { id, primaryParty }'
```

```
curl --silent `
  --oauth2-bearer ${ADMIN_TOKEN} `
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users" `
  | jq --raw-output '.users[] | { id, primaryParty }'
```

**Grant** a user's rights using Ledger API (bash, pwsh):

```
echo '
{
  "userId": "'${ALICE_USERID}'",
  "identityProviderId": "",
  "rights": [
    {
      "can_act_as": {
        "party": "'${ALICE_PARTY}'"
      }
    }
  ]
}
' | jq --compact-output \
  | grpcurl --plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.admin.UserManagementService.GrantUserRights \
  | jq
```

TODO: pwsh

**Grant** a user's rights using Ledger JSON API (bash, pwsh):

```
echo '
{
  "userId": "'${BOB_USERID}'",
  "identityProviderId": "",
  "rights": [
    {
      "kind": {
        "CanReadAs" : {
          "value": {
            "party" : "'${BOB_PARTY}'"
          }
        }
      }
    }
  ]
}
' | jq --compact-output \
  | curl --silent --json @- \
      --oauth2-bearer ${ADMIN_TOKEN} \
      "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users/${BOB_USERID}/rights" \
  | jq
```

TODO: pwsh

**List** a user's rights using Ledger API (bash, pwsh):

```
echo '
{
  "user_id": "'${ALICE_USERID}'"
}
' | jq --compact-output \
  | grpcurl --plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.admin.UserManagementService/ListUserRights \
  | jq
```

TODO: pwsh

**List** a user's rights using Ledger JSON API (bash, pwsh):

```
curl --silent \
  --oauth2-bearer ${ADMIN_TOKEN} \
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users/${BOB_USERID}/rights" \
  | jq
```

```
curl --silent `
  --oauth2-bearer ${ADMIN_TOKEN} `
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users/${BOB_USERID}/rights" `
  | jq
```

**Generate** a mocked JWT token for a user (bash, pwsh):

```
export ALICE_TOKEN=$( \
  curl --silent \
    --location localhost:8080/mockauth/token \
    --header "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "grant_type=client_credentials" \
    --data-urlencode "client_id=${ALICE_USERID}" \
    --data-urlencode "client_secret=secret" \
    --data-urlencode "participant_id=${PARTICIPANT_ID}" \
      | jq --raw-output '.access_token');

echo ${ALICE_TOKEN} \
  | jq --raw-input --raw-output \
    'split(".")
    | .[1]
    | @base64d' \
  | jq
```

```
$ALICE_TOKEN=$( `
  curl --silent `
    --location localhost:8080/mockauth/token `
   --header "Content-Type: application/x-www-form-urlencoded" `
    --data-urlencode "grant_type=client_credentials" `
    --data-urlencode "client_id=${ALICE_USERID}" `
    --data-urlencode "client_secret=secret" `
    --data-urlencode "participant_id=${PARTICIPANT_ID}" `
      | jq --raw-output '.access_token'); echo ${ALICE_TOKEN}

echo ${ALICE_TOKEN} `
  | jq --raw-input --raw-output `
    'split(".")
    | .[1]
    | @base64d' `
  | jq
```

## DARs and Packages

**Encode** a DAR file as a base64 string (bash, pwsh):

```
export DAR_FILE=$(base64 --wrap 0 \
    .daml/dist/ledger-api-samples-0.0.1.dar \
  )
```

TODO: pwsh

**Upload** a DAR file using the Ledger API (bash, pwsh):

```
echo '
{
  "dar_file": "'${DAR_FILE}'"
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      ${LEDGER_HOST}:${LEDGER_PORT} \
      com.daml.ledger.api.v2.admin.PackageManagementService.UploadDarFile
```

TODO: pwsh

**Upload** a DAR file using the Ledger JSON API (bash, pwsh):

TODO: bash

```

```

TODO: pwsh

**Get** the main package id for a given DAR (bash, pwsh):

```
export PACKAGE_ID=$(
  daml damlc inspect-dar --json \
    .daml/dist/ledger-api-samples-0.0.1.dar \
    | jq --raw-output .main_package_id
  ); echo ${PACKAGE_ID}
```

```
$PACKAGE_ID=$( `
  daml damlc inspect-dar --json `
    .daml/dist/ledger-api-samples-0.0.1.dar `
    | jq --raw-output .main_package_id `
  ); echo ${PACKAGE_ID}
```

**List** all the packages using Ledger API (bash, pwsh):

```
grpcurl -plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.PackageService.ListPackages
```

```
grpcurl -plaintext `
  -H "Authorization: Bearer ${ADMIN_TOKEN}" `
  "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.PackageService.ListPackages
```

**List** all the packages using Ledger JSON API (bash, pwsh):

```
curl --silent \
  --oauth2-bearer ${ADMIN_TOKEN} \
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/packages" \
  | jq
```

```
curl --silent `
  --oauth2-bearer ${ADMIN_TOKEN} `
  "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/packages" `
  | jq
```

**List** the uploaded package using Ledger API (bash, pwsh):

```
grpcurl -plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.admin.PackageManagementService.ListKnownPackages \
  | grep -B 1 -A 4 ${PACKAGE_ID}
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.admin.PackageManagementService.ListKnownPackages `
  | Select-String -Pattern ${PACKAGE_ID} -Context 1,4
```

**List** known packages and DAR file names using Daml Assistant (bash, pwsh):

```
echo ${ADMIN_TOKEN} > admin.jwt;
daml packages list \
    --host ${LEDGER_HOST} \
    --port ${LEDGER_PORT} \
    --access-token-file admin.jwt \
  | sort --key 2
```

```
daml packages list --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | Sort-Object -Property { $_.Split(" ")[1] }
```

**List** the DAR hashes using the Admin API (bash, pwsh):

```
grpcurl -plaintext \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  "${LEDGER_HOST}:${LEDGER_ADMIN}" \
  com.digitalasset.canton.admin.participant.v30.PackageService.ListDars \
| jq
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.PackageService.ListDars `
| jq
```

## Create contracts

**Create** a contract using Ledger API (bash, pwsh):

```
echo '
{
  "commands": {
    "command_id": "somecommandid04",
    "act_as": [
      "'${ALICE_PARTY}'"
    ],
    "commands": [
      {
        "create": {
          "template_id": {
            "package_id": "#ledger-api-samples",
            "module_name": "Main",
            "entity_name": "Asset"
          },
          "create_arguments": {
            "fields": [
              {
                "label": "issuer",
                "value": {
                  "party": "'${ALICE_PARTY}'"
                }
              },
              {
                "label": "owner",
                "value": {
                  "party": "'${ALICE_PARTY}'"
                }
              },
              {
                "label": "name",
                "value": {
                  "text": "my asset"
                }
              },
              {
                "label": "category",
                "value": {
                  "enum": {
                    "constructor": "Stock"
                  }
                }
              }
            ]
          }
        }
      }
    ]
  }
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ALICE_TOKEN}" \
      ${LEDGER_HOST}:${LEDGER_PORT} \
      com.daml.ledger.api.v2.CommandService.SubmitAndWait
```

TODO: pwsh

**Create** a contract using Ledger JSON API (bash, pwsh):


TODO: fix the following which does not work

```
echo '
{
  "command_id": "somecommandid01",
  "user_id": "'${BOB_USERID}'",
  "act_as": [
    "'${BOB_PARTY}'"
  ],
  "read_as": [
    "'${BOB_PARTY}'"
  ],
  "commands": [
    {
      "CreateCommand": {
        "template_id": "#ledger-api-samples:Main:Asset",
        "create_arguments": {
          "issuer": "'${BOB_PARTY}'",
          "owner": "'${BOB_PARTY}'",
          "name": "my asset",
          "category": "Stock"
        }
      }
    }
  ],
  "application_id": "bob",
  "submission_id": "somesubmissionid01",
  "workflow_id": "someworkflowid",
  "domain_id": "",
  "package_id_selection_preference": [
    "'${PACKAGE_ID}'"
  ],
  "deduplication_period": {
    "DeduplicationDuration": {
      "value": {
        "seconds": 60,
        "nanos": 0,
        "unknownFields": {
          "fields": {}
        }
      }
    }
  },
  "disclosed_contracts": []
}
' | jq --compact-output \
  | curl --silent --json @- \
      --oauth2-bearer ${BOB_TOKEN} \
      "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/commands/submit-and-wait" \
  | jq
```

TODO: pwsh


## Read Contracts

**Get** the current ledger offset using Ledger API (bash, pwsh):

```
export LEDGER_OFFSET=$(
  grpcurl -plaintext \
    -H "Authorization: Bearer ${ALICE_TOKEN}" \
    "${LEDGER_HOST}:${LEDGER_PORT}" \
    com.daml.ledger.api.v2.StateService.GetLedgerEnd \
    | jq --raw-output '.offset'
  ); echo ${LEDGER_OFFSET}
```

```
$LEDGER_OFFSET=$( `
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
    com.daml.ledger.api.v2.StateService.GetLedgerEnd `
    | jq --raw-output '.offset'
  ); echo ${LEDGER_OFFSET}
```

**Get** the current ledger offset using Ledger JSON API (bash, pwsh):

```
export LEDGER_OFFSET=$(
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/ledger-end" \
      --oauth2-bearer ${ALICE_TOKEN} \
    | jq --raw-output .offset
); echo ${LEDGER_OFFSET}
```

```
$LEDGER_OFFSET=$( `
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/ledger-end" `
    | jq --raw-output .offset `
); echo ${LEDGER_OFFSET}
```

**Get** the active contracts over Ledger API (bash, pwsh):

```
echo '
{
  "verbose": "true",
  "active_at_offset": "'${LEDGER_OFFSET}'",
  "filter": {
    "filters_by_party": {
      "'${ALICE_PARTY}'": {}
    }
  }
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ALICE_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.StateService.GetActiveContracts \
  | jq \
  | tee actives.json \
  | jq
```

```
@"
{
  "verbose": "true",
  "active_at_offset": "${LEDGER_OFFSET}",
  "filter": {
    "filters_by_party": {
      "${ALICE_PARTY}": {}
    }
  }
}
"@ | `
grpcurl -plaintext -d `@ "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.StateService.GetActiveContracts `
  | Tee-Object -FilePath "actives.json"
```

**Get** the active contracts over Ledger JSON API, using websocat (bash, pwsh):

```
echo '
{
  "verbose": true,
  "activeAtOffset": "'${LEDGER_OFFSET}'",
  "filter": {
    "filtersByParty" : {
      "'${ALICE_PARTY}'": {
        "cumulative": []
      }
    }
  }
}
' | jq --compact-output \
  | websocat \
     --header "Authorization: Bearer ${ALICE_TOKEN}" \
     -n1 \
     ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts \
  | jq \
  | tee actives.json \
  | jq
```

```
@"
{
  "verbose": true,
  "activeAtOffset": "${LEDGER_OFFSET}",
  "filter": {
    "filtersByParty" : {
      "'${ALICE_PARTY}'": {
        "cumulative": []
      }
    }
  }
}
"@ | jq --compact-output `
   | websocat -n1 ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts `
   | jq `
   | Tee-Object -FilePath "actives.json"
```

**Get** the active contracts Ledger JSON API, using curl (bash, pwsh):

TODO: fix, based on
https://discuss.daml.com/t/permission-denied-claims-do-not-authorize-to-read-data-as-any-party-super-reader-wildcard/7864?u=wallacekelly

```
echo '
{
  "verbose": true,
  "activeAtOffset": "'${LEDGER_OFFSET}'",
  "filter": {
    "filtersByParty" : {
      "'${ALICE_PARTY}'": {
        "cumulative": []
      }
    }
  }
}
' | jq --compact-output \
  | curl --silent --json @- \
     --oauth2-bearer ${ALICE_TOKEN} \
     "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts" \
  | jq \
  | tee actives.json \
  | jq
```

```
@"
{
  "verbose": true,
  "activeAtOffset": "${LEDGER_OFFSET}",
  "filter": {
    "filtersByParty": {},
    "filtersForAnyParty": {
      "cumulative": [
        {
          "identifierFilter": {
            "WildcardFilter": {
              "value": {
                "includeCreatedEventBlob": true
              }
            }
          }
        }
      ]
    }
  }
}
"@ | jq --compact-output `
   | curl --silent --json `@- "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts" `
   | jq `
   | Tee-Object -FilePath "actives.json" `
   | jq
```

**Get** the active contracts for a given template using Ledger API (bash, pwsh):

```
export PACKAGE_ID=#ledger-api-samples
export MODULE_NAME=Main
export ENTITY_NAME=Asset
```

```
echo '
{
  "verbose": true,
  "active_at_offset": "'${LEDGER_OFFSET}'",
  "filter": {
    "filters_by_party": {
      "'${ALICE_PARTY}'": {
        "cumulative": [
          {
            "template_filter": {
              "template_id": {
                "package_id": "'${PACKAGE_ID}'",
                "module_name": "'${MODULE_NAME}'",
                "entity_name": "'${ENTITY_NAME}'"
              }
            }
          }
        ]
      }
    }
  }
}
' |
  grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ALICE_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.StateService.GetActiveContracts \
  | jq \
  | tee actives.json \
  | jq
```

```
$PACKAGE_ID = "#ledger-api-samples"
$MODULE_NAME = "Main"
$ENTITY_NAME = "Asset"
```

```
@"
  {
    "verbose": true,
    "active_at_offset": "${LEDGER_OFFSET}",
    "filter": {
      "filters_by_party": {
        "${ALICE_PARTY}": {
          "cumulative": [
            {
              "template_filter": {
                "template_id": {
                  "package_id": "${PACKAGE_ID}",
                  "module_name": "${MODULE_NAME}",
                  "entity_name": "${ENTITY_NAME}"
                }
              }
            }
          ]
        }
      }
    }
  }
"@ | `
grpcurl -plaintext -d `@ "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.StateService.GetActiveContracts `
  | jq
```

## Read contracts for all parties (bash, pwsh)

1. Allocate a party `bob`.
1. Create a user `bob`.
1. Add the `CanReadAsAnyParty` right.
1. Get a token for `bob`.
1. Read the contracts of `alice`, passing a token for `bob`.

```
echo '
{
  "party_id_hint": "bob"
}
' | grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ADMIN_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.admin.PartyManagementService.AllocateParty \
  | jq
```

## Subscribe to updates

**Subscribe** to updates for a party with `grpcurl` (bash, pwsh):

```
echo '
{
  "begin_exclusive": 0,
  "verbose": true,
  "filter": {
    "filters_by_party": {
      "'${ALICE_PARTY}'": { }
    }
  }
}
' |
  grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ALICE_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.UpdateService.GetUpdates \
  | jq
```

TODO: pwsh

**Subscribe** to updates of a specific template with `grpcurl` (bash, pwsh):

```
echo '
{
  "verbose": true,
  "begin_exclusive": "0",
  "filter": {
    "filters_by_party": {
      "'${ALICE_PARTY}'": {
        "cumulative": [
          {
            "template_filter": {
              "include_created_event_blob": true,
              "template_id": {
                "package_id": "'${PACKAGE_ID}'",
                "module_name": "'${MODULE_NAME}'",
                "entity_name": "'${ENTITY_NAME}'"
              }
            }
          }
        ]
      }
    }
  }
}
' |
  grpcurl -plaintext -d @ \
      -H "Authorization: Bearer ${ALICE_TOKEN}" \
      "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.UpdateService.GetUpdates \
  | jq
```


**Subscribe** to transactions for a party with `websocat` (bash, pwsh):

```
echo '
{
  "verbose": true,
  "beginExclusive": "0",
  "filter": {
    "filtersByParty" : {
      "'${ALICE_PARTY}'": {
        "cumulative": []
      }
    }
  }
}
' | jq --compact-output \
  | websocat \
     --header "Authorization: Bearer ${ALICE_TOKEN}" \
     -n1 \
     ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/updates/flats \
  | jq
```

TODO: pwsh

**Subscribe** to transactions of a template with `websocat` (bash, pwsh):

```
echo '
{
  "verbose": false,
  "beginExclusive": "0",
  "filter": {
    "filtersByParty" : {
      "'${ALICE_PARTY}'": {
        "cumulative": [
          {
            "identifierFilter": {
              "TemplateFilter": {
                "value": {
                  "includeCreatedEventBlob": false,
                  "templateId": "'${PACKAGE_ID}':'${MODULE_NAME}':'${ENTITY_NAME}'"
                }
              }
            }
          }
        ]
      }
    }
  }
}
' | jq --compact-output \
  | websocat \
     --header "Authorization: Bearer ${ALICE_TOKEN}" \
     -n1 \
     ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/updates/flats \
  | jq
```

TODO: pwsh

## gRPC Reflection

**List** the available gRPC services (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  list
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  list
```

**List** the methods on a gRPC service (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  list com.daml.ledger.api.v2.StateService
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  list com.daml.ledger.api.v2.StateService
```

**Describe** the methods on a gRPC service (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  describe com.daml.ledger.api.v2.StateService.GetActiveContracts
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  describe com.daml.ledger.api.v2.StateService.GetActiveContracts
```

**Describe** a gRPC message (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  describe com.daml.ledger.api.v2.GetActiveContractsRequest
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  describe com.daml.ledger.api.v2.GetActiveContractsRequest
```

## Miscellaneous

Get the ledger version information with gRPC (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.VersionService.GetLedgerApiVersion
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.VersionService.GetLedgerApiVersion
```

**Get** the participant status from the Admin API (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" \
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus
```

**Open** a Canton Console into the ledger (bash, pwsh):

Insert the result of `echo $ADMIN_TOKEN` into remote.conf.

```
daml canton-console --host localhost --port 6865 --admin-api-port 6866 --config remote.conf
```



















## TODO

* Switch to a 3.3 snapshot
* Replace command-ids with GUIDs
* Write jq functions one instruction per line
* Finish the current set of examples
* Alice with Ledger API
* Bob with Ledger JSON API
* Bob can_read_as_any_party
* Add an exercise example
* Consistent quote characters
* Consistent indents
* Consistent header styles
* Consistent heading capitalization
* Best practice for bash?
* "Get" vs. "List"
* Double-check that the correct token is used.
* Add `--location` option
* Provide alternative, default JWT for non-auth situations
* Include a JWT on every call, defaulting to the default one.
* Surround strings and URLs with quotes
* Replace option shorthands (e.g., `curl --silent`)
* Make sure all file output is `tee`'d.
* Combine JSON and gRPC commands, with a table before each sample?
* Table of contents with links
* All variables are surrounded with braces `\$(?!{)[A-Z_]+(?!})`
* Test on an auth-enabled ledger (bash, pwsh)
* Test on a non-auth-enabled ledger (bash, pwsh)
* Add sample output
* Provide an example for every endpoint.
* Create an interactive webpage
  * Display all the examples, with navigation
  * Filter by search string
  * Filter by Ledger API, Ledger JSON API, curl, websocat, grpcurl, Daml Assistant, etc.
  * Search results include dependent environment variables