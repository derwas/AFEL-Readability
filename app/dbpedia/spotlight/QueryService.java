package dbpedia.spotlight;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.DBPediaParsedResource;

import play.Logger;
import javax.inject.*;
import play.api.Configuration;
import play.api.Play;

public class QueryService {
	
    //private static String  API_URL    = "http://140.203.154.200:8010/";
    private static  double  CONFIDENCE = 0.5;
    private static  int     SUPPORT    = 0;
    private static  String  powered_by ="non";
    private static  String  spotter ="Default";//"LingPipeSpotter"=Annotate all spots 
                                                //AtLeastOneNounSelector"=No verbs and adjs.    
                                                //"CoOccurrenceBasedSelector" =No 'common words'
                                                //"NESpotter"=Only Per.,Org.,Loc.
    private static String  disambiguator ="Default";//Default ;Occurrences=Occurrence-centric;Document=Document-centric
    private static String  showScores ="yes";
    
    private static HttpClient client = new HttpClient();
    
    public static JSONObject getdbpediaSpotlightJSON(String text, String API_URL) {
    	 	
  

		Logger.info("Querying API.");
		String spotlightResponse=null;
		PostMethod postMethod = new PostMethod(API_URL + "rest/annotate/?");
		NameValuePair[] data = {new NameValuePair("confidence" , CONFIDENCE+""),
								new NameValuePair("support" , SUPPORT+""),
								new NameValuePair("text" , text)};
		postMethod.addRequestHeader(new Header("Accept", "application/json"));
		postMethod.setRequestBody(data);

		spotlightResponse = request(postMethod);

		assert spotlightResponse != null;

		JSONObject resultJSON = null;

		try {
			resultJSON = new JSONObject(spotlightResponse);
		} catch (JSONException e) {
			Logger.info("Received invalid response from DBpedia Spotlight API.");
		}

		


		return resultJSON;
	}
    
    public static String JSONtoHTML(JSONObject jsonObject){
    	String result ="";
    	Map<String,DBPediaParsedResource> resources = new HashMap();
    	
    	JSONArray  entities = jsonObject.getJSONArray("Resources");
    	
    	for (int i = 0; i < entities.length(); i++) {  
    	     JSONObject childJSONObject = entities.getJSONObject(i);
    	     String uri = childJSONObject.getString("@URI");
    	     String surfaceForm     = childJSONObject.getString("@surfaceForm");
    	     String types = childJSONObject.getString("@types");
    	     
    	     if (resources.containsKey(uri)){
    	    	 DBPediaParsedResource r = resources.get(uri);
    	    	 r.surfaceForm.add(surfaceForm);
    	    	 r.count++;
    	     }
    	     else{
    	    	 resources.put(uri, new DBPediaParsedResource(uri, surfaceForm, types));
    	     }
    	     
    	     
    	}
    	
    	Map<String,DBPediaParsedResource> resourcesSorted = new LinkedHashMap<>(); 
    	
    	resources.entrySet().stream()
        .sorted(Map.Entry.<String, DBPediaParsedResource>comparingByValue().reversed())
        .forEachOrdered(x -> resourcesSorted.put(x.getKey(), x.getValue()));
    	
    	resources = resourcesSorted;
    	
    	for(String key: resources.keySet()){
    		if (resources.get(key).surfaceForm.size() > 1){
    		result = result + "<a href = \""+key+"\" target=\"_blank\">"+
    					resources.get(key).surfaceForm.iterator().next()+ "</a> (<a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\""+
    				resources.get(key).getSurfaceForms()+"\">"+
    				resources.get(key).count+"</a>),  ";
    		}
    		else{
    			result = result + "<a href = \""+key+"\" target=\"_blank\">"+
    					resources.get(key).surfaceForm.iterator().next()+ "</a> ("+
    				resources.get(key).count+"),  ";
    		}
    		
    		
    	}
    	result = result.substring(0,result.length()-2);
    	
    	return result;
    }
    
    
    public static String JSONtoJSONString(JSONObject jsonObject){
    	String result ="{";
    	Map<String,DBPediaParsedResource> resources = new HashMap();
    	
    	JSONArray  entities = jsonObject.getJSONArray("Resources");
    	
    	for (int i = 0; i < entities.length(); i++) {  
    	     JSONObject childJSONObject = entities.getJSONObject(i);
    	     String uri = childJSONObject.getString("@URI");
    	     String surfaceForm     = childJSONObject.getString("@surfaceForm");
    	     String types = childJSONObject.getString("@types");
    	     
    	     if (resources.containsKey(uri)){
    	    	 DBPediaParsedResource r = resources.get(uri);
    	    	 r.surfaceForm.add(surfaceForm);
    	    	 r.count++;
    	     }
    	     else{
    	    	 resources.put(uri, new DBPediaParsedResource(uri, surfaceForm, types));
    	     }
    	     
    	     
    	}
    	
    	Map<String,DBPediaParsedResource> resourcesSorted = new LinkedHashMap<>(); 
    	
    	resources.entrySet().stream()
        .sorted(Map.Entry.<String, DBPediaParsedResource>comparingByValue().reversed())
        .forEachOrdered(x -> resourcesSorted.put(x.getKey(), x.getValue()));
    	
    	resources = resourcesSorted;
    	
    	result = result + "\"nbConcepts\":" + resources.size()+","+
    				"\"concepts\":[";
    	for(String key: resources.keySet()){
    		if (resources.get(key).surfaceForm.size() > 1){
    		result = result + "{\"URI\":\""+key+"\","+
    				"\"surfaceForm\":\""+resources.get(key).surfaceForm.iterator().next()+ "\","+
    				"\"otherSurfaceForms\":\""+resources.get(key).getSurfaceForms()+"\","+
    				"\"occurrence\":"+resources.get(key).count+"},";
    		}
    		else{
    			result = result + "{\"URI\":\""+key+"\","+
        				"\"surfaceForm\":\""+resources.get(key).surfaceForm.iterator().next()+ "\","+
        				"\"occurrence\":"+resources.get(key).count+"},";
    		}
    		
    		
    	}
    	result = result.substring(0,result.length()-1);
    	result = result +"]}";
    	
    	return result;
    }
	
    public static String request(HttpMethod method) {
        String response = null;
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
            	Logger.info("Method failed: " + method.getStatusLine());
            }

            // Read the response body.
            //byte[] responseBody = method.getResponseBody(); //TODO Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            //response = new String(responseBody);

            //Code updated from (4 lines) https://stackoverflow.com/questions/10658485/using-dbpedia-spotlight-in-java-or-scala
            Reader in = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
            StringWriter writer = new StringWriter();
            org.apache.commons.io.IOUtils.copy(in, writer);
            response = writer.toString();
            
            
        } catch (HttpException e) {
        	Logger.info("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
        	Logger.info("Fatal transport error: " + e.getMessage());
        	Logger.info(method.getQueryString());
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return response;

    }

}
