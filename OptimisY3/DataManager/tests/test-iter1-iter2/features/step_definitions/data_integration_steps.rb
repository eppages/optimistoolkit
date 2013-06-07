Given /^a file "([^"]*)" is uploaded to hdfs cluster$/ do |filename|
   @local_sha1 = `ruby upload.rb #{filename}`
end

When /^a DataNode is going down$/ do
  url = "http://109.231.81.106:8080/DataManagerAPI/testframework/killDataNode?sid=ehealth"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot kill a DataNode") unless response.body == "OK"
end

Then /^the file "([^"]*)" is accessible and is not corrupted$/ do |filename|
   @remote_sha1 = `ruby download.rb #{filename}`
   puts "Local_sha1(#{filename}) = #{@local_sha1}" 
   puts "Remote_sha1(#{filename}) = #{@remote_sha1}" 
   raise("Error: Data Corruption") unless @local_sha1 == @remote_sha1
end

Then /^the file "([^"]*)" can be deleted$/ do |filename|
  url = "http://130.239.48.114:8080/DataManagerAPI/odfs/delete?sid=ehealth&filename=#{filename}&recursive=false"
  response = Net::HTTP.get_response( URI.parse(url) )
  raise("Error: Cannot kill a DataNode") unless response.body == "TRUE"
end

