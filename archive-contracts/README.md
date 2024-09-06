# Archive Contracts from Canton Console

## Overview

This demo walks through the steps of archiving contracts
from within the Canton Console.

## Download

To download this demo branch, run this command:

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --branch archive-contracts-from-console \
  --single-branch archive-contracts-from-console
```

## Demo Steps

Start the ledger and create contracts.

```
daml start --start-navigator no
```

View the existing contracts.

```
daml script \
  --dar .daml/dist/archive-contracts-0.0.1.dar \
  --script-name Main:inventoryAssets \
  --ledger-host localhost \
  --ledger-port 6865
```

Get the package id.

```
daml damlc inspect-dar --json \
  .daml/dist/archive-contracts-0.0.1.dar \
  | jq '.main_package_id' -r
```

Start a Canton Console.

```
daml canton-console \
  -C canton.features.enable-testing-commands=yes
```

Within the Canton Console, get the party id.

```
var party = sandbox.parties.hosted("alice").head.party
```

Create a TemplateId.

```
var assetTemplate = new TemplateId(
  packageId="<replace>",
  moduleName="Main",
  entityName="Asset")
```

Count the number of related contracts.

```
sandbox.ledger_api.acs.of_all(
  filterTemplates=Seq(filterTemplate)).length
```

Get a single contract.

```
var contract = sandbox.ledger_api.acs.of_all(
  limit=1,
  filterTemplates=Seq(assetTemplate)).head
```

Create an Archive command.

```
var archiveCmd = ledger_api_utils.exercise(
  choice="Archive",
  arguments=Map(),
  event=contract.event)
```

Archive the command.

```
sandbox.ledger_api.commands.submit(
  actAs=Seq(party),
  commands=Seq(archiveCmd))
```

Count the remaining contracts.

```
sandbox.ledger_api.acs.of_all(
  filterTemplates=Seq(assetTemplate)).length
```
