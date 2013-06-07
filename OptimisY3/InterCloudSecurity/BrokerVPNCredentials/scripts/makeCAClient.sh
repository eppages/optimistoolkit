#!/bin/bash
cd /opt/optimis/vpn/BrokerVPNCredentials/target/classes/
# Start the BrokerCA on the Broker server
java -cp ".:../BrokerVPNCredentials-3.0-jar-with-dependencies.jar" -Djava.rmi.server.hostname=217.33.61.85 -Djava.security.policy=../../casecurity.policy eu/optimis/ics/BrokerVPNCredentials/PeerCredManager peer 217.33.61.85

cd /etc/racoon/certs/
ln -s ca.crt `openssl x509 -hash -noout -in ca.crt`.0
