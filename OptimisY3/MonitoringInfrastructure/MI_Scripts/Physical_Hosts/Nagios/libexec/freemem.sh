cat /proc/meminfo | grep MemFree | awk '{print $2, $3}'
