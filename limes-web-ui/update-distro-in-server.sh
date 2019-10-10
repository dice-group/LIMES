#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
rm -rf "${DIR}/../limes-core/src/main/resources/web-ui"
git rm --cached -r "${DIR}/../limes-core/src/main/resources/web-ui"
pushd "${DIR}";
npm install
npm run build
popd;
cp -rf "${DIR}/dist/" "${DIR}/../limes-core/src/main/resources/web-ui"
git add "${DIR}/../limes-core/src/main/resources/web-ui"