# Introduce a New Interface

## Background

TODO

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

CD into the project folder.

```
cd introduce-new-interface/introduce-new-interface
```

Start the ledger with an `Asset1` contract.

```
cd Assets1

daml start
```

## Deploy a new interface

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
  --script-name Assets2:setup
```


## TODO

- double check that Daml Script's @queryInterface is not doing something extra
  by querying the Ledger API directly to see what results.
- clean up the code
- write the demo steps
- write the background