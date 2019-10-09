#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
npm run build
cp -rf "${DIR}/dist" "${DIR}/../limes-core/src/main/resources/web-ui"