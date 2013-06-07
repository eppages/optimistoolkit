/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.providerinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import javax.ws.rs.*;

/**
 * @author hrasheed
 * 
 */
@Path(value = "/Service")
public class ProviderInfoService {

    private static final Logger LOG = Logger.getLogger(ProviderInfoService.class);
    
    public ProviderInfoService()
    {
        LOG.debug("initializing new instance of provider info service");
    }
    
    @GET
    @Path("getInfo")
    @Produces(value = "text/plain")
    public String getInfo()
    {
    	
    	FileInputStream stream = null;
    	
    	try {
    			 
    		URL url = getClass().getResource("/provider-info.properties");
    		File configFile = new File(url.toURI());
    	
    		stream = new FileInputStream( configFile );
    		
    		FileChannel fc = stream.getChannel();
    		
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		   
		    return Charset.defaultCharset().decode(bb).toString();
		  
    	} catch (Exception e) {
    		LOG.error("error in getting provider info contents from file: " + e.getMessage(), e);
    		} finally {
    			try {
    				if (stream != null)
    					stream.close();
    			} catch (IOException ex) {
    				LOG.error("error in getting provider info from config file: " + ex.getMessage(), ex);
    			}
    		}
    	
    	return null;
    }
}
