#/bin/bash

BASEDIR=$(dirname $0)

echo "Calling add_new_partition.sh..."
echo "Process starts at:" `date +%Y-%m-%d_%H:%M:%S`
$BASEDIR/add_new_partition.sh
echo "Process ends at:" `date +%Y-%m-%d_%H:%M:%S`

echo "Calling archive_old_mdata.sh..."
echo "Process starts at:" `date +%Y-%m-%d_%H:%M:%S`
$BASEDIR/archive_old_mdata.sh 6
echo "Process ends at:" `date +%Y-%m-%d_%H:%M:%S`

echo "Calling reduce_db_volume.sh..."
echo "Process starts at:" `date +%Y-%m-%d_%H:%M:%S`
$BASEDIR/reduce_db_volume.sh `date +%Y-%m` 6
echo "Process ends at:" `date +%Y-%m-%d_%H:%M:%S`

exit 0

