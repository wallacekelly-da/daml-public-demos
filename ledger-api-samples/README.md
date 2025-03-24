# ledger-api-samples

A collection of `curl`, `grpcurl`, and Postman calls.

## Setup

To get the collection:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch ledger-api-samples \
  ledger-api-samples
```

To start the ledger:

```
cd ledger-api-samples

daml start
```

Set environment variables:

```
export LEDGER_HOST=localhost
export LEDGER_PORT=6865
export LEDGER_ADMIN=6866
export LEDGER_JSON=7575
```

## JSON API

[Full documentation](https://docs.digitalasset.com/canton/3.3/usermanual/json-api/index.html)

Check the health:

```
curl http://$LEDGER_HOST:$LEDGER_JSON/readyz
```



## gRPC

[Full documentation](https://docs.digitalasset.com/build/references/ledger-grpc-api-reference/index.html)

### Authentication

If the target Ledger API requires a JWT, store the token in `LEDGER_TOKEN` and include the following in the `grpcurl` call:

```
-H "Authorization: Bearer $LEDGER_TOKEN"
```

### Reflection

List the services:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  list
```

List the methods on a service:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  list grpc.health.v1.Health
```

Describe the methods on a service:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  describe grpc.health.v1.Health.Check
```

Describe a message:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  describe grpc.health.v1.HealthCheckResponse
```

### Service Status

Check the gRPC health:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  grpc.health.v1.Health.Check
```

Get the ledger version information:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.VersionService.GetLedgerApiVersion
```

Get the participant status from the Admin API:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_ADMIN_PORT \
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus
```

Get the participant ID from the Admin API:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_ADMIN \
  com.digitalasset.canton.admin.participant.v30.ParticipantStatusService.ParticipantStatus \
  | jq -r '.status.commonStatus.uid'
```

### DARs, Packages, Parties

Get the main package id for a given DAR:  
(requires Daml SDK)

```
export PACKAGE_ID=$(
  daml damlc inspect-dar --json \
    .daml/dist/ledger-api-samples-0.0.1.dar \
    | jq -r .main_package_id
  ); echo $PACKAGE_ID
```

List package ids:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.PackageService.ListPackages \
  | grep $PACKAGE_ID
```

List known packages:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.admin.PackageManagementService.ListKnownPackages \
  | grep -B 1 -A 4 $PACKAGE_ID
```

List known packages and DAR file names:  
(requires Daml SDK)

```
daml packages list --host $LEDGER_HOST --port $LEDGER_PORT \
  | sort --key 2
```

List the DARs using the Admin API:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_ADMIN \
  com.digitalasset.canton.admin.participant.v30.PackageService.ListDars
```

Get the participant thumbnail:

```
export THUMBNAIL=$(
  grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
    com.daml.ledger.api.v2.admin.PartyManagementService.GetParticipantId \
    | jq -r '.participant_id | split("::").[1]'
  ); echo $THUMBNAIL
```

List local parties, using jq:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
  | jq '.party_details[] | select(.is_local)'
```

Select a party by party hint:

```
export PARTY_ID=$(
  grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
    com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
    | jq -r '.party_details[] | select(.is_local) | select(.party | startswith("Alice::")) | .party'
  ); echo $PARTY_ID
```

List local parties, using grep:

```
grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.admin.PartyManagementService.ListKnownParties \
  | grep $THUMBNAIL
```

List known parties:  
(requires the Daml SDK)

```
daml ledger list-parties --host $LEDGER_HOST --port $LEDGER_PORT \
  | grep 'isLocal = True'
```

Select a party by name:


### Contracts

Get a ledger offset:

```
export LEDGER_END=$(
  grpcurl -plaintext $LEDGER_HOST:$LEDGER_PORT \
    com.daml.ledger.api.v2.StateService.GetLedgerEnd \
    | jq -r '.offset'
  ); echo $LEDGER_END
```

Get the active contracts:

```
grpcurl -plaintext -d @ $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.StateService.GetActiveContracts <<EOF
  {
    "active_at_offset": "$LEDGER_END",
    "filter": {
      "filters_by_party": {
        "$PARTY_ID": {}
      }
    },
    "verbose": "true"
  }
EOF
```

Get the active contracts for a given template:

```
export PACKAGE_ID=e2d56802d425926d85a71243706d728391790f6c61c0447f91c1f9ffac849225
export PACKAGE_ID=#CreateParties
export MODULE_NAME=Main
export ENTITY_NAME=Asset
```

```
grpcurl -plaintext -d @ $LEDGER_HOST:$LEDGER_PORT \
  com.daml.ledger.api.v2.StateService.GetActiveContracts <<EOF \
  | jq
  {
    "verbose": true,
    "active_at_offset": "$LEDGER_END",
    "filter": {
      "filters_by_party": {
        "$PARTY_ID": {
          "cumulative": [
            {
              "template_filter": {
                "template_id": {
                  "package_id": "$PACKAGE_ID",
                  "module_name": "$MODULE_NAME",
                  "entity_name": "$ENTITY_NAME"
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