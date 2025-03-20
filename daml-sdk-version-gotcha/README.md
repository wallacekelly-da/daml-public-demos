# Daml SDK Version Gotcha

## Download

```
git clone \
  https://github.com/wallacekelly-da/daml-public-demos.git \
  --single-branch \
  --depth 1 \
  --branch daml-sdk-version-gotcha \
  daml-sdk-version-gotcha
```

## Demo

1. **Install** a 2.x version of the Daml SDK.
   ```
   daml install 2.9.5
   ```

1. **Create** the two Daml projects.
   ```
   daml new lib

   daml new app
   ```

1. **Create** the multi-packages file.
   ```
   packages:
   - app
   - lib
   ```

1. **Confirm** the SDK version for the projects.
   ```
   > cat lib/daml.yaml | grep sdk-version
   sdk-version: 2.9.5
   ```

1. **Install** a 3.x version of the Daml SDK.
   ```
   daml install 3.3.0-snapshot.20241127.0
   ```

1. **Build** the projects.
   ```
   daml build --all
   ```

1. **Start** the sandbox.
   ```
   daml sandbox --dar app/.daml/dist/app-0.0.1.dar
   ```

1. **See** the error.
   ```
   upload-dar did not succeed:
   "ALLOWED_LANGUAGE_VERSIONS(8,a276e426):
   Disallowed language version in package 86828...:
   Expected version between 2.1 and 2.dev but got 1.14" 
   ```
   
   See [the forums](https://discuss.daml.com/t/why-expected-version-between-2-1-and-2-dev-but-got-1-14/7530/2?u=wallacekelly).

1. **Avoid** the error.
   ```
   DAML_SDK_VERSION=2.9.5 daml sandbox --dar app/.daml/dist/app-0.0.1.dar
   ```
