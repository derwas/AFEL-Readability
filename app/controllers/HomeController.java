package controllers;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.json.JSONObject;

import com.typesafe.config.Config;

import Readability.Readability;
import net.htmlparser.jericho.Source;
import play.Application;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.DBModule;
import play.db.jpa.JPAModule;
import play.inject.guice.GuiceApplicationBuilder;
import play.data.Form;
import play.mvc.*;
import play.Logger;
import play.api.Configuration;

import java.net.URL;
import play.twirl.api.Html;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller{
	
	private final Config config;

    @javax.inject.Inject
    public HomeController(Config config) {
        this.config = config;
    }

    public Result index() {
    	Application app = new GuiceApplicationBuilder()
   			 .disable(DBModule.class, JPAModule.class)
   			        .build();

   	FormFactory formFactory = app.injector().instanceOf(FormFactory.class);
   	
   	//Configuration conf = app.injector().instanceOf(Configuration.class);
   	
   	String API_URL = config.getString("dbpediaspotlight" );
 

       DynamicForm requestData = formFactory.form().bindFromRequest();
  	 	String url = requestData.get("url-input");

  	 		Logger.info("-------------->" + url);
  	 		
  	 		
  	 		if (url != null){
  	 			String stats ="";

  	 			String scores ="";
  	 			
  	 			String concepts = "";
  	 			try{
  	 			//Source source=new Source(new URL(url));
  	 	        //String renderedText=source.getRenderer().toString();
  	 	        
  	 			
  	 			WebPageExtractor webPagePdfExtractor = new WebPageExtractor();
  	 			Map<String, Object> extractedMap = webPagePdfExtractor.processRecord(url);
  	 		    String renderedText = extractedMap.get("text").toString();
  	 			   
  	 		    
  	 		    //Logger.info("\n\n\n\n"+renderedText+"\n\n\n\n");
  	 			Readability r = new Readability(renderedText);
  	 			

  	 	    	
  	 			
  	 			 stats ="<div class=\"col-md-6\">" + 
  	 			 		"<h2>Numbers and Stats</h2>" +
  	 			 		"<p>Number of words: " + r.getWords() + "<br>" +
  	 								"Number of sentences: " + r.getSentences()+ "<br>" +
  	 								"Number of complex words: " + r.getComplex()+ "<br>" +
  	 								"Number of syllables: " + r.getSyllables()+ "<br>" +
  	 								"Number of caracters: " + r.getCharacters()+ "<br>" +
  	 								"</p>" +
  	 			 		"   </div>";
  	 			 
  	 			 scores= "<div class=\"col-md-6\">" +
  	 					 "<h2>Readability Scores</h2>" +
  	 					 "<p><a href=\"http://dbpedia.org/page/SMOG\" target=\"_blank\">SMOG Index</a> : " + r.getSMOGIndex() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/SMOG\" target=\"_blank\">SMOG</a> : " + r.getSMOG() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/Flesch%E2%80%93Kincaid_readability_tests\" target=\"_blank\">Flesch Reading Ease </a>: " + r.getFleschReadingEase() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/Flesch%E2%80%93Kincaid_readability_tests\" target=\"_blank\">Flesch-Kincaid Grade Level</a> : " + r.getFleschKincaidGradeLevel() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/Automated_readability_index\" target=\"_blank\">Automated Readability Index</a> : " + r.getARI() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/Gunning_fog_index\" target=\"_blank\">Gunning-Fog Index</a> : " + r.getGunningFog() + "<br>" +
  	 								"<a href=\"http://dbpedia.org/page/Coleman%E2%80%93Liau_index\" target=\"_blank\">Coleman-Liau Index</a> : " + r.getColemanLiau() +"</p>" +
  	 								"   </div>";
  	 			 
   	 			//TODO dbpediaspotlight query  	 
   	 			
   	 			JSONObject jo = dbpedia.spotlight.QueryService.getdbpediaSpotlightJSON (renderedText, API_URL);
   	 			Logger.info("Results received from DBPedia Spotlight");
   	 	    	Logger.info("Parsing results");
  	 			 
  	 			String linksToConcepts = dbpedia.spotlight.QueryService.JSONtoHTML(jo);
  	 			
  	 			//Logger.info("\n\n"+linksToConcepts);


  	 			int numConcepts =jo.getJSONArray("Resources").length();

  	 			 concepts= "<div class=\"col-md-12\">" +
  	 					 "<h2>"+numConcepts+" DBPedia Concepts<a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Legend: Concept (number of occurrences)\"> <span class=\"glyphicon glyphicon-info-sign\"></a></h2>" +
  	 					 "<p>"+linksToConcepts+"<br><br></p>" +
  	 								"   </div>";
  	 			
  	 			} catch(Exception e){
  	 				Logger.info("Unable to parse URL");
  	  		   	 	return ok(views.html.index.render(url,Html.apply(stats),Html.apply(scores),Html.apply(concepts))); 

  	 			}	
  	 					
  		   	 	return ok(views.html.index.render(url,Html.apply(stats),Html.apply(scores),Html.apply(concepts))); 

  	 		}
    	
	   	 	return ok(views.html.index.render("http://",Html.apply(""),Html.apply(""),Html.apply(""))); 

    }
    

}
