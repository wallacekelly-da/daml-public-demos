FROM ubuntu:jammy

# install utils
RUN apt-get update \
  && apt-get install -y iputils-ping \
  && apt-get install -y curl \
  && apt-get install -y vim \
  && apt-get install -y jq \
  && apt-get install -y openjdk-17-jre-headless
RUN curl -sSL "https://github.com/fullstorydev/grpcurl/releases/download/v1.9.1/grpcurl_1.9.1_linux_x86_64.tar.gz" \
  | tar -xz -C /usr/local/bin

# create a user
RUN useradd -rm -d /home/daml -s /bin/bash -g root -G sudo -u 1001 daml

# download a copy of Canton
ARG CANTON_VERSION=2.7.9
ADD https://github.com/digital-asset/canton/releases/download/v${CANTON_VERSION}/canton-open-source-${CANTON_VERSION}.tar.gz /tmp/canton.tar.gz
RUN chown daml /tmp/canton.tar.gz

# download a copy of Daml SDK
ARG DAML_SDK_VERSION=2.7.9
ADD https://github.com/digital-asset/daml/releases/download/v${DAML_SDK_VERSION}/daml-sdk-${DAML_SDK_VERSION}-linux.tar.gz /tmp/daml-sdk.tar.gz
RUN chown daml /tmp/daml-sdk.tar.gz

# switch to the user
USER daml
WORKDIR /home/daml

# install Canton
RUN mkdir -p ./.canton \
  && tar -xf /tmp/canton.tar.gz -C ./.canton --strip-components=1 \
  && rm /tmp/canton.tar.gz
ENV PATH="/home/daml/.canton/bin:${PATH}"
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64/

# install SDK
RUN mkdir -p /tmp/daml-sdk \
  && tar -xf /tmp/daml-sdk.tar.gz -C /tmp/daml-sdk --strip-components=1 \
  && /tmp/daml-sdk/install.sh \
  && rm /tmp/daml-sdk.tar.gz \
  && rm -rf /tmp/daml-sdk \
  && printf "auto-install: false\nupdate-check: never\n" >> /home/daml/.daml/daml-config.yaml
ENV PATH="/home/daml/.daml/bin:${PATH}"

# copy over Daml code
COPY ./daml.yaml ./daml.yaml
COPY ./Main.daml ./Main.daml