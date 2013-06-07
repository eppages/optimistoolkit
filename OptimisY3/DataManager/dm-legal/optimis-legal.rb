#!/usr/bin/env ruby
# OPTIMIS Legal Service

APPDIR = File.expand_path(File.dirname(__FILE__)) unless defined?(APPDIR)
def fromLocal(name) File.join(APPDIR, name) end

require 'rubygems'
require 'msgpack/rpc'
require 'base64'
require 'digest/sha1'
require 'json'
require 'redis'
require 'tempfile'
require 'date'
require fromLocal 'legal.rb'

STDOUT.sync = true

class OptimisLegalService

 def retrieveCPDPath(provider)
     # replace with rest call
     remotecpdFilePath = "#{APPDIR}/signed_cpds/#{provider}.cpd"
     cpdContent = IO.read(remotecpdFilePath)

     cpdFile = Tempfile.new('cpd')
        cpdFile.write(cpdContent)
     cpdFile.close
     return cpdFile.path
 end 

 def checkLegal(sid, manifest, localProvider, remoteProvider)
    puts "\n[#{DateTime.now}] Check Legal with sid:#{sid}, localProvider:#{localProvider} remoteProvider: #{remoteProvider}"
	    
    return Legal.checkLegal(sid, manifest, localProvider, remoteProvider, retrieveCPDPath(remoteProvider) )
 end

 def checkLegalTest(sid, manifest, localProvider, remoteProvider, remoteCPDXML)
    puts "\n[#{DateTime.now}] Check Legal with sid:#{sid}, localProvider:#{localProvider} remoteProvider: #{remoteProvider}"
     cpdFile = Tempfile.new('cpd')
        cpdFile.write(remoteCPDXML)
     cpdFile.close

    return Legal.checkLegal(sid, manifest, 
                                    localProvider,
                                    remoteProvider, 
                                    cpdFile.path )
 end

end

PORT = 1998
service = MessagePack::RPC::Server.new
service.listen('localhost', PORT, OptimisLegalService.new)
puts "OPTIMIS DATAMANAGER LEGAL SERVICE LISTENING ON PORT #{PORT}!"
service.run


