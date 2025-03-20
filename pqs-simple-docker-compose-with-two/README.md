# Daml Public Demos by Wallace Kelly

Each demo is in its own Git branch.

## Git clone this demo

```
git clone https://github.com/wallacekelly-da/daml-public-demos.git -b pqs-simple-docker-compose --single-branch pqs-simple-docker-compose
```

See <https://github.com/wallacekelly-da/daml-public-demos/tree/daml-script-participant-config> for details about the multi-participant scripting.

## Sample commands

```
rm -f configs/participant-config.json

daml build

docker compose up utils scribe1 scribe2 --detach

docker compose up scripts

docker compose down
```