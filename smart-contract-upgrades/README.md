# Smart Contract Upgrades

## Download

**Checkout** this demo with:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch smart-contract-upgrades \
  smart-contract-upgrades
```

## Setup

**CD** into the folder.

```
cd smart-contract-upgrades/smart-contract-upgrades
```

**Install** the SDK.

```
cd Assets1/AssetModels

daml install project

cd ../..
```

**Set** the SDK version.

```
export DAML_SDK_VERSION=2.10.0
```

**Build** the Daml.

```
daml build --all
```

**Login** to jFrog.

```
docker login digitalasset-docker.jfrog.io
```

**Pull** the images.

```
docker compose pull
```

**Start** the participant node.

```
docker compose up --detach participant1
```

**Confirm** the Ledger API is up-and-running.

```
grpcurl \
  -plaintext \
  localhost:4001 \
  com.daml.ledger.api.v1.LedgerIdentityService.GetLedgerIdentity
```

## Step 1 - Create the initial contracts

**Get** the party ids.

```
daml ledger list-parties \
  --host localhost \
  --port 4001
```

**Replace** the party id in [get-transactions.json](./queries/get-transactions.json).

**Get** the package id.

```
daml damlc inspect-dar --json \
  Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  | grep main_package_id
```

**Replace** the package id in [get-transactions.json](./queries/get-transactions.json).

**Subscribe** to the transactions.

```
cat queries/get-transactions.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.TransactionService.GetTransactions
```

In another terminal, **create** a contract. **Observe** the transaction event in the original terminal.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  --script-name Assets:setup
```

In [get-transactions.json](./queries/get-transactions.json), **replace** the package-id with `#AssetModels`.

In the original terminal, **stop** and **restart** the `GetTransactions` subscription.

```
cat queries/get-transactions.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.TransactionService.GetTransactions
```

**Create** a second contract.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets1/AssetModels/.daml/dist/AssetModels-0.0.1.dar \
  --script-name Assets:setup
```

## Step 2 - Deploy a new version

**Upload** the v2 DAR file.

```
daml ledger upload-dar \
  --host localhost \
  --port 4001 \
  Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar
```

**Create** a v2 contract.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:setup
```

**Notice** the following:

* When the Daml Script queried for assets, it retrieved and displayed all the assets.
* The new field is defaulted with `None` for the old contracts.
* The new contract was streamed to the `GetTransactions` subscription.

## Step 3 - Experiment with template filtering

**Note** that at this point, there are four contracts in the ACS
-- two v1 contracts and two v2 contracts.

**Get** the party ids.

```
daml ledger list-parties \
  --host localhost \
  --port 4001
```

**Replace** the party id in [get-active-contracts.json](./queries/get-active-contracts.json).
**Notice** that the template filter is currently for `"package_id": "#AssetModels"`.

**Query** for active `Assets`.

```
cat queries/get-active-contracts.json | \
  grpcurl \
    -plaintext \
    -d @ \
    localhost:4001 \
    com.daml.ledger.api.v1.ActiveContractsService.GetActiveContracts | \
       grep -A 1 'template_id\|"desc"'
```

**Notice** that all four contracts, across two different templates, are returned.
The old contracts do not have the new `desc` field. The new contracts do.

```
      "template_id": {
        "package_id": "6f9b3...",
--
      "template_id": {
        "package_id": "6f9b3...",
--
      "template_id": {
        "package_id": "fd594...",
--
            "label": "desc",
            "value": {
--
      "template_id": {
        "package_id": "fd594...",
--
            "label": "desc",
            "value": {
```

**Change** the `package_id` field in [get-active-contracts.json](./queries/get-active-contracts.json)
to be one of the package ids, instead of the package name identifier `#AssetModels`.

**Rerun** the query. **Notice** that only the contracts with the matching package id are returned.





## Step 4 - Exercise new choices

**Exercise** the new, _non-consuming_ `GetSummary` choice.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:getSummaries
```

**Notice** the following:

* The new choice was called on the old contracts.
* The old contracts were not upgraded to call the _nonconsuming_ choice.  
  (we know this because no create events occurred on the transaction stream)

**Exercise** the _consuming_ `ReturnIt` choice on all the contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 4001 \
  --dar Assets2/AssetModels/.daml/dist/AssetModels-0.0.2.dar \
  --script-name Assets:returnAll
```

**Notice** the following:

* The new choice was exercised on all contracts, including v1 contracts.  
  (all assets have been returned to `alice`.)
* The old contracts were archived. Version 2 contracts were created.  
  (as can be seen on the contract stream.)
