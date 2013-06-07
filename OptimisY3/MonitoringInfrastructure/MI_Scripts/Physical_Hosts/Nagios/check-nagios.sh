#! /bin/sh

## A script to check the Nagios installation

echo cd /usr/local/nagios/libexec
cd /usr/local/nagios/libexec
echo ./check_users -w 5 -c 10
./check_users -w 5 -c 10
echo ./check_disk -w 20% -c 10% -p /dev/sda1
./check_disk -w 20% -c 10% -p /dev/sda1
echo ./check_procs -w 5 -c 10 -s Z
./check_procs -w 5 -c 10 -s Z
echo ./check_procs -w 150 -c 200
./check_procs -w 150 -c 200
echo ./check_load -w 15,10,5 -c 30,25,20
./check_load -w 15,10,5 -c 30,25,20
echo ./check_disk -w 20% -c 10% -p /
./check_disk -w 20% -c 10% -p /
echo ./totalmem.sh
./totalmem.sh
echo ./freemem.sh
./freemem.sh
echo ./check_optimis_users.sh
./check_optimis_users.sh
echo ./check_optimis_mac_address.sh
./check_optimis_mac_address.sh
echo ./check_optimis_fqdn.sh
./check_optimis_fqdn.sh
echo ./noproc.sh
./noproc.sh
echo ./check_downstream.sh -i eth0
./check_downstream.sh -i eth0
echo ./check_upstream.sh -i eth0
./check_upstream.sh -i eth0
echo ./check_optimis_reboot.sh
./check_optimis_reboot.sh
echo ./check_status -H localhost -w 3000.0,80% -c 5000.0,100% -p 5
./check_status -H localhost -w 3000.0,80% -c 5000.0,100% -p 5
echo ./check_optimis_disk_total_space.sh
./check_optimis_disk_total_space.sh
echo ./check_optimis_cpu_speed.sh
./check_optimis_cpu_speed.sh
echo ./check_optimis_hardware_error.sh
./check_optimis_hardware_error.sh

