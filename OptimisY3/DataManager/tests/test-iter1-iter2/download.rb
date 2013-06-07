require "net/http"
require "uri"
require 'digest/sha1'

filename = ARGV[0]

uri = URI.parse("http://130.239.48.114:8080/DataManagerAPI/odfs/download?filename=/user/root/#{filename}")

response = Net::HTTP.get_response(uri)

#puts response.body
puts Digest::SHA1.hexdigest response.body

