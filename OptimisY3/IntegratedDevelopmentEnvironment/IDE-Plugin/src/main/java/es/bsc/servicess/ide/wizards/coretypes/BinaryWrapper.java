/*
 *  Copyright 2011-2013 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package es.bsc.servicess.ide.wizards.coretypes;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.core.search.StringOperation;

import es.bsc.servicess.ide.TitlesAndConstants;
import es.bsc.servicess.ide.model.CoreElementParameter;
import es.bsc.servicess.ide.model.MethodCoreElement;
import es.bsc.servicess.ide.model.Parameter;


public class BinaryWrapper {
	
	private static final String STR_PREFIX = "strFile";
	public static final String VAR_SEP_REGEX="\\$";
	public static final String VAR_SEP="$";

	public static String createBinaryString(String exec, String indent) {
		return indent+"cmd = cmd.concat("+convertToCode(exec)+");";
		
	}
	
	public static String createArgumentString(String arg, String indent){
		return indent+"cmd = cmd.concat("+ convertToCode(arg)+");";
	}

	public static String convertToCode(String str) {
		String code = new String();
		int lastPos = 0;
		int sepPos = str.indexOf(VAR_SEP);
		while (sepPos >= 0){
			//There is something before the variable
			if (lastPos != sepPos){
				if (lastPos != 0)
					code = code.concat("+");
				code = code.concat("\""+ str.substring(lastPos, sepPos)+"\"");
			}
			
			int nextPos = str.indexOf(VAR_SEP, sepPos+VAR_SEP.length());
			//Exists a second variable separator and is 
			if (nextPos >0 && nextPos > sepPos+VAR_SEP.length()){
				if (sepPos != VAR_SEP.length()-1)
					code = code.concat("+");
				code = code.concat(str.substring(sepPos+VAR_SEP.length(),nextPos));
				lastPos = nextPos+VAR_SEP.length();
				sepPos = str.indexOf(VAR_SEP, lastPos);
			}else if (nextPos < 0){
				if (sepPos != VAR_SEP.length()-1)
					code = code.concat("+");
				code = code.concat(str.substring(sepPos+VAR_SEP.length(), str.length()));
				lastPos = str.length();
				sepPos = -1;
			}else	
				lastPos = sepPos+VAR_SEP.length();	
		}
		if (lastPos < str.length()){
			if (lastPos != 0)
				code = code.concat("+");
			code = code.concat("\""+str.substring(lastPos, str.length())+"\"");
		}

		return code;
	}
	
	public static boolean checkString(String str){
		int i=0;
		int sepPos = str.indexOf(VAR_SEP);
		while (sepPos>=0){
			i++;
			sepPos = str.indexOf(VAR_SEP, sepPos+VAR_SEP.length());
		}
		return (i%2==0);
	}
	
	public static List<String> getParameters(String str, List<String> params){
		if (params == null)
			params = new ArrayList<String>();
		int sepPos = str.indexOf(VAR_SEP);
		while (sepPos>=0){
			int nextSepPos = str.indexOf(VAR_SEP, sepPos+VAR_SEP.length());
			if (nextSepPos<0){
				params.add(str.substring(sepPos+VAR_SEP.length(), str.length()));
				break;
			}
			params.add(str.substring(sepPos+VAR_SEP.length(), nextSepPos));
			sepPos = str.indexOf(VAR_SEP, nextSepPos+VAR_SEP.length());
		}
		return params;
	}
	
	public static void main(String[] args) {
        try {
        	
        	System.out.println(createBinaryString("/absolutePath/$tmpDir$/binaryFolder/$binary$", ""));
        	System.out.println(createBinaryString("/absolutePath/$tmpDir$/binaryFolder/$binary_path$/binary",""));
        	System.out.println(createBinaryString("$tmpDir$/binaryFolder/$binary$",""));
        	System.out.println(createBinaryString("$tmpDir$/$rel_dir$/binaryFolder/binary",""));
        	System.out.println(createBinaryString("$tmpDir$$rel_dir$/binaryFolder/binary",""));
        	System.out.println(createBinaryString("$binary$",""));
        	System.out.println(createBinaryString("binary",""));
        	System.out.println(createBinaryString("$binary",""));
        	System.out.println(createBinaryString("in $binary$",""));
        	System.out.println(checkOutputs("$tmpDir$/$rel_dir$/binaryFolder/binary"));
        	System.out.println(checkOutputs("$binary"));
        	System.out.println(checkOutputs("binary"));
        	System.out.println(checkOutputs("$binary$"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static boolean checkOutputs(String str) {
		int i=0;
		if (str.startsWith(VAR_SEP)){
			int sepPos = str.indexOf(VAR_SEP, VAR_SEP.length());
			if (sepPos==str.length()-1){
				return true;
			}
		}
		return false;
	}
	/** Create the string to introduce in the process standard input
		- if a parameter in the stdin is a File introduce their content 
		- if a parameter in the stdin is String introduce its value 
		- if a parameter in the stdin is other Object it has to implement 
		the .toString() method 
	 * @throws Exception */ 
	public static String createStandardInputString(String stdin,
			MethodCoreElement ce, String indent) throws JavaModelException {
		if (stdin.trim().length()>0){	
			String varPreProcessCode = new String();
			String code = new String();
			int lastPos = 0;
			int sepPos = stdin.indexOf(VAR_SEP);
			while (sepPos >= 0){
				//There is something before the variable
				if (lastPos != sepPos){
					if (lastPos != 0)
						code = code.concat("+");
					code = code.concat("\""+ stdin.substring(lastPos, sepPos)+"\"");
				}
				int nextPos = stdin.indexOf(VAR_SEP, sepPos+VAR_SEP.length());
				//Exists a second variable separator and is 
				if (nextPos >0 && nextPos > sepPos+VAR_SEP.length()){
					String varName = stdin.substring(sepPos+VAR_SEP.length(),nextPos);
					System.out.println("Evaluating variable name : "+ varName+ " (From p."+sepPos+VAR_SEP.length()+" to p."+nextPos+")");
					if (varName!= null && varName.length()>0 && !varName.equals(TitlesAndConstants.RETURNTYPE)){
						if (sepPos != VAR_SEP.length()-1)
							code = code.concat("+");
						String type = getType(varName,ce);
						if (type.equals(TitlesAndConstants.STRING) || type.equals(TitlesAndConstants.JAVA_LANG + "." + TitlesAndConstants.STRING)){
							code = code.concat(varName);
						}else if (type.equals(CoreElementParameter.FILE)||(type.equals(CoreElementParameter.FILE_ALONE))){
							varPreProcessCode = varPreProcessCode.concat(createReadFromFileCode(varName, indent));
							code = code.concat(STR_PREFIX + varName);
						}else if (Signature.createTypeSignature(type, false).startsWith("Q")){
							code = code.concat(varName+".toString()");
						}else
							code = code.concat(varName);
					}
					lastPos = nextPos+VAR_SEP.length();
					sepPos = stdin.indexOf(VAR_SEP, lastPos);
				}else if (nextPos < 0){
					//there is not a new separator
					String varName =stdin.substring(sepPos+VAR_SEP.length(), stdin.length());
					System.out.println("Evaluating variable name : "+ varName+ " (From p."+sepPos+VAR_SEP.length()+" to p."+nextPos+")");
					if (varName!= null && varName.length()>0 && !varName.equals(TitlesAndConstants.RETURNTYPE)){
						if (sepPos != VAR_SEP.length()-1)
							code = code.concat("+");
						String type = getType(varName,ce);
						if (type.equals(TitlesAndConstants.STRING) || type.equals(TitlesAndConstants.JAVA_LANG+"."+ TitlesAndConstants.STRING)){
							code = code.concat(varName);
						}else if (type.equals(CoreElementParameter.FILE)||(type.equals(CoreElementParameter.FILE_ALONE))){
							varPreProcessCode = varPreProcessCode.concat(createReadFromFileCode(varName, indent));
							code = code.concat(STR_PREFIX + varName);
						}else if (Signature.createTypeSignature(type, false).startsWith("Q")){
							code = code.concat(varName+".toString()");
						}else
							code = code.concat(varName);
					}
					lastPos = stdin.length();
					sepPos = -1;
				}else	
					lastPos = sepPos+VAR_SEP.length();	
			}
			if (lastPos < stdin.length()){
				if (lastPos != 0)
					code = code.concat("+");
				code = code.concat("\""+stdin.substring(lastPos, stdin.length())+"\"");
			}
			String codeFragment = new String(indent+"java.io.BufferedOutputStream stdinStream = new java.io.BufferedOutputStream(execProc.getOutputStream());\n");
			codeFragment = codeFragment.concat(indent+"stdinStream.write(new String("+code+").getBytes());\n");
			codeFragment = codeFragment.concat(indent+"stdinStream.flush();\n");
			codeFragment = codeFragment.concat(indent+"stdinStream.close();\n");
			return varPreProcessCode+"\n" + codeFragment;
		}else{
			return (indent+"execProc.getOutputStream().close();\n");
		}
		
	}

	private static String createReadFromFileCode(String varName, String indent) {
		String code = new String(indent+"StringBuilder "+varName+"_sb = new StringBuilder();\n");
		code = code.concat(indent+"java.io.BufferedReader "+varName+"_br = new java.io.BufferedReader(new java.io.FileReader("+varName+"));\n");
		code = code.concat(indent+"String " + varName + "_line;\n");
		code = code.concat(indent+"while ((" + varName + "_line = " + varName + "_br.readLine()) != null)\n");
		code = code.concat(indent+"\t"+varName+"_sb.append(" + varName + "_line);\n");
		code = code.concat(indent+varName+"_br.close();\n");
		code = code.concat(indent+"String "+ STR_PREFIX+varName+" = " +varName+ "_sb.toString();\n");
		return code;
		
	}

	private static String getType(String varName, MethodCoreElement ce) throws JavaModelException {
		if (ce!=null){ 
			if (ce.getParameters()!= null && ce.getParameters().size()>0){
				for (Parameter p:ce.getParameters()){
					System.out.println("Comparing " + varName +" with "+ p.getName() );
					if (varName.equals(p.getName())){
						return p.getType();
					}
				}
				throw (new JavaModelException(new Exception("Error parameter "+varName+ "not found"),JavaModelStatus.ERROR));
			}else{
				throw (new JavaModelException(new Exception("Error: there are no parameter defined in the core element "),JavaModelStatus.ERROR));
			}
		}else{
			throw (new JavaModelException(new Exception("Error: Core element is null "),JavaModelStatus.ERROR));
		}
			
	}

	public static String createStandardStreamsRedirectionString(String stderr, String streamVarName, String streamCreationMethod,
			MethodCoreElement ce, String indent) throws JavaModelException {
		/* TODO create the string object or file to store the process standard output 
		- if a parameter in the stdout is a File introduce the process output in the file; 
		- if a parameter in the stdout is a String introduce the process output in the String 
		- if a parameter in the stdout is an Object (currently not supported) 
		(possibly create an object with the constructors as a file?)*/
		if(stderr.trim().length()>0){
			String code = new String();
			if (stderr.startsWith(VAR_SEP)){
				int sepPos = stderr.indexOf(VAR_SEP, VAR_SEP.length());
				if (sepPos==stderr.length()-1){
					String varName =stderr.substring(VAR_SEP.length(), stderr.length()-1);
					System.out.println("Evaluating variable name : "+ varName+ " (From p."+VAR_SEP.length()+" to p."+(stderr.length()-1)+")");
					if (varName!= null && varName.length()>0 && !varName.equals(TitlesAndConstants.RETURNTYPE)){
						code = code.concat(indent+ "java.io.InputStream " + streamVarName+ " = " + streamCreationMethod+";\n");
						String type = getType(varName,ce);
						if (type.equals(TitlesAndConstants.STRING) || type.equals(TitlesAndConstants.JAVA_LANG + "." + TitlesAndConstants.STRING)){
							code = code.concat(generateWriteInputStreamToString(streamVarName, varName, indent ));
						}else if (type.equals(CoreElementParameter.FILE)||(type.equals(CoreElementParameter.FILE_ALONE))){
							code = code.concat(generateWriteInputStreamToFile(streamVarName, varName, indent ));
						}else if (Signature.createTypeSignature(type, false).startsWith("Q")){
							//TODO 
						}else
							code = code.concat(varName);
						code = code.concat(indent+streamVarName+".close();\n");
					}else if (varName!= null && varName.length()>0 && varName.equals(TitlesAndConstants.RETURNTYPE)){
						code = code.concat(indent+ "java.io.InputStream " + streamVarName+ " = " + streamCreationMethod+";\n");
						code = code.concat(generateWriteInputStreamToString(streamVarName, TitlesAndConstants.RETURNTYPE, indent ));
					}
				}else
					throw (new JavaModelException(new Exception("Error: incorrect format for string "+ stderr),JavaModelStatus.ERROR));
			}
			return code;
		}else
			return generateEmptyStreams(streamVarName, streamCreationMethod, indent);
		
	}

	private static String generateEmptyStreams(String streamVarName, String streamCreationMethod, String indent) {
		/// Read error to prevent the subprocess from blocking
		String code = new String(indent+"java.io.BufferedInputStream "+ streamVarName+"_bis = new java.io.BufferedInputStream("+streamCreationMethod+");\n");
		code = code.concat(indent+"byte[]" +streamVarName+"_b = new byte[1024];\n");
		code = code.concat(indent+"while ("+ streamVarName+"_bis.read(" + streamVarName +"_b) >= 0);\n");
		code = code.concat(indent+ streamVarName +"_bis.close();\n");
		code = code.concat(indent+streamCreationMethod+".close();\n");
		return code;
	}

	private static String generateWriteInputStreamToFile(String streamVarName,
			String varName, String indent) {

		String code = new String(indent+"java.io.BufferedInputStream "+ streamVarName+"_bis = new java.io.BufferedInputStream("+streamVarName+");\n");
		code = code.concat(indent+"java.io.BufferedOutputStream "+ varName+"_bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream("+varName+"));\n");

		code = code.concat(indent+"int "+varName+"_bytes;\n");
		code = code.concat(indent+"byte[]" +varName+"_b = new byte[1024];\n");

		code = code.concat(indent+"while (("+varName+"_bytes = "+ streamVarName+"_bis.read(" + varName +"_b)) >= 0)\n");
		code = code.concat(indent+"\t"+ varName+"_bos.write(" +varName+"_b, 0, "+varName+"_bytes);\n");

		code = code.concat(indent+ streamVarName +"_bis.close();\n");
		code = code.concat(indent+ varName + "_bos.close();\n");
		return code;
	}

	private static String generateWriteInputStreamToString(String streamVarName,
			String varName, String indent) {
		String code = new String(indent+"StringBuilder "+varName+"_sb = new StringBuilder();\n");
		code = code.concat(indent +"java.io.BufferedReader "+streamVarName+"_br = new java.io.BufferedReader(new java.io.InputStreamReader("+streamVarName+"));\n");
		code = code.concat(indent +"String " + streamVarName + "_line;\n");
		code = code.concat(indent +"while ((" + streamVarName + "_line = "+streamVarName+"_br.readLine()) != null)\n");
		code = code.concat(indent +"\t"+varName+"_sb.append(" + streamVarName + "_line);\n");

		code = code.concat(indent + streamVarName+"_br.close();\n");
		code = code.concat(indent + varName +" = " +varName + "_sb.toString();\n");
		return code;
	}

	public static String createEnvironmentVarsCode(
			Map<String, String> keyValueMap, String indent) {
		String code = new String(indent+" java.util.Map<String, String> env = pb.environment();\n");
		code = code.concat(indent+" env.put(\"WISECONFIGDIR\", \"/optimis_service/wise2.2.0/wisecfg/\");\n");
		if (keyValueMap!=null && keyValueMap.size()>0){
			for(Entry<String, String> e:keyValueMap.entrySet()){
				code = code.concat(indent+" env.put("+convertToCode(e.getKey())+", "+convertToCode(e.getValue())+");\n");
				
			}
		}
		return code;
	}

	public static String createCommandCode(String exec, List<String> args,
			String indent) {
		int command_size = args.size()+1;
		String methodBody = new String(indent+"String[] cmd = new String["+command_size+"];\n");
		methodBody = methodBody.concat(indent+"cmd[0] = "+convertToCode(exec)+";\n");
		for (int i=0;i<args.size(); i++){
			int iterator=i+1;
			methodBody = methodBody.concat("\t cmd["+iterator+"] ="+convertToCode(args.get(i))+ ";\n");
			
		}
		return methodBody;
	}

}
