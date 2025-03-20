# HTTP JSON SAMPLE

Related docs: <https://docs.daml.com/json-api/index.html>

## Download the demo

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --branch http-json-sample \
  --single-branch http-json-sample
```

## Sample Commands

Compile the Daml:

```
daml build
```

Start everything:

```
docker compose up scripts --detach
```

Check HTTP JSON API is up-and-running:

```
curl http://localhost:7575/readyz`
```

Get the party id:

```
daml ledger list-parties --host localhost --port 5003
```

Get the user id:

```
curl http://localhost:7575/v1/users --header 'Authorization: Bearer ignored'
```

Create a JWT using either a Custom Daml Access Token (deprecated) or an Audience-based User Token:

```
{
  "https://daml.com/ledger-api":
  {
    "ledgerId": "participant1",
    "applicationId": "HTTP-JSON-API-Gateway",
    "actAs": ["alice::12209b520f92ed5a59c5451670e0e629488453850aab0d48b05e1a7cd75991ab557b"]
  }
}
```

```
{
  "iss": "ignored",
  "sub": "alice",
  "aud": "https://daml.com/jwt/aud/participant/participant1"
}
```

<https://jwt.io/>

Example Audience-based User Token:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZ25vcmVkIiwic3ViIjoiYWxpY2UiLCJhdWQiOiJodHRwczovL2RhbWwuY29tL2p3dC9hdWQvcGFydGljaXBhbnQvcGFydGljaXBhbnQxIn0.l9xKl0HZEaWx59dtfot3Uvf3v2ApW7xXOtYmW8-wNzM
```

View the contract:

```
curl http://localhost:7575/v1/query --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZ25vcmVkIiwic3ViIjoiYWxpY2UiLCJhdWQiOiJodHRwczovL2RhbWwuY29tL2p3dC9hdWQvcGFydGljaXBhbnQvcGFydGljaXBhbnQxIn0.l9xKl0HZEaWx59dtfot3Uvf3v2ApW7xXOtYmW8-wNzM'
```

Get the package id:

```
daml damlc inspect-dar .daml/dist/http-json-sample-0.0.1.dar --json | grep main_package_id
```

Stream the contracts:

```
wscat --connect http://localhost:7575/v1/stream/query \
      --subprotocol 'daml.ws.auth' \
      --subprotocol 'jwt.token.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpZ25vcmVkIiwic3ViIjoiYWxpY2UiLCJhdWQiOiJodHRwczovL2RhbWwuY29tL2p3dC9hdWQvcGFydGljaXBhbnQvcGFydGljaXBhbnQxIn0.l9xKl0HZEaWx59dtfot3Uvf3v2ApW7xXOtYmW8-wNzM' \
      --execute '{ "templateIds" : [ "78cedc1977eb4b01c106061654bbc6b82b1a358d8a16e7b34701766b0e4ad581:Main:PaintHouse" ] }'
```
