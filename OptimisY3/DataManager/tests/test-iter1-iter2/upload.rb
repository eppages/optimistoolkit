require 'rubygems'
require 'ruby-multipart-post'
require 'digest'

def fileSHA1(filename)
  sha1 = Digest::SHA1.new

  File.open(filename) do|file|
     buffer = ''

    # Read the file 512 bytes at a time
    while not file.eof
       file.read(512, buffer)
       sha1.update(buffer)
    end
   end

  return sha1
end

filename = ARGV[0]

params = {
   "file" => FileUploadIO.new(filename, "application/octet-stream")
}


multipart_post = MultiPart::Post.new(params)
multipart_post.submit("http://130.239.48.114:8080/DataManagerAPI/odfs/upload")
puts fileSHA1(filename)
