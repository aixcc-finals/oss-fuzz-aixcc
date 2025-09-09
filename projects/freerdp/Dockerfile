# Copyright 2023 Google LLC
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

FROM ghcr.io/aixcc-finals/base-builder:v1.3.0

# See https://github.com/FreeRDP/FreeRDP/wiki/Compilation
# Install Git and base dependences.
RUN apt-get update && apt-get install -y devscripts equivs \
	build-essential git-core cmake ninja-build pkg-config ccache

#RUN git clone --depth 1 https://github.com/FreeRDP/FreeRDP.git

WORKDIR $SRC
COPY pkgs/FreeRDP.tar.gz $SRC/FreeRDP.tar.gz
RUN tar -zxf $SRC/FreeRDP.tar.gz && rm $SRC/FreeRDP.tar.gz

WORKDIR FreeRDP

# Install all dependencies required by the nightly package.
RUN mk-build-deps --install --tool 'apt-get --yes --no-remove --no-install-recommends' packaging/deb/freerdp-nightly/control

COPY build.sh *.options $SRC/
