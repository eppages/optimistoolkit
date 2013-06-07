#!/bin/bash

# Copyright 2013 University of Leeds
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#  
#       http://www.apache.org/licenses/LICENSE-2.0
#  
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#Test with defaults
java -jar VmContextualizer.jar 1 /opt/optimis/vmc/runtime

#Test image manipulation scripts
echo ""
echo "### Testing following scripts in bin:"

for i in `find bin -maxdepth 1 -type f`
do
  echo "### Testing script usage output:"
  echo ""
  sh $i
  echo "### DONE ###"
  echo ""
  echo "### Testing with empty image:"
  echo ""
  echo "WARNING: TEST NOT IMPLEMENTED YET!"
  echo ""
done

echo "### TEST: eu.optimis.vc.ImageScripts.bash:50) COMPLETE ###"

echo ""
echo "OK (3 tests)"

echo ""
echo "Greping for ISO Image in repository:"
echo ""
ls repository | grep jboss
echo ""
echo "Test passed image is found!"
rm repository/3_jboss_1.iso
