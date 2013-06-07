God.watch do |w|
  w.name = "Optimis Legal Service"
  w.start = "ruby /usr/optimis/dm-legal/optimis-legal.rb"
  w.log = '/var/log/optimis-legal-service.log'
  w.keepalive
end

God.watch do |w|
  w.name = "Optimis Download Service"
  w.start = "python /usr/optimis/dm-download/optimis-download-server"
  w.log = '/var/log/optimis-download-service.log'
  w.keepalive
end

God.watch do |w|
  w.name = "Optimis Core Service"
  w.start = "ruby /usr/optimis/dm-core/dm-core.rb"
  w.log = '/var/log/optimis-core-service.log'
  w.keepalive
end

if File.exist?("/root/atos-security/atos.rb")
God.watch do |w|
  w.name = "Optimis ATOS Security Service"
  w.start = "ruby /root/atos-security/atos.rb"
  w.log = '/var/log/optimis-atos-service.log'
  w.keepalive
end
end

if File.exist?("/usr/optimis/dm-arsys/target/arsyswrapper-1.0-SNAPSHOT-jar-with-dependencies.jar")
God.watch do |w|
  w.name = "Optimis Arsys Downloader Service"
  w.start = "java -jar /usr/optimis/dm-arsys/target/arsyswrapper-1.0-SNAPSHOT-jar-with-dependencies.jar"
  w.log = '/var/log/optimis-arsys-downloader.log'
  w.keepalive
end
end


