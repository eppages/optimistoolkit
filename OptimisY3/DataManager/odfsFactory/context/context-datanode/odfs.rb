require 'socket'
require 'rubygems'
require 'aef/init'
require 'net/http'
require 'uri'

class OptimisDaemon < Aef::Init
    def start
      system('echo start')
      url = URI.parse('http://slashdot.org/index.html')
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
      http.request(req)
      }

        sleep 5

        local_ip_tun = `ifconfig tun0`.match(/inet addr:(\d*\.\d*\.\d*\.\d*)/)[1] || "localhost"
       
      File.open('/root/odfs-log', 'w') do |f|
        f.puts local_ip_tun
        f.puts "test started at #{Time.now}"
        f.puts res.body
      end


    end

    def status
      puts "status"
    end

    def stop
      system('echo stop')
    end
end

  
OptimisDaemon.parse
