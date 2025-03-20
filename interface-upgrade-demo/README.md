# Smart Contract Upgrades

## Setup

CD into the folder.

```
cd interface-upgrade-demo
```

Install the SDK.

```
cd Assets1/AssetModels

daml install project

cd ../..
```

Set the SDK version.

```
export DAML_SDK_VERSION=2.9.5
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

Replace the party id in [get-transactions.json](./queries/get-transactions.json) and [get-transactions-by-interface.json](./queries/get-transactions-by.json).

Get the package id for the V1 package.

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

Create a v1 contract and notice that it appears on the transaction
stream.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  --script-name Assets:setup
```

Get the package id for the Asset Interface package package.

```
daml damlc inspect-dar --json \
  AssetInterface/.daml/dist/AssetInterface-0.0.1.dar \
  | grep main_package_id
```


In [get-transactions-by-interface.json](./queries/get-transactions-by-interface.json), replace the package-id with package ID of the asset interface package.

Start another transacion stream using the interface query and notice
that the V1 contract appears here also, with the fields visible as in
the viewtype `IAssetView`.


```
cat queries/get-transactions-by-interface.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.TransactionService.GetTransactions
```

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

* When the Daml Script queried for assets, it retrieved and displayed all the assets, both v1 and v2.
* The v2 contract was not emitted on the v1 transaction stream, but it
  was emitted on the interface transaction stream with the same
  `IAssetView` representation.

## Step 3 - Exercise new choices

Exercise the new, _non-consuming_ `GetSummary` choice.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar AssetInterface/.daml/dist/AssetInterface-0.0.1.dar \
  --script-name AssetInterface:showSummariesForAlice
```

Notice the following:

* The new choice was called on both v1 and v2 contracts.
* No events occurred on either transaction stream.
  (the old contracts were not converted to new contracts)

Exercise the _consuming_ `ReturnIt` choice on all the contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar AssetInterface/.daml/dist/AssetInterface-0.0.1.dar \
  --script-name AssetInterface:returnAll
```

Notice the following:

* The new choice was exercised on all contracts, including both v1 and
  v2 contracts.  (all assets have been returned to `alice`.)
* The old contracts were archived and replacement contracts have been
  created for the corresponding version.

---

```
daml ledger upload-dar \
  --host localhost \
  --port 4001 \
   AssetInterface/.daml/dist/AssetInterface-0.0.1.dar
```

