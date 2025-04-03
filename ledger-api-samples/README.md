# Ledger API Samples

A collection of `grpcurl`, `curl`, `websocat`, and Postman calls.

## Prequisites

* [jq](https://jqlang.org/download/)
* [grpcurl](https://github.com/fullstorydev/grpcurl)
* [websocat](https://github.com/vi/websocat)

## Setup

To get the collection in bash:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch ledger-api-samples \
  ledger-api-samples
```

To get the collection in pwsh:

```
git clone `
  https://github.com/wallacekelly-da/daml-public-demos.git `
  --single-branch `
  --depth 1 `
  --branch ledger-api-samples `
  ledger-api-samples
```

To start the ledger:

```
cd ledger-api-samples

daml start
```

Set environment variables in bash:

```
export LEDGER_HOST=localhost
export LEDGER_PORT=6865
export LEDGER_ADMIN=6866
export LEDGER_JSON=7575
export USER_ID=alice
```

Set variables in pwsh:

```
$LEDGER_HOST = "localhost"
$LEDGER_PORT = "6865"
$LEDGER_ADMIN = "6866"
$LEDGER_JSON = "7575"
$USER_ID = "alice"
```

## JSON API

[Full documentation](https://docs.digitalasset.com/build/3.3/explanations/json-api/)

### Authentication

TODO: test the following.

If the target Ledger API requires a JWT, store the token in `LEDGER_TOKEN` and include the following in the `grpcurl` call:

```
-H "Authorization: Bearer ${LEDGER_TOKEN}"
```

### Reflection

TODO

### Service Status

#### Check the health:

```
curl "http://${LEDGER_HOST}:${LEDGER_JSON}/readyz"
```

### DARs, Packages, Parties, Users

#### List all packages

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/packages" \
  | jq
```

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/packages" `
  | jq
```



#### Get the local parties (bash, pwsh)

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" \
  | jq --raw '.partyDetails[] | select (.isLocal == true) | .party'
```

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" `
  | jq --raw '.partyDetails[] | select (.isLocal == true) | .party'
```

#### Get a specific party (bash, pwsh)

```
export PARTY_ID=$(
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" \
    | jq --raw '.partyDetails[] | select (.isLocal == true) | select(.party | startswith("Alice::")) | .party' \
); echo ${PARTY_ID}
```

```
$PARTY_ID=$(
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/parties" `
    | jq --raw '.partyDetails[] | select (.isLocal == true) | select(.party | startswith("Alice::")) | .party' `
); echo ${PARTY_ID}
```

#### List the users (bash, pwsh)

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users" \
  | jq --raw '.users[] | { id, primaryParty }'
```

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users" `
  | jq --raw '.users[] | { id, primaryParty }'
```

#### List a user's rights (bash, pwsh)

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users/${USER_ID}/rights" \
  | jq
```

```
curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/users/${USER_ID}/rights" `
  | jq
```

### Read Contracts

#### Get the current ledger offset (bash, pwsh):

```
export LEDGER_OFFSET=$(
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/ledger-end" \
    | jq --raw .offset
); echo $LEDGER_OFFSET
```

```
$LEDGER_OFFSET=$( `
  curl --silent "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/ledger-end" `
    | jq --raw .offset `
); echo $LEDGER_OFFSET
```

#### Get the active contracts over websocket (bash, pwsh):

```
echo '
{
  "verbose": true,
  "activeAtOffset": "'${LEDGER_OFFSET}'",
  "filter": {
    "filtersByParty" : {
      "'${PARTY_ID}'": {
        "cumulative": []
      }
    }
  }
}
' | jq --compact \
  | websocat -n1 ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts \
  | jq \
  >> actives.json
```

```
@"
{
  "verbose": true,
  "activeAtOffset": "${LEDGER_OFFSET}",
  "filter": {
    "filtersByParty" : {
      "'${PARTY_ID}'": {
        "cumulative": []
      }
    }
  }
}
"@ | jq --compact `
   | websocat -n1 ws://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts `
   | jq `
   | Tee-Object -FilePath "actives.json"
```

#### Get the active contracts over HTTP (bash, pwsh):

```
echo '
{
  "verbose": true,
  "activeAtOffset": "'${LEDGER_OFFSET}'",
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
' | jq --compact \
  | curl --silent --json @- "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts" \
  | jq \
  | tee actives.json
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
"@ | jq --compact `
   | curl --silent --json `@- "http://${LEDGER_HOST}:${LEDGER_JSON}/v2/state/active-contracts" `
   | jq `
   | Tee-Object -FilePath "actives.json"
```

## gRPC

[Full documentation](https://docs.digitalasset.com/build/references/ledger-grpc-api-reference/index.html)

### Authentication

If the target Ledger API requires a JWT, store the token in `LEDGER_TOKEN` and include the following in the `grpcurl` call:

```
-H "Authorization: Bearer ${LEDGER_TOKEN}"
```

### Reflection

#### List the services (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  list
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  list
```

#### List the methods on a service (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  list grpc.health.v1.Health
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  list grpc.health.v1.Health
```

#### Describe the methods on a service (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  describe grpc.health.v1.Health.Check
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  describe grpc.health.v1.Health.Check
```

#### Describe a message (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  describe grpc.health.v1.HealthCheckResponse
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  describe grpc.health.v1.HealthCheckResponse
```

### Service Status

#### Check the gRPC health (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  grpc.health.v1.Health.Check
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  grpc.health.v1.Health.Check
```

#### Get the ledger version information (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.VersionService.GetLedgerApiVersion
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.VersionService.GetLedgerApiVersion
```

#### Get the participant status from the Admin API (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" \
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus
```

#### Get the participant ID from the Admin API (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" \
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus \
  | jq --raw '.status.commonStatus.uid'
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus `
  | jq --raw '.status.commonStatus.uid'
```

### DARs, Packages, Parties, Users

#### Get the main package id for a given DAR (bash, pwsh):

```
export PACKAGE_ID=$(
  daml damlc inspect-dar --json \
    .daml/dist/ledger-api-samples-0.0.1.dar \
    | jq --raw .main_package_id
  ); echo ${PACKAGE_ID}
```

```
$PACKAGE_ID=$( `
  daml damlc inspect-dar --json `
    .daml/dist/ledger-api-samples-0.0.1.dar `
    | jq --raw .main_package_id `
  ); echo ${PACKAGE_ID}
```

#### List package ids (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.PackageService.ListPackages \
  | grep ${PACKAGE_ID}
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.PackageService.ListPackages `
  | Select-String -Pattern ${PACKAGE_ID}
```

#### List known packages (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.admin.PackageManagementService.ListKnownPackages \
  | grep -B 1 -A 4 ${PACKAGE_ID}
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.admin.PackageManagementService.ListKnownPackages `
  | Select-String -Pattern ${PACKAGE_ID} -Context 1,4
```

#### List known packages and DAR file names using Daml Assistant (bash, pwsh):

```
daml packages list --host ${LEDGER_HOST} --port ${LEDGER_PORT} \
  | sort --key 2
```

```
daml packages list --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | Sort-Object -Property { $_.Split(" ")[1] }
```

#### List the DARs using the Admin API (bash, pwsh):

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" \
  com.digitalasset.canton.admin.participant.v30.PackageService.ListDars
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.PackageService.ListDars
```

#### Get the participant id (bash, pwsh):

```
export PARTICIPANT_ID=$(
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
    com.daml.ledger.api.v2.admin.PartyManagementService.GetParticipantId \
    | jq --raw '.participant_id | split("::").[1]'
  ); echo ${PARTICIPANT_ID}
```

```
$PARTICIPANT_ID=$(
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
    com.daml.ledger.api.v2.admin.PartyManagementService.GetParticipantId `
    | jq --raw '.participant_id | split("::").[1]'
  ); echo ${PARTICIPANT_ID}
```

#### List local parties, using jq (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
  | jq '.party_details[] | select(.is_local)'
```

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties `
  | jq '.party_details[] | select(.is_local)'
```

#### Select a party by party hint (bash, pwsh):

```
export PARTY_ID=$(
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
    | jq --raw '.party_details[] | select(.is_local) | select(.party | startswith("Alice::")) | .party'
  ); echo ${PARTY_ID}
```

```
$PARTY_ID=$( `
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties `
    | jq --raw '.party_details[] | select(.is_local) | select(.party | startswith("Alice::")) | .party' `
  ); echo ${PARTY_ID}
```

#### List local parties, using grep (bash, pwsh):

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
  | grep ${PARTICIPANT_ID}
```

```
grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties `
  | Select-String -Pattern ${PARTICIPANT_ID}
```

#### List known parties using Daml Assistant (bash, pwsh):

```
daml ledger list-parties --host ${LEDGER_HOST} --port ${LEDGER_PORT} \
  | grep 'isLocal = True'
```

```
daml ledger list-parties --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | Select-String -Pattern 'isLocal = True'
```

### Read Contracts

#### Get a ledger offset (bash, pwsh):

```
export LEDGER_OFFSET=$(
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
    com.daml.ledger.api.v2.StateService.GetLedgerEnd \
    | jq --raw '.offset'
  ); echo ${LEDGER_OFFSET}
```

```
$LEDGER_OFFSET=$( `
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
    com.daml.ledger.api.v2.StateService.GetLedgerEnd `
    | jq --raw '.offset'
  ); echo ${LEDGER_OFFSET}
```

#### Get the active contracts (bash, pwsh):

```
echo '
{
  "verbose": "true",
  "active_at_offset": "${LEDGER_OFFSET}",
  "filter": {
    "filters_by_party": {
      "${PARTY_ID}": {}
    }
  }
}
' | grpcurl -plaintext -d @ "${LEDGER_HOST}:${LEDGER_PORT}" \
      com.daml.ledger.api.v2.StateService.GetActiveContracts \
  | tee actives.json
```

```
@"
{
  "verbose": "true",
  "active_at_offset": "${LEDGER_OFFSET}",
  "filter": {
    "filters_by_party": {
      "${PARTY_ID}": {}
    }
  }
}
"@ | `
grpcurl -plaintext -d `@ "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.StateService.GetActiveContracts `
  | Tee-Object -FilePath "actives.json"
```

#### Get the active contracts for a given template (bash, pwsh):

```
export PACKAGE_ID=#ledger-api-samples
export MODULE_NAME=Main
export ENTITY_NAME=Asset
```

```
echo '
{
  "verbose": true,
  "active_at_offset": "${LEDGER_OFFSET}",
  "filter": {
    "filters_by_party": {
      "${PARTY_ID}": {
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
' |
  grpcurl -plaintext -d @ "${LEDGER_HOST}:${LEDGER_PORT}" \
    com.daml.ledger.api.v2.StateService.GetActiveContracts \
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
        "${PARTY_ID}": {
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

## TODO

* Include a JWT on every call, defaulting to the default one.
* Surround strings and URLs with quotes
* Replace option shorthands (e.g., `curl --silent`)
* Make sure all file output is `tee`'d.
* Combine JSON and gRPC commands, with a table before each sample?
* Table of contents with links
* Test on an auth-enabled ledger
* Provide an example for every endpoint.