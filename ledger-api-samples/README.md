# ledger-api-samples

A collection of `curl`, `grpcurl`, and Postman calls.

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
```

Set variables in pwsh:

```
$LEDGER_HOST = "localhost"
$LEDGER_PORT = "6865"
$LEDGER_ADMIN = "6866"
$LEDGER_JSON = "7575"
```

## JSON API

[Full documentation](https://docs.digitalasset.com/canton/3.3/usermanual/json-api/index.html)

#### Check the health:

```
curl "http://${LEDGER_HOST}:${LEDGER_JSON}/readyz"
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
  | jq -r '.status.commonStatus.uid'
```

```
grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_ADMIN}" `
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus `
  | jq -r '.status.commonStatus.uid'
```

### DARs, Packages, Parties

#### Get the main package id for a given DAR (bash, pwsh):

```
export PACKAGE_ID=$(
  daml damlc inspect-dar --json \
    .daml/dist/ledger-api-samples-0.0.1.dar \
    | jq -r .main_package_id
  ); echo ${PACKAGE_ID}
```

```
$PACKAGE_ID=$( `
  daml damlc inspect-dar --json `
    .daml/dist/ledger-api-samples-0.0.1.dar `
    | jq -r .main_package_id `
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
  | grep ${PACKAGE_ID}
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
  | grep -B 1 -A 4 ${PACKAGE_ID}
```

#### List known packages and DAR file names using Daml Assistant (bash, pwsh):

```
daml packages list --host ${LEDGER_HOST} --port ${LEDGER_PORT} \
  | sort --key 2
```

```
daml packages list --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | sort --key 2
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
    | jq -r '.participant_id | split("::").[1]'
  ); echo ${PARTICIPANT_ID}
```

```
$PARTICIPANT_ID=$(
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
    com.daml.ledger.api.v2.admin.PartyManagementService.GetParticipantId `
    | jq -r '.participant_id | split("::").[1]'
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
    | jq -r '.party_details[] | select(.is_local) | select(.party | startswith("Alice::")) | .party'
  ); echo ${PARTY_ID}
```

```
$PARTY_ID=$( `
  grpcurl -plaintext ${LEDGER_HOST}:${LEDGER_PORT} `
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties `
    | jq -r '.party_details[] | select(.is_local) | select(.party | startswith("Alice::")) | .party' `
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
  | grep ${PARTICIPANT_ID}
```

#### List known parties using Daml Assistant (bash, pwsh):

```
daml ledger list-parties --host ${LEDGER_HOST} --port ${LEDGER_PORT} \
  | grep 'isLocal = True'
```

```
daml ledger list-parties --host ${LEDGER_HOST} --port ${LEDGER_PORT} `
  | grep 'isLocal = True'
```

### Read Contracts

#### Get a ledger offset (bash, pwsh):

```
export LEDGER_END=$(
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" \
    com.daml.ledger.api.v2.StateService.GetLedgerEnd \
    | jq -r '.offset'
  ); echo ${LEDGER_END}
```

```
$LEDGER_END=$( `
  grpcurl -plaintext "${LEDGER_HOST}:${LEDGER_PORT}" `
    com.daml.ledger.api.v2.StateService.GetLedgerEnd `
    | jq -r '.offset'
  ); echo ${LEDGER_END}
```

#### Get the active contracts (bash, pwsh):

```
grpcurl -plaintext -d @ "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.StateService.GetActiveContracts <<EOF
  {
    "active_at_offset": "${LEDGER_END}",
    "filter": {
      "filters_by_party": {
        "${PARTY_ID}": {}
      }
    },
    "verbose": "true"
  }
EOF
```

```
@"
{
  "active_at_offset": "${LEDGER_END}",
  "filter": {
    "filters_by_party": {
      "${PARTY_ID}": {}
    }
  },
  "verbose": "true"
}
"@ | `
grpcurl -plaintext -d `@ "${LEDGER_HOST}:${LEDGER_PORT}" `
  com.daml.ledger.api.v2.StateService.GetActiveContracts
```

#### Get the active contracts for a given template (bash, pwsh):

```
export PACKAGE_ID=#ledger-api-samples
export MODULE_NAME=Main
export ENTITY_NAME=Asset
```

```
grpcurl -plaintext -d @ "${LEDGER_HOST}:${LEDGER_PORT}" \
  com.daml.ledger.api.v2.StateService.GetActiveContracts <<EOF \
  | jq
  {
    "verbose": true,
    "active_at_offset": "${LEDGER_END}",
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
EOF
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
    "active_at_offset": "${LEDGER_END}",
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
