#!/usr/bin/env bash
#
# LIMES - LIMES – Link Discovery Framework for Metric Spaces.
# Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

if [ $# -ne 2 ]; then
  echo "Usage: $0 new_version new_snapshot_version"
  exit 1
fi
new_version="$1"
new_snapshot_version="$2"
tmpOut=$(mktemp /tmp/mvn-release-tmp-out.XXXXXX)
mvn verify > "$tmpOut" 2>&1
if [ $? -eq 0 ]; then
  mvn versions:set -DnewVersion="${new_version}" > "$tmpOut" 2>&1 && \
  mvn versions:commit >> "$tmpOut" 2>&1 && \
  git add . >> "$tmpOut" 2>&1 && \
  git commit -m "preparing release ${new_version}" >> "$tmpOut" 2>&1 && \
  git tag -a "${new_version}" -m "release ${new_version}" >> "$tmpOut" 2>&1 && \
  mvn versions:set -DnewVersion="${new_snapshot_version}" >> "$tmpOut" 2>&1 && \
  mvn versions:commit >> "$tmpOut" 2>&1 && \
  git add . >> "$tmpOut" 2>&1 && \
  git commit -m "preparing next development iteration" >> "$tmpOut" 2>&1
  if [ $? -ne 0 ]; then
    echo "Problem with release: Nothing has been pushed yet! Please consult the logs and resolve the issues before pushing!"
    cat "$tmpOut"
  else
    echo "Successfully authored release ${new_version}"
    git push origin "${new_version}"
    git push
  fi
else
  echo "Error in mvn verify, please see attached log!"
  cat "$tmpOut"
fi
rm "$tmpOut"