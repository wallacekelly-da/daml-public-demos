# Docker Canton 3.x Example

This illustrates a minimal Docker Compose for the following:

* A single Canton 3.x process
* A single private domain
* A single participant

## Demo Steps

1. Clone the repository:

    ```
    git clone \
      https://github.com/wallacekelly-da/daml-public-demos.git \
      --single-branch \
      --depth 1 \
      --branch docker-canton3-example \
      docker-canton3-example
    ```

2. Start the Docker Compose:

    ```
    docker compose up --detach canton
    ```

3. Start a Canton Console:

    ```
    docker compose run -it --rm console
    ```
