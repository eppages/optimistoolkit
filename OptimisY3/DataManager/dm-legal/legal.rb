#!/usr/bin/env ruby
#   Copyright 2013 National Technical University of Athens
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

require "base64"
require 'rexml/document'
include REXML

module Legal

APPDIR=File.expand_path(File.dirname(__FILE__)) unless defined?(APPDIR)

OPERATION_LEGAL   = "LEGAL"
OPERATION_ILLEGAL = "ILLEGAL"

class OptimisManifest

 def getParameters()
   return @parameters
 end
 
 def setManifest(xml)
   @parameters = Hash.new

   xmldoc = REXML::Document.new(xml)
   
   xmldoc.elements.each("opt:ServiceManifest") { |e|
       @parameters["opt:manifestId"] = e.attributes["opt:manifestId"]
   }

   xmldoc.elements.each("opt:ServiceManifest") { |e| 
      @parameters["opt:manifestId"] = e.attributes["opt:manifestId"]
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:VirtualMachineDescription") { |e| 
      @parameters["opt:isFederationAllowed"] = e.attributes["opt:isFederationAllowed"]
   }

   @parameters["eligible_country_list"] =  Array.new()

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:EligibleCountryList/opt:Country") { |e| 
      @parameters["eligible_country_list"].push(e.text)
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:DataProtectionLevel") { |e| 
       @parameters["opt:DataProtectionLevel"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:DataStorage/opt:AllocationUnit") { |e| 
      @parameters["opt:DataStorage/opt:AllocationUnit"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:DataStorage/opt:Capacity") { |e| 
      @parameters["opt:DataStorage/opt:Capacity"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:SCC/opt:apply") { |e| 
      @parameters["opt:SCC:apply"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:SCC/opt:Location") { |e| 
      @parameters["opt:SCC:Location"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:SCC/opt:Description") { |e| 
     @parameters["opt:SCC:Description"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:BCR/opt:apply") { |e|
     @parameters["opt:BCR:apply"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:BCR/opt:Location") { |e|
     @parameters["opt:BCR:Location"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:BCR/opt:Description") { |e|
     @parameters["opt:BCR:Description"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:IPR/opt:apply") { |e|
     @parameters["opt:IPR:apply"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:IPR/opt:Location") { |e|
     @parameters["opt:IPR:Location"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:IPR/opt:Description") { |e|
     @parameters["opt:IPR:Description"] = e.text
   }

   xmldoc.elements.each("opt:ServiceManifest/opt:DataProtectionSection/opt:IPR/opt:Rule/opt:Title") { |e|
     @parameters["opt:IPR:Title"] = e.text
   }

  end
end

def self.verifyBCRSign(fileName)
 begin  
   xmlfile = File.new(fileName)
   xmldoc = Document.new(xmlfile)

   puts "[#{fileName}] Check signature..."

   xmlfile = File.new(fileName)
   xmldoc = Document.new(xmlfile)

   bcr_text=""
   xmldoc.elements.each("tns:IaaSProvider/tns:LegalRequirements/tns:BCR/tns:BCR_Text") { |e|
     bcr_text = e.text()
    }

   File.open('.bcrtext', 'w') {|f| f.write(bcr_text) }
 
   `openssl dgst  -sha1 -out .digest .bcrtext`

   bcr_sign=""
   xmldoc.elements.each("tns:IaaSProvider/tns:LegalRequirements/tns:BCR/tns:DigitallySigned/tns:DigitalSignature") { |e|
     bcr_sign = e.text()
    }

   sign = Base64.decode64(bcr_sign)

   File.open('.signature', 'w') {|f| f.write(sign) }

   `openssl rsautl -verify -in .signature -out .verify-digest -inkey #{APPDIR}/ca/pub-key.pem -pubin`

    verify_digest = File.open(".verify-digest", 'r') { |f| f.read } 
    digest = File.open(".digest", 'r') { |f| f.read } 
    
    if verify_digest == digest
       puts "Legal: signature verified successfully"
       return OPERATION_LEGAL
    else
       puts "ILLegal: Cannot verify signature"
       puts "#{verify_digest}\n#{digest}"
       return OPERATION_ILLEGAL
    end
    
   `rm .signature .digest .bcrtext .verify-digest`  

   50.times {print "-" }
   puts ""

 rescue Exception => e 
    return OPERATION_ILLEGAL
 end  
end

def self.verifyIPR(manifestIPR, remoteCPDFilePath)
   xmlfile = File.new(remoteCPDFilePath)
   xmldoc = Document.new(xmlfile)

   iprlist = Array.new()
   xmldoc.elements.each("tns:IaaSProvider/tns:LegalRequirements/tns:IPRcompliance/tns:level") { |e|
      cpdIPR = e.text()
      if cpdIPR.casecmp(manifestIPR).zero?
        return OPERATION_LEGAL 
      end 
   }

   return OPERATION_ILLEGAL
end

def self.checkLegal(sid, manifest, localProvider, remoteProvider, remoteCPDFilePath)
   result    = OPERATION_LEGAL
   extractor = OptimisManifest.new
   extractor.setManifest(manifest)
   params = extractor.getParameters()
   scc = params["opt:SCC:apply"]

   if scc.casecmp("false").zero?
      result = OPERATION_ILLEGAL
   end

   bcrCheck = verifyBCRSign(remoteCPDFilePath)

   if bcrCheck.casecmp(OPERATION_ILLEGAL).zero?
      result = OPERATION_ILLEGAL
   end

   ipr      = params["opt:IPR:apply"]
   iprlevel = params["opt:IPR:Title"]
   
   if ipr.casecmp("true")
      statusIPR = verifyIPR(iprlevel, remoteCPDFilePath)
      if statusIPR.casecmp(OPERATION_ILLEGAL).zero?
         result = OPERATION_ILLEGAL        
      end
   end
 
   return result  
end


end
