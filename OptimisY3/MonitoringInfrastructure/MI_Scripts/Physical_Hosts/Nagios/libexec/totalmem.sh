cat /proc/meminfo | grep MemTotal | awk '{print $2, $3}'
