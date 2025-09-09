#!/bin/bash -eu
# Copyright 2025 Google LLC
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

# shadowsocks has submodules, untar them into the correct place from $SRC/aixcc-submodules
tar -zxf ../aixcc-submodules/submodules.tar.gz

apt-get install --no-install-recommends -y pkg-config gettext build-essential \
    autoconf libtool libpcre3-dev asciidoc xmlto libev-dev libc-ares-dev      \
    automake libmbedtls-dev libsodium-dev
./autogen.sh
./configure --disable-shared --enable-static
make V=1

cp ${SRC}/*.options ${OUT}/

$CC $CFLAGS $LIB_FUZZING_ENGINE -O0 $SRC/json_fuzz.c \
    ./src/.libs/libshadowsocks-libev.a \
    ./libipset/.libs/libipset.a \
    ./libbloom/.libs/libbloom.a \
    ./libcork/.libs/libcork.a \
    -l:libcrypto.a \
    -l:libpcre.a \
    -l:libsodium.a \
    -l:libcares.a \
    -l:libpcre.a \
    -l:libmbedcrypto.a \
    -l:libev.a \
    -l:libcares.a \
    -I./src/ -o $OUT/json_fuzz
