# HTTP JSON SAMPLE

Related docs: <https://docs.daml.com/json-api/index.html>

## Sample Commands

Start everything up:

`docker compose up scripts --detach`

Check HTTP JSON API is up-and-running:

`curl http://localhost:7575/readyz`

Get the package id:

`daml damlc inspect-dar .daml/dist/http-json-sample-0.0.1.dar --json | grep main_package_id`

Get the party id:

`daml ledger list-parties --host localhost --port 5003`

Form a JWT:

```
{
  "https://daml.com/ledger-api":
  {
    "ledgerId": "participant1",
    "applicationId": "HTTP-JSON-API-Gateway",
    "actAs": ["alice::1220068bd3c9050b92a29b64dc84ad9f5a9c9ff07335c109ecbad4b60837a7313c4f"]
  }
}
```

<https://jwt.io/>

Confirm that the auth is correct:

`curl http://localhost:7575/v1/packages --header 'Authorization: Bearer eyJh...ecfK0'`

View the contract:

`curl http://localhost:7575/v1/query --header 'Authorization: Bearer eyJhbG....g5qwsh0'`
