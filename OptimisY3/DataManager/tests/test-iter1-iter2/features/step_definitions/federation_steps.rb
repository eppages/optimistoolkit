require "uri"
require "net/http"

Given /^a DM cluster is deployed in "([^"]*)" with sid "([^"]*)"$/ do |iprovider, sid| 
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/AccountExists?sid=#{sid}&iprovider=#{iprovider}"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: #{sid} has not been created in #{iprovider}") unless response.body == "OK"
end

When /^risk Assesment module suggests federation$/ do
   url = "http://213.27.211.117:8080/DataManagerAPI/federation/undeploy?sid=ehealth"
   x = Net::HTTP.get_response( URI.parse(url) )
   puts x.body

end

Then /^create a federation VM in "([^"]*)"$/ do |iprovider|
    url = "http://213.27.211.117:8080/DataManagerAPI/federation/start?provider=flex&sid=ehealth&numnodes=1&memory=256"
    x = Net::HTTP.get_response( URI.parse(url) )
    puts x.body
end

Then /^the new DataNode is attached to the cluster$/ do
    sleep(80)
    url="http://109.231.81.106:8080/DataManagerAPI/risk/parameters"
    x = Net::HTTP.get_response( URI.parse(url) )
    res = x.body

    if res.index("ESP").nil?
      raise("Error: DataNode has not been attached!!")
    else
      puts "OK: DataNode attached successfully!"
    end
end


