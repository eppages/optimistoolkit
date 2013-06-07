#!/bin/bash

#export OS_AUTH_URL=http://172.16.8.1:5000/v2.0
#export OS_TENANT_ID=f6cb51c22ef94d80823ec498d5f94355
#export OS_TENANT_NAME=optimis
#export OS_USERNAME=smendoza
#export OS_PASSWORD=77Lx61

#nova --os_username=smendoza --os-tenant-name=optimis --os-auth-url='http://172.16.8.1:5000/v2.0' --os-password='77Lx61' boot --image=e487d76e-ffdd-4356-9c9b-6f6356542262 --flavor=1 nombredelamaquina_25

nova boot --image=e487d76e-ffdd-4356-9c9b-6f6356542262 --flavor=1 nombredelamaquina_32

#nova boot --image=$1 --flavor=$2 $4
