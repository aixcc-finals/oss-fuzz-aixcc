#!/bin/bash
apt install -y libxml2-dev
pip install pytest pytest-xdist
cmake -DBUILD_wireshark=NO -DBUILD_stratoshark=NO -DBUILD_sharkd=NO -DVCSVERSION_OVERRIDE="Git v3.1.0 packaged as 3.1.0-1" .
make
pytest --skip-missing-programs all -k 'not clopts' --disable-capture  --ignore test/suite_capture.py
err_code=$?
exit $err_code
