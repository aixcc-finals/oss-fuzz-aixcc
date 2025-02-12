#!/bin/bash -eux
# Copyright 2016 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
################################################################################

if [ "$1" = "--cache-from" ]; then
    PULL_CACHE=1
    shift
    CACHE_TAG="${1//\//-}"  # s/\//-/g -> for branch names that contain slashes
    shift
elif [ "$1" = "--cache-to" ]; then
    PUSH_CACHE=1
    shift
    CACHE_TAG="${1//\//-}"  # s/\//-/g -> for branch names that contain slashes
    shift
fi

ARG_TAG="$1"
shift

BASE_IMAGES=(
    "ghcr.io/aixcc-finals/base-image            infra/base-images/base-image"
    "ghcr.io/aixcc-finals/base-clang            infra/base-images/base-clang"
    "ghcr.io/aixcc-finals/base-builder          infra/base-images/base-builder"
    "ghcr.io/aixcc-finals/base-builder-go       infra/base-images/base-builder-go"
    "ghcr.io/aixcc-finals/base-builder-jvm      infra/base-images/base-builder-jvm"
    "ghcr.io/aixcc-finals/base-builder-python   infra/base-images/base-builder-python"
    "ghcr.io/aixcc-finals/base-builder-rust     infra/base-images/base-builder-rust"
    "ghcr.io/aixcc-finals/base-builder-ruby     infra/base-images/base-builder-ruby"
    "ghcr.io/aixcc-finals/base-builder-swift    infra/base-images/base-builder-swift"
    "ghcr.io/aixcc-finals/base-runner           infra/base-images/base-runner"
    "ghcr.io/aixcc-finals/base-runner-debug     infra/base-images/base-runner-debug"
)

for tuple in "${BASE_IMAGES[@]}"; do
    read -r image path <<< "$tuple"

    if [ "${PULL_CACHE+x}" ]; then

        docker buildx build \
            --build-arg IMG_TAG="${ARG_TAG}" \
            --cache-from=type=registry,ref="${image}:${CACHE_TAG}" \
            --tag "${image}:${ARG_TAG}" --push "$@" "${path}"

    elif [ "${PUSH_CACHE+x}" ]; then

        docker buildx build \
            --build-arg IMG_TAG="${ARG_TAG}" \
            --cache-from=type=registry,ref="${image}:${CACHE_TAG}" \
            --cache-to=type=registry,ref="${image}:${CACHE_TAG}",mode=max \
            --tag "${image}:${ARG_TAG}" --push "$@" "${path}"

    else

        docker buildx build \
            --build-arg IMG_TAG="${ARG_TAG}" \
            --tag "${image}:${ARG_TAG}" --push "$@" "${path}"

    fi

done

