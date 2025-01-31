# Introduce a New Interface

## Overview

This demo explorers how interface instances can be added to existing templates.
This is in the context of an evolving data model.

1. An `Assets1` DAR is deployed and an `Asset1` contract is created.
2. Subsequently, an `Assets2` DAR is deployed which defines _both_ a new `Asset2` template
   _and_ a new interface `IAsset`. The interface is intended to interact
   with both versions of the asset template.

   * The new `Asset2` template renames an existing field and adds a new _non-optional_ field.
   * The new `IAsset` interface declares yet another new field, wraps an existing choice, and declares a new choice.

   A new `Asset2` contract is created.
3. The ledger is queried for all contracts that implement the interface.
   Both `Asset1` and `Asset2` contracts are returned.
4. The new interface-defined choice is executed on _all_ existing
   contracts. This action effectively "upgrades" `Asset1` contracts
   into `Asset2` contracts.

All of this is done without the Smart Contracts Upgrade feature set.
That why the template is renamed from `Asset1` to `Asset2`.

## Key code

* [Assets1.daml](./Assets1/daml/Assets1.daml)
* [Assets2.daml](./Assets2/daml/Assets2.daml)
* [Interfaces.daml](./Assets2/daml/Interfaces.daml)

_Note: In a real project, the interfaces should go in
a separate DAR. They are included in `Assets2` here
for simplicity in the demo._

## Setup

To checkout this demo, use:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch introduce-new-interface \
  introduce-new-interface
```

Change into the project folder.

```
cd introduce-new-interface/introduce-new-interface
```

Start the ledger with an `Asset1` contract.

```
cd Assets1

daml start
```

The output includes:

```
TV created and given to Bob.
```

Store away Alice's party id:

```
export DEMO_ALICE=$(grpcurl --plaintext localhost:6865 \
  com.daml.ledger.api.v1.admin.PartyManagementService.ListKnownParties \
  | jq -r '.party_details[] | select(.party | startswith("alice::")) | .party')
```


## Deploy changes

Build the Assets2 project.

```
cd Assets2

daml build
```

Upload the new DAR to the ledger.

```
daml ledger upload-dar .daml/dist/Assets2-0.0.2.dar
```

Create an Assets2 contract.

```
daml script \
  --ledger-host localhost \
  --ledger-port 6865 \
  --dar .daml/dist/Assets2-0.0.2.dar \
  --script-name Scripts:setup
```

The output includes:

```
DVDs created and given to Charlie
```

Store away the new package id:

```
export DEMO_PACKAGEID=$(daml damlc inspect-dar --json \
  .daml/dist/Assets2-0.0.2.dar \
  | jq -r '.main_package_id')
```

## Query for both Asset1 and Asset2 contracts

Notice the implementation of the [`listAssets` script](./Assets2/daml/Scripts.daml)
queries for all contracts which implement the `IAsset` interface.

Run the [`listAssets` script](./Assets2/daml/Scripts.daml).
Notice that it returns _all_ assets. 

```
daml script \
  --ledger-host localhost \
  --ledger-port 6865 \
  --dar .daml/dist/Assets2-0.0.2.dar \
  --script-name Scripts:listAssets
```

The output includes information about both an `Asset1` contract and an `Asset2` contract.
A default quantity of `1` has been returned for the `Asset1` contract.

```
Asset1: Bob's 1 TV
Asset2: Charlie's 5 discs
```

Store away the current ledger offset:

```
export DEMO_OFFSET=$(grpcurl --plaintext localhost:6865 \
  com.daml.ledger.api.v1.TransactionService.GetLedgerEnd \
  | jq -r '.offset.absolute')
```

Query for any contracts which implement `IAsset`.

```
cat GetActiveContracts.json \
  | envsubst \
  | grpcurl --plaintext -d @ \
      localhost:6865 \
      com.daml.ledger.api.v1.ActiveContractsService.GetActiveContracts \
  | jq -f GetActiveContracts.jq
```

The results:

```json
{
  "owner": "bob::12203...",
  "id": "TV",
  "quantity": "1",
  "version": "1"
}
{
  "owner": "charlie::12203...",
  "id": "discs",
  "quantity": "5",
  "version": "2"
}
```

## Exercise a choice on both Asset1 and Asset2 contracts

Notice that the [`IAsset` interface](./Assets2/daml/Interfaces.daml)
declares a `TakeBack` choice.

Run the [`takeBackAssets` script](./Assets2/daml/Scripts.daml).
Notice that the choice is exercised on both `Asset1` and `Asset2` contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 6865 \
  --dar .daml/dist/Assets2-0.0.2.dar \
  --script-name Scripts:takeBackAssets
```

Re-run the [`listAssets` script](./Assets2/daml/Scripts.daml).
Notice that the resulting contracts are _all_ `Asset2` contracts.

```
daml script \
  --ledger-host localhost \
  --ledger-port 6865 \
  --dar .daml/dist/Assets2-0.0.2.dar \
  --script-name Scripts:listAssets
```

Confirm the same results with `grpcurl`:

```
export DEMO_OFFSET=$(grpcurl --plaintext localhost:6865 \
  com.daml.ledger.api.v1.TransactionService.GetLedgerEnd \
  | jq -r '.offset.absolute')


cat GetActiveContracts.json \
  | envsubst \
  | grpcurl --plaintext -d @ \
      localhost:6865 \
      com.daml.ledger.api.v1.ActiveContractsService.GetActiveContracts \
  | jq -f GetActiveContracts.jq
```
