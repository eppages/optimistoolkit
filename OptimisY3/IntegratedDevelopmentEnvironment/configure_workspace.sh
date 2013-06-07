#!/bin/sh

if test $# -ne 1 || test "$1" = "--help" || test "$1" = "-help" || test "$1" = "-h" ; then
	echo Usage\: $0 \<SVN Y3 location folder\>
	exit 1
fi
newpath="$1"

if test -e "$newpath"/IntegratedDevelopmentEnvironment/.last_path ; then
	lastpath=`cat "$newpath"/IntegratedDevelopmentEnvironment/.last_path`
else
	echo "Error: cannot find the '.last_path' file."
	exit 1
fi

echo Setting up the SVN IDE Folder path to "'"$newpath"'"
echo The old path was: "'"$lastpath"'"
newpath_esc=$(echo $newpath | sed 's/\//\\\//g')
lastpath_esc=$(echo $lastpath | sed 's/\//\\\//g')

echo Processing metadata files
mv "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/TestProject/META-INF/metadata.xml "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old && (cat "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old | sed "s/$lastpath_esc/$newpath_esc/g") > "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/TestProject/META-INF/metadata.xml
rm "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old
mv "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_1/META-INF/metadata.xml "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old && (cat "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old | sed "s/$lastpath_esc/$newpath_esc/g") > "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_1/META-INF/metadata.xml
rm "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old
mv "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_2/META-INF/metadata.xml "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old && (cat "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old | sed "s/$lastpath_esc/$newpath_esc/g") > "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_2/META-INF/metadata.xml
rm "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old
mv "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_3/META-INF/metadata.xml "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old && (cat "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old | sed "s/$lastpath_esc/$newpath_esc/g") > "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter_1_Feat_3/META-INF/metadata.xml
rm "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old
mv "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter2_Feat_4/META-INF/metadata.xml "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old && (cat "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old | sed "s/$lastpath_esc/$newpath_esc/g") > "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/Test_Iter2_Feat_4/META-INF/metadata.xml
rm "$newpath"/IntegratedDevelopmentEnvironment/test/workspace/.metadata.xml.old
echo $newpath > "$newpath"/IntegratedDevelopmentEnvironment/.last_path

