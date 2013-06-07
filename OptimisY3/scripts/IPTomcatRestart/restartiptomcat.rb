#!/usr/bin/ruby

require 'net/http'

unless ENV["CATALINA_HOME"]
  puts "CATALINA_HOME environment variable must be set"
end

unless ENV["OPTIMIS_HOME"]
  puts "OPTIMIS_HOME environment variable must be set"
end

puts "WARNING: this is a beta version"

puts "* Looking for currently running IP Tomcat..."
`ps auxw`.split("\n").each do |process|
  if process.include? "java" and process.include?ENV["CATALINA_HOME"]
    pid = process.split("\s")[1]
    puts "\tFound IP Tomcat as PID=#{pid}. Killing process"
    `kill -9 #{pid}`
  end
end

puts "* Removing CloudOptimizer from Tomcat..."
`mv #{ENV["CATALINA_HOME"]}/webapps/CloudOptimizer.war /tmp/CloudOptimizer.war`
`rm -rf #{ENV["CATALINA_HOME"]}/webapps/CloudOptimizer*`

puts "* Starting Tomcat..."
`#{ENV["CATALINA_HOME"]}/bin/catalina.sh start`

puts "* Waiting for VMM..."

vmmready = nil
while vmmready.nil?
  begin
    resp = Net::HTTP.get(URI("http://localhost:8080/VMManager/info"))
    if resp == "DRP is running"
      vmmready = 1
    else
      puts "\tStill not ready: #{resp}"
      sleep 2
    end

  rescue Exception => e
    puts "\tStill not ready: #{e.to_s}"
    sleep 2
  end
end
puts "\tReady!"
puts "* Waiting for FTE..."
fteready = nil
while fteready.nil?
  begin
    resp = Net::HTTP.get(URI("http://localhost:8080/FTE/info"))
    if resp == "Ok"
      fteready = 1
    else
      puts "\tStill not ready: #{resp}"
      sleep 2
    end
  rescue Exception => e
    puts "\tStill not ready: #{e.to_s}"
    sleep 2
  end
end

puts "\tReady!"
puts "* Deploying Cloud Optimizer"
`mv /tmp/CloudOptimizer.war #{ENV["CATALINA_HOME"]}/webapps/CloudOptimizer.war `
puts "* Waiting for CloudOptimizer to be up and running..."
coready = nil
while coready.nil?
  begin
    resp = Net::HTTP.get(URI("http://localhost:8080/CloudOptimizer/physicalresources/ids"))
    if resp
      coready = 1
      puts resp
    else
      puts "\tStill not ready: #{resp}"
      sleep 2
    end
  rescue Exception => e
    puts "\tStill not ready: #{e.to_s}"
    sleep 2
  end
end
puts "\tReady!"
