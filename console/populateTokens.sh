#!/usr/bin/env bash

# This scripts edits a given remote configuration file,
# replacing lines like: participant-name.token = ""
# with lines like this: participant-name.token = "abcdef1234567890"
# based on retrieving a JWT token

# confirm the environment variables are set
[[ -z "${REMOTE_CONFIG}" ]] && { echo "Environment variable REMOTE_CONFIG cannot be empty"; exit 1; }
[[ -z "${REMOTE_CONFIG_OUT}" ]] && { echo "Environment variable REMOTE_CONFIG_OUT is not set. Using remote.conf."; }

OUTPUT_FILE="${REMOTE_CONFIG_OUT:-remote.conf}"
TEMP_FILE=$(mktemp --suffix ".conf")

# copy the remote config into a temporary file
cp "${REMOTE_CONFIG}" "${TEMP_FILE}"

# get the list of participant nodes needing tokens
nodes+=$(sed -n 's/^\s*\(\w\+\)\.token\s*=\s*".*"\s*$/\1/p' "${TEMP_FILE}")
echo 'Found these nodes with token fields in '"${REMOTE_CONFIG}"':'
echo '    '${nodes[@]}
