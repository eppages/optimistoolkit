When /^the SD\/DO calls the DM with arguments the sid: "([^"]*)" and the "([^"]*)"$/ do |sid, iprovider|
  @iprovider = iprovider
  @sid = sid
end

Then /^the result of check legal should be "([^"]*)"$/ do |legalResult|
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/checklegal?sid=#{@sid}&iprovider=#{@iprovider}"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Wrong legal assessment!") unless response.body == legalResult
end


