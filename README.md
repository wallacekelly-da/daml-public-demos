# Daml Public Demos by Wallace Kelly

## Source Code

Checkout this branch.

```
git clone https://github.com/wallacekelly-da/daml-public-demos.git --branch docker-compose-health-check --single-branch docker-compose-health-check

cd docker-compose-health-check
```

## Background

On a recent project, I needed to run a Docker Compose network.
However, I needed one of the containers to wait and not start
until the other containers were up-and-running.

## The Problem

In this demo, the `utils` container needs to run _after_ `participant1` and `participant2` are up-and-running. Otherwise, the result is one of these errors, depending on the race conditions:

```
Node participant1 is not initialized and therefore does not have an Id assigned yet.
```

```
java.lang.IndexOutOfBoundsException: 1 is out of bounds (min 0, max 0)
```

## The Solution

1. Turn on the gRPC Health Service on the Canton nodes. For example:

```
canton {
  participants {
    participant1 {
           :
           :
      monitoring.grpc-health-server {
        address = participant1
        port = 5861
      }
    }
  }
}
```

2. Add a Docker Compose `healthcheck` for the containers. For example:

```
services:
  participant1:
        :
        :
    healthcheck:
      test:
        [
          "CMD",
          "/usr/local/bin/grpc_health_probe",
          "--addr",
          "participant1:5861"
        ]
```

3. Add a `condition` to the _dependent_ container. For example:

```
services:
  utils:
     :
     :
    depends_on:
      participant1:
        condition: service_healthy
      participant2:
        condition: service_healthy
```

## Sample Outputs

### Before

```
> docker compose down
> docker compose up

[+] Running 5/5
 ✔ Network daml-public-demos_default  Created
 ✔ Container mydomain                 Created
 ✔ Container participant2             Created
 ✔ Container participant1             Created
 ✔ Container utils                    Created
Attaching to utils
utils  | ERROR Node participant1 is not initialized and therefore does not have an Id assigned yet.
```

### After

```
> docker compose down
> docker compose up

[+] Running 5/4
 ✔ Network daml-public-demos_default  Created
 ✔ Container mydomain                 Created
 ✔ Container participant2             Created
 ✔ Container participant1             Created
 ✔ Container utils                    Created
Attaching to utils
utils  | alice::12202df...
utils  | bob::12209f3...
```

## References

* [Canton Health Checks](https://docs.daml.com/canton/usermanual/monitoring.html#health-checks)
* [GrpcHealthServerConfig](https://docs.daml.com/canton/scaladoc/com/digitalasset/canton/config/GrpcHealthServerConfig.html)
* [grpc_health_probe](https://github.com/grpc-ecosystem/grpc-health-probe)
* [Docker Compose healthcheck](https://docs.docker.com/compose/compose-file/compose-file-v3/#healthcheck)
* [Docker Compose service_healthy](https://docs.docker.com/compose/compose-file/05-services/#long-syntax-1)
* [Docker Compose up options](https://docs.docker.com/engine/reference/commandline/compose_up/#options)
* [Stack Overflow: How to wait for other service to be ready](https://stackoverflow.com/questions/52322800/docker-compose-how-to-wait-for-other-service-to-be-ready)