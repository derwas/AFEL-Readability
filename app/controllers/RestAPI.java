package controllers;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import models.EvaluationResults;
import models.Stats;
import opennlp.tools.parser.Parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.typesafe.config.Config;

import Readability.Readability;
import play.Application;
import play.Logger;
import play.api.Configuration;
import play.api.libs.json.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.DBModule;
import play.db.jpa.JPAModule;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.*;
import play.mvc.Http.Request;
import play.twirl.api.Html;

public class RestAPI extends Controller {
	private final Config config;

    @javax.inject.Inject
    public RestAPI(Config config) {
        this.config = config;
    }


	  public Result index() {

	      return ok(views.html.api.render());
	       }


	  
	  public Result getReadabilityScores(){
		  
		  	String base64Credentials = ""+request().getHeaders().get(AUTHORIZATION);
	    	if(base64Credentials.contains("Basic")){
	    	Application app = new GuiceApplicationBuilder()
	      			 .disable(DBModule.class, JPAModule.class)
	      			        .build();

	    	
	    		base64Credentials = base64Credentials.substring(base64Credentials.indexOf("Basic")+6,base64Credentials.length()-1);
	    	
	    		String credentials = new String(Base64.getDecoder().decode(base64Credentials.toString()),
		                Charset.forName("UTF-8"));
		    	//Logger.info ("test something (2) ===>"+credentials);
		    	String[] values = credentials.split(":",2);
		    	
		    	Logger.info ("REST GET from =>"+values[0]);
	    	//if user if ok
		    	if (controllers.Application.authorisedUser(base64Credentials)){
	    			FormFactory formFactory = app.injector().instanceOf(FormFactory.class);

	    			DynamicForm requestData = formFactory.form().bindFromRequest();
		     	 	String url = requestData.get("url-input");

		     	 		Logger.info("URL  =>" + url);
		     	 		
		     	 		
		     	 		if (url != null){
		
		     	 			try{
		     	 			//Source source=new Source(new URL(url));
		     	 	        //String renderedText=source.getRenderer().toString();
		     	 	        
		     	 			
		     	 			WebPageExtractor webPagePdfExtractor = new WebPageExtractor();
		     	 			Map<String, Object> extractedMap = webPagePdfExtractor.processRecord(url);
		     	 		    String renderedText = extractedMap.get("text").toString();
		     	 			    
		     	 			//Readability r = new Readability(renderedText);
		     	 			
		     	 			JsonObject jo = new JsonObject();
		     	 			jo.addProperty("URL", url);
		     	 			jo.add("Scores", getScores(renderedText));
		     	 			jo.add("Stats", getStats(renderedText));

	      	 			 
		     		    	return ok(jo.toString()).as("application/json");

		     	 			} catch(Exception e){
		      	 				Logger.info("Unable to parse URL");
		      	 				JsonObject jo = new JsonObject();
					    		JsonObject error = new JsonObject();
					    		error.addProperty("status","400");
					    		error.addProperty("title", "Format");
					    		error.addProperty("detail", "Bad Request: Unable to parse URL!");
			     	 			jo.add("error", error);
					    		return status(400, jo.toString()).as("application/json");
		      	 				//return  status(400, "Bad Request: Unable to parse URL!");
		      	 			}		
		      		   	 	//return ok();

		      	 		}
		     	 		else{
		     	 			JsonObject jo = new JsonObject();
				    		JsonObject error = new JsonObject();
				    		error.addProperty("status","400");
				    		error.addProperty("title", "Format");
				    		error.addProperty("detail", "Bad Request: Unable to parse URL!");
		     	 			jo.add("error", error);
				    		return status(400, jo.toString()).as("application/json");
	      		   	 		//return  status(400, "Bad Request: Unable to parse URL!");
		     	 		}
		    	}else{
		    		JsonObject jo = new JsonObject();
		    		JsonObject error = new JsonObject();
		    		error.addProperty("status","401");
		    		error.addProperty("title", "Authorization");
		    		error.addProperty("detail", "Unauthorized User!");
     	 			jo.add("error", error);
		    		return status(401, jo.toString()).as("application/json");
		    	}
	    		
	    	}else
	    	{//there is no basic authentication
	    		JsonObject jo = new JsonObject();
	    		JsonObject error = new JsonObject();
	    		error.addProperty("status","401");
	    		error.addProperty("title", "Authorization");
	    		error.addProperty("detail", "Unauthorized User!");
 	 			jo.add("error", error);
	    		return status(401, jo.toString()).as("application/json");
	    	}
	    }

	public Result getEvaluationResults(){

		String base64Credentials = ""+request().getHeaders().get(AUTHORIZATION);
		if(base64Credentials.contains("Basic")){
			Application app = new GuiceApplicationBuilder()
					.disable(DBModule.class, JPAModule.class)
					.build();


			base64Credentials = base64Credentials.substring(base64Credentials.indexOf("Basic")+6,base64Credentials.length()-1);

			String credentials = new String(Base64.getDecoder().decode(base64Credentials.toString()),
					Charset.forName("UTF-8"));
			//Logger.info ("test something (2) ===>"+credentials);
			String[] values = credentials.split(":",2);

			Logger.info ("REST GET from =>"+values[0]);
			//if user if ok
			if (controllers.Application.authorisedUser(base64Credentials)){
				FormFactory formFactory = app.injector().instanceOf(FormFactory.class);

				DynamicForm requestData = formFactory.form().bindFromRequest();
				String url = requestData.get("url-input");

				Logger.info("URL  =>" + url);


				if (url != null){

					try{
						//Source source=new Source(new URL(url));
						//String renderedText=source.getRenderer().toString();


						WebPageExtractor webPagePdfExtractor = new WebPageExtractor();
						Map<String, Object> extractedMap = webPagePdfExtractor.processRecord(url);
						String renderedText = extractedMap.get("text").toString();

						//Readability r = new Readability(renderedText);

						JsonObject jo = new JsonObject();
						jo.addProperty("URL", url);
						jo.add("Scores", getScores(renderedText));
						jo.add("Stats", getStats(renderedText));
						try{
							String API_URL = config.getString("dbpediaspotlight" );

							jo.add("dbPediaConcepts",getConcepts(renderedText, API_URL));
						}catch (Exception e){
							Logger.info("Unable to parse Concepts");
							return ok(jo.toString()).as("application/json");
						}

						//jo.add("Concepts", joDBP);
						return ok(jo.toString()).as("application/json");

					} catch(Exception e){
						Logger.info("Unable to parse URL");
						JsonObject jo = new JsonObject();
						JsonObject error = new JsonObject();
						error.addProperty("status","400");
						error.addProperty("title", "Format");
						error.addProperty("detail", "Bad Request: Unable to parse URL!");
						jo.add("error", error);
						return status(400, jo.toString()).as("application/json");
						//return  status(400, "Bad Request: Unable to parse URL!");
					}
					//return ok();

				}
				else{
					JsonObject jo = new JsonObject();
					JsonObject error = new JsonObject();
					error.addProperty("status","400");
					error.addProperty("title", "Format");
					error.addProperty("detail", "Bad Request: Unable to parse URL!");
					jo.add("error", error);
					return status(400, jo.toString()).as("application/json");
					//return  status(400, "Bad Request: Unable to parse URL!");
				}
			}else{
				JsonObject jo = new JsonObject();
				JsonObject error = new JsonObject();
				error.addProperty("status","401");
				error.addProperty("title", "Authorization");
				error.addProperty("detail", "Unauthorized User!");
				jo.add("error", error);
				return status(401, jo.toString()).as("application/json");
			}

		}else
		{//there is no basic authentication
			JsonObject jo = new JsonObject();
			JsonObject error = new JsonObject();
			error.addProperty("status","401");
			error.addProperty("title", "Authorization");
			error.addProperty("detail", "Unauthorized User!");
			jo.add("error", error);
			return status(401, jo.toString()).as("application/json");
		}
	}

	  private static JsonObject getScores(String text){
			Readability r = new Readability(text);
			
			JsonObject scores = new JsonObject();
				JsonObject SMOGIndex = new JsonObject();
				SMOGIndex.addProperty("label", "SMOG Index");
				SMOGIndex.addProperty("uri","http://dbpedia.org/page/SMOG");
				SMOGIndex.addProperty("value", r.getSMOGIndex());
			scores.add("SMOG_Index", SMOGIndex);

	 			JsonObject SMOG = new JsonObject();
	 			SMOG.addProperty("label", "SMOG");
	 			SMOG.addProperty("uri","http://dbpedia.org/page/SMOG");
	 			SMOG.addProperty("value", r.getSMOG());
			scores.add("SMOG", SMOG);
			
	 			JsonObject Flesch_Reading_Ease = new JsonObject();
	 			Flesch_Reading_Ease.addProperty("label", "Flesch Reading Ease");
	 			Flesch_Reading_Ease.addProperty("uri","http://dbpedia.org/page/Flesch%E2%80%93Kincaid_readability_tests");
	 			Flesch_Reading_Ease.addProperty("value", r.getFleschReadingEase());
			scores.add("Flesch_Reading_Ease", Flesch_Reading_Ease);
		
	 			JsonObject FleschKincaid_Grade_Level = new JsonObject();
	 			FleschKincaid_Grade_Level.addProperty("label", "Flesch-Kincaid Grade Level");
	 			FleschKincaid_Grade_Level.addProperty("uri","http://dbpedia.org/page/Flesch%E2%80%93Kincaid_readability_tests");
	 			FleschKincaid_Grade_Level.addProperty("value", r.getFleschKincaidGradeLevel());
	 		scores.add("Flesch-Kincaid_Grade_Level", FleschKincaid_Grade_Level);
				
	 			JsonObject Automated_Readability_Index = new JsonObject();
	 			Automated_Readability_Index.addProperty("label", "Automated Readability Index");
	 			Automated_Readability_Index.addProperty("uri","http://dbpedia.org/page/Flesch%E2%80%93Kincaid_readability_tests");
	 			Automated_Readability_Index.addProperty("value", r.getARI());
	 		scores.add("Automated_Readability_Index", Automated_Readability_Index);
		
	 			JsonObject GunningFog_Index = new JsonObject();
	 			GunningFog_Index.addProperty("label", "Gunning-Fog Index");
	 			GunningFog_Index.addProperty("uri","http://dbpedia.org/page/Gunning_fog_index");
	 			GunningFog_Index.addProperty("value", r.getGunningFog());
	 		scores.add("Gunning-Fog_Index", GunningFog_Index);
		
	 			JsonObject ColemanLiau_Index = new JsonObject();
	 			ColemanLiau_Index.addProperty("label", "Coleman-Liau Index");
	 			ColemanLiau_Index.addProperty("uri","http://dbpedia.org/page/Gunning_fog_index");
	 			ColemanLiau_Index.addProperty("value", r.getColemanLiau());
	 		scores.add("Coleman-Liau_Index", ColemanLiau_Index);
			
			return scores;
		  
	  }
	  
	  private static JsonObject getConcepts(String text, String API_URL){
		  

		  
		  
		  String strConcepts = dbpedia.spotlight.QueryService.JSONtoJSONString(dbpedia.spotlight.QueryService.getdbpediaSpotlightJSON(text,API_URL));
		  //Logger.info("Concepts" + strConcepts);

		  JsonObject concepts =  new JsonParser().parse(strConcepts).getAsJsonObject();
		  return concepts;
	  }
	  
	  private static JsonObject getStats(String text){
			Readability r = new Readability(text);
			
			JsonObject stats = new JsonObject();
				JsonObject numberOfWords = new JsonObject();
				numberOfWords.addProperty("label", "Number of Words");
				numberOfWords.addProperty("value", r.getWords());
			stats.add("numberOfWords", numberOfWords);

	 			JsonObject numberOfSentences= new JsonObject();
	 			numberOfSentences.addProperty("label", "Number Of Sentences");
	 			numberOfSentences.addProperty("value", r.getSentences());
	 		stats.add("numberOfSentences", numberOfSentences);
			
	 			JsonObject numberOfComplexWords = new JsonObject();
	 			numberOfComplexWords.addProperty("label", "Number Of Complex Words");
	 			numberOfComplexWords.addProperty("value", r.getComplex());
	 		stats.add("numberOfComplexWords", numberOfComplexWords);
		
	 			JsonObject numberOfSyllables = new JsonObject();
	 			numberOfSyllables.addProperty("label", "Number Of Syllables");
	 			numberOfSyllables.addProperty("value", r.getSyllables());
	 		stats.add("numberOfSyllables", numberOfSyllables);
				
	 			JsonObject numberOfCaracters = new JsonObject();
	 			numberOfCaracters.addProperty("label", "Number Of Caracters");
	 			numberOfCaracters.addProperty("value", r.getCharacters());
	 		stats.add("numberOfCaracters", numberOfCaracters);
		

			return stats;
		  
	  }
	  
}
