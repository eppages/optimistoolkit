#! /bin/sh
# Copyright 2012 University of Stuttgart
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 
# This script tests ICS functionalities
# make sure 'curl' is installed
# Author: Anthony Sulistio
# Usage: ./test_ics.sh [IP_address] [PORT]

curl=`which curl`
if [ -z "$curl" ]; then
    echo "Please install the 'curl' package before running this script."
    exit 1
fi

ICS_HOST="localhost"
if [ -n "$1" ]; then
    ICS_HOST=$1   
fi

# NOTE: the UMU 3 server uses 8087 as the port number due to Emotive installation
PORT=8080
if [ -n "$2" ]; then
    PORT=$2
fi
echo "Running this script on" $ICS_HOST "with port" $PORT

echo
echo "Get a list of available images"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image"
curl http://${ICS_HOST}:$PORT/ImageCreationService/image

echo
echo "-----------------------------------------------"
echo "Get an information of each individual image"
# NOTE: need to put -s for silent mode, i.e. don't show progress meter or error messages
list=`curl -s http://${ICS_HOST}:$PORT/ImageCreationService/image | awk -F',' '{print $1}' | awk -F':' '{print $2}'`
for i in $list
do
    echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$i"
    curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$i
    echo
done

echo "-----------------------------------------------"
echo "Get the status of each individual image"
for i in $list
do
    echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$i/status"
    curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$i/status
    echo
    echo
done

echo
echo "-----------------------------------------------"
echo "Create a Orchestration Element and prints an image ID"
ID=`curl -s -H"Content-Type:text/plain" -X POST http://${ICS_HOST}:$PORT/ImageCreationService/image -d "OrchestrationElement"`
echo "> curl -s -H"Content-Type:text/plain" -X POST http://${ICS_HOST}:$PORT/ImageCreationService/image -d "OrchestrationElement""

echo
echo "The new image ID is" $ID
echo
echo "Find out the detailed information of this image"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID"
curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID

n=30   # in seconds
echo
echo "Wait for" $n "seconds to see the image state changed from BUSY to READY"
sleep $n

echo
echo "Find out the new state of this image"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID"
curl -s http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID | grep --color=auto state

echo
echo "Find out the status of this image:"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/status"
curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/status

echo
echo "-----------------------------------------------"
file=$$.txt
echo "A random file for testing ICS" > $file

echo "Upload a txt file:" $file
echo "> curl -H'Content-Type: multipart/form-data' --form name="$file" --form file=@$file http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/file"
curl -H'Content-Type: multipart/form-data' --form name="$file" --form file=@$file http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/file

echo
echo "-----------------------------------------------"
zip=$$.zip
zip -q $zip $file

echo "Upload a zip file:" $zip
echo "> curl -H'Content-Type: multipart/form-data' --form name="$zip" --form file=@$zip http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/zip"
curl -H'Content-Type: multipart/form-data' --form name="$zip" --form file=@$zip http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/zip

echo
echo "-----------------------------------------------"
war=$$.war
cp $zip $war

echo "Upload a war file:" $war
echo "> curl -H'Content-Type: multipart/form-data' --form name="$war" --form file=@$war http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/war"
curl -H'Content-Type: multipart/form-data' --form name="$war" --form file=@$war http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/war

echo
echo "-----------------------------------------------"
echo "Finalize this image to be ready for downloading"
echo "> curl -X POST http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/finalize"
final=`curl -s -X POST http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID/finalize`

echo
echo "You can download the finalized image on the below link"
echo $final

echo
echo "The image state is changed from READY to FINALIZED"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID"
curl -s http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID | grep --color=auto -i finalized

echo
echo "-----------------------------------------------"
sleep 10
echo "Delete the image ID:" $ID
echo "> curl -X DELETE http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID"
curl -X DELETE http://${ICS_HOST}:$PORT/ImageCreationService/image/$ID

echo
echo "This will delete the below image:"
echo $final

echo
echo "Get the new list of available images"
echo "> curl http://${ICS_HOST}:$PORT/ImageCreationService/image"
curl http://${ICS_HOST}:$PORT/ImageCreationService/image

# cleanup the files 
rm -f $file $zip $war

echo
echo "--- End of Testing ---"
echo


