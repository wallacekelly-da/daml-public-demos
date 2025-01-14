# Smart Contract Upgrades

## Download

To checkout this demo, use:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch smart-contract-upgrades \
  smart-contract-upgrades
```

## Setup

CD into the folder.

```
cd smart-contract-upgrades
```

Install the SDK.

```
cd Assets1/AssetModels

daml install project

cd ../..
```

Set the SDK version.

```
export DAML_SDK_VERSION=2.10.0-rc1
```

Build the Daml.

```
daml build --all
```

Login to jFrog.

```
docker login digitalasset-docker.jfrog.io
```

Pull the images.

```
docker compose pull
```

Start the demo.

```
docker compose up --detach httpjson1
```

Confirm the Ledger API is up-and-running.

```
grpcurl \
  -plaintext \
  localhost:4001 \
  com.daml.ledger.api.v1.LedgerIdentityService.GetLedgerIdentity
```

Confirm the HTTP JSON API is up-and-running.

```
curl http://localhost:7575/readyz
```

## Step 1 - Create the initial contracts

Create contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  --script-name Assets:setup
```

Get the party ids.

```
daml ledger list-parties \
  --host localhost \
  --port 4001
```

Replace the party id in [filter-by-alice.json](./queries/filter-by-alice.json).

Get the contracts.

```
cat queries/filter-by-alice.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.ActiveContractsService.GetActiveContracts
```

```
export TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZ25vcmVkIiwic3ViIjoiYWxpY2UiLCJhdWQiOiJodHRwczovL2RhbWwuY29tL2p3dC9hdWQvcGFydGljaXBhbnQvcGFydGljaXBhbnQxIn0.l9xKl0HZEaWx59dtfot3Uvf3v2ApW7xXOtYmW8-wNzM'

curl http://localhost:7575/v1/query \
   --header 'Authorization: Bearer '"${TOKEN}" \
   | jq
```
