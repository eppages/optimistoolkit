README
* To create an image with qcow2 format (size 5 GB):
qemu-img create -f qcow2 ubuntu_5GB.qcow2 5G

* To install the newly-created image (use either qemu 32- or 64-bit command) in Ubuntu:
qemu-system-i386   -m 1024 -boot d -enable-kvm -hda ubuntu_5GB.qcow2 -cdrom ubuntu-12.04.1-server-i386.iso
qemu-system-x86_64 -m 1024 -boot d -enable-kvm -hda ubuntu_5GB.qcow2 -cdrom ubuntu-12.04.1-server-amd64.iso

* To run the image in Ubuntu:
qemu-system-i386   -m 1024 -net nic,model=e1000 -net user -enable-kvm -hda ubuntu_5G.qcow2
qemu-system-x64_64 -m 1024 -net nic,model=e1000 -net user -enable-kvm -hda ubuntu_5G.qcow2

* Once the image is running, upload the below script and run it to install packages:
  $ ./configuration/centos.sh          -- for CentOS 6.3 image
  $ ./configuration/ubuntu/ubuntu.sh   -- for Ubuntu Server 12.04 image
    NOTE: For the Ubuntu image, also upload configuration (*.conf) files into the image

  To change the root password in the CentOS image:
  $ echo root_pwd > blah.txt                   (change the "root_pwd" !!)
  $ cat blah.txt | passwd --stdin root
  
  To change the root password in the Ubuntu image:
  $ echo "root:root_pwd" > blah.txt            (change the "root_pwd" !!)
  $ echo "optimis:optimis_pwd" >> blah.txt     (change the "optimis_pwd" !!)
  $ chpasswd < blah.txt

