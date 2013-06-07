#!/us/bin/env ruby

APPDIR = File.expand_path(File.dirname(__FILE__)) unless defined?(APPDIR)
def requireLocal(name) File.join(APPDIR, name) end

require 'rubygems'
require 'msgpack/rpc'

PORT = 1998
client = MessagePack::RPC::Client.new('127.0.0.1', 1998)

sid            = 'test'
manifest       = IO.read("#{APPDIR}/sample-manifests/manifest3.xml")
localProvider  = 'atos'
remoteProvider = 'umea'
result = client.call(:checkLegal, sid, manifest, localProvider, remoteProvider)

p result
