From the workspace directory, run to make jar file:-

svn checkout --username ali.sajjad --password pakistan http://pandora.atosorigin.es/svn/optimis/branches/OptimisY2/InterCloudSecurity/P2PVPN
cd P2PVPN/
mvn clean package
echo "Peer Done"

To start the VPN Peer on any VM:-

Please make sure that ipsec-tools is installed and racoon is running on udp port 500

cd P2PVPN/target/
sudo java -jar P2PVPN-3.0-jar-with-dependencies.jar


To start the SuperPeer on the Broker:-

cd P2PVPN/target/classes/
sudo java -cp ".:../P2PVPN-3.0-jar-with-dependencies.jar" eu/optimis/ics/p2p/SuperPeer
