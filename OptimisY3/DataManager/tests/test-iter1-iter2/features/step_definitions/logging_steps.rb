Then /^the logs monitored read access "([^"]*)"$/ do |bytes|
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/checklogs?sid=ehealth&filename=thecloud.txt"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot kill a DataNode") unless response.body == bytes
end

