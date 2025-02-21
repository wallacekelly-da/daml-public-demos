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
cd smart-contract-upgrades/smart-contract-upgrades
```

Install the SDK.

```
cd Assets1/AssetModels

daml install project

cd ../..
```

Set the SDK version.

```
export DAML_SDK_VERSION=2.10.0
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
docker compose up --detach participant1
```

Confirm the Ledger API is up-and-running.

```
grpcurl \
  -plaintext \
  localhost:4001 \
  com.daml.ledger.api.v1.LedgerIdentityService.GetLedgerIdentity
```

## Step 1 - Create the initial contracts

Get the party ids.

```
daml ledger list-parties \
  --host localhost \
  --port 4001
```

Replace the party id in [get-transactions.json](./queries/get-transactions.json).

Get the package id.

```
daml damlc inspect-dar --json \
  Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  | grep main_package_id
```

Replace the package id in [get-transactions.json](./queries/get-transactions.json).

Subscribe to the transactions.

```
cat queries/get-transactions.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.TransactionService.GetTransactions
```

Create a contract.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  --script-name Assets:setup
```

In [get-transactions.json](./queries/get-transactions.json), replace the package-id with `#AssetModels`.

Create a second contract.

## Step 2 - Deploy a new version

Upload the v2 DAR file.

```
daml ledger upload-dar \
  --host localhost \
  --port 4001 \
  Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar
```

Create a v2 contract.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:setup
```

Notice the following:

* When the Daml Script queried for assets, it retrieved and displayed all the assets.
* The new field is defaulted with `None`.
* The new contract was streamed to GetTransactions.

## Step 3 - Exercise new choices

Exercise the new, _non-consuming_ `GetSummary` choice.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:getSummaries
```

Notice the following:

* The new choice was called on the old contracts.
* No create events occurred on the transaction stream.  
  (the old contracts were not converted to new contracts)

Exercise the _consuming_ `ReturnIt` choice on all the contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:returnAll
```

Notice the following:

* The new choice was exercised on all contracts, including v1 contracts.  
  (all assets have been returned to `alice`.)
* The old contracts were archived. Version 2 contracts were created.  
  (as can be seen on the contract stream.)