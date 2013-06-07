When /^a new object is created and stored to odoss$/ do
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/checkodoss?operation=putobject"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot put object") unless response.body == "OK"
end

Then /^the object retrieved is the same$/ do
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/checkodoss?operation=getobject"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot get object") unless response.body == "OK"
end

Then /^the object can be deleted$/ do
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/checkodoss?operation=deleteobject"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot delete objext") unless response.body == "OK"
end


