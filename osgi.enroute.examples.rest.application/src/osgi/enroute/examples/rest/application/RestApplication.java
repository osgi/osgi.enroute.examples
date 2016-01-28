package osgi.enroute.examples.rest.application;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.dto.api.DTOs;
import osgi.enroute.dto.api.TypeReference;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequirePagedownWebResource(resource = "enmarkdown.js")
@Component(name="osgi.enroute.examples.rest")
public class RestApplication implements REST {

	@Reference
	DTOs dtos;
	
	//*************************************
	//GET Examples using URL arguments
	//*************************************
	
	//GET http://localhost:8080/rest/upper/ ==> returns "CHUCKSTEAK IS GREAT"  
	public String getUpper(RESTRequest rr) {
		StringBuilder st = new StringBuilder("Get with no argument: \n")
				.append("  --> rr._host(): " + rr._host() + "\n");
		System.out.println(st.toString() + "\n");
		return "ChuckSteak is Great".toUpperCase();
	}
	
	//GET http://localhost:8080/rest/upper/steak ==> returns "STEAK"
	public String getUpper(RESTRequest rr, String string) {
		string = string.toUpperCase();
		StringBuilder st = new StringBuilder("Get with one string argument: \n")
				.append("  --> String 1: " + string+ "\n")
				.append("  --> rr._host(): " + rr._host() + "\n");
		System.out.println(st.toString() );
		return string;
	}
	
	//GET http://localhost:8080/rest/upper/steak1/steak2 ==> returns "STEAK1 and STEAK2"
	public String getUpper(RESTRequest rr, String string, String string2) {
		string = string.toUpperCase();
		string2 = string2.toUpperCase();
		StringBuilder st = new StringBuilder("Get with two string argument: \n")
				.append("  --> String 1: " + string+ "\n")
				.append("  --> String 2: " + string2+ "\n")
				.append("  --> rr._host(): " + rr._host() + "\n");
		System.out.println(st.toString());
		return string + " and " + string2;
	}
	
	//*************************************
	//GET Example using URL parameters
	//*************************************
	
	//note the name of the member variables map to the URL parameters used below
	interface UpperRequest2 extends RESTRequest {
		String upper1();
		String upper2();
	}  

	//GET http://localhost:8080/rest/upper2/?upper2=test ==> returns "TEST"
	public String getUpper2( UpperRequest2 ur ) {
		String ret = ur.upper2().toUpperCase();
		StringBuilder st = new StringBuilder("Get with one param: \n")
				.append("Upper2: " + ret + "\n");
		System.out.println(st.toString());
		return ret;
	}
	
	//*************************************
	//GET Example returning a serialized object
	//DELETE Example using URL arguments
	//*************************************

	public static class History extends DTO {
		public String input;
		public String output;
	}
	
	private final Map<String, History> m_history = new ConcurrentHashMap<>();
	
	//GET http://localhost:8080/rest/upper3/TesT ==> returns {"input":"TesT","output":"TEST"}
	//GET http://localhost:8080/rest/upper3/TesT2 ==> returns {"input":"TesT2","output":"TEST2"}
	public History getUpper3(RESTRequest rr, String string) {
		History h = new History();
		h.input = string;
		h.output = string.toUpperCase();
		m_history.put(h.input, h);
		StringBuilder st = new StringBuilder("Get returning an object: \n")
				.append("input: " + h.input + "\n")
				.append("output: " + h.output + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return h;
	}
	
	//DELETE http://localhost:8080/rest/upper3/TesT2 ==> returns null - see console
	//NOTE: the delete payload is ignored
	public Map<String, History> deleteUpper3(RESTRequest rr, String string) {
		m_history.remove(string);
		StringBuilder st = new StringBuilder("Delete an entry: \n")
				.append("input: " + string + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return m_history;
	}

	//*************************************
	//POST and PUT Examples - builds on previous History Example - using arguments
	//*************************************
	
	//POST http://localhost:8080/rest/upper4/Sam ==> returns {"input":"Sam","output":"SAM"}
	//POST http://localhost:8080/rest/upper4/Sam2 ==> returns {"input":"Sam2","output":"SAM2"}
	public History postUpper4(RESTRequest rr, String payload, String string) {
		History h = new History();
		h.input = string;
		h.output = string.toUpperCase();
		m_history.put(h.input, h);
		StringBuilder st = new StringBuilder("Post an entry using arguments: \n")
				.append("input: " + h.input + "\n")
				.append("output: " + h.output + "\n")
				.append("payload: " + payload + "\n") 
				.append("string: " + string + "\n")
				.append("  --> rr._host(): " + rr._host() + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return h;
	}
	
	//PUT http://localhost:8080/rest/upper4/Sam ==> returns {"input":"Sam","output":"SAM"}
	//PUT http://localhost:8080/rest/upper4/Sam2 ==> returns {"input":"Sam2","output":"SAM2"}
	public History putUpper4(RESTRequest rr, String payload, String string) {
		History h = new History();
		h.input = string;
		h.output = string.toUpperCase();
		m_history.put(h.input, h);
		StringBuilder st = new StringBuilder("Put an entry: \n")
				.append("input: " + h.input + "\n")
				.append("output: " + h.output + "\n")
				.append("payload: " + payload + "\n") 
				.append("string: " + string + "\n")
				.append("  --> rr._host(): " + rr._host() + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return h;
	}

	
	//*************************************
	//POST and PUT Examples - builds on previous History Example - using payload
	//*************************************
	interface UpperRequest5 extends RESTRequest {
		History _body();
	}  
	
	//POST http://localhost:8080/rest/upper5/ with a payload of {"input":"Sam3","output":"SAM3"} ==> returns ...
	public History postUpper5(UpperRequest5 rq5) {
		History h = rq5._body();
		m_history.put(h.input, h);
		StringBuilder st = new StringBuilder("Post an entry using payload: \n")
				.append("input: " + h.input + "\n")
				.append("output: " + h.output + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return h;
	}
	
	//PUT http://localhost:8080/rest/upper5/ with a payload of {"input":"Sam3","output":"SAM3"} ==> returns ...
	public History putUpper5(UpperRequest5 rq5) {
		History h = rq5._body();
		m_history.put(h.input, h);
		StringBuilder st = new StringBuilder("Put an entry using payload: \n")
				.append("input: " + h.input + "\n")
				.append("output: " + h.output + "\n")
				.append("Collection: " + m_history.toString() + "\n");
		System.out.println(st.toString());
		return h;
	}
	
	
	public static class Example extends DTO {
		public String method;
		public String title;
		public String uri;
		public String expect;
		public Map<String,String> payload;
		
		public Example() {}
		
		Example(String verb, String title, String uri, Map<String,String> payload, String expect) {
			this.title=title;
			this.uri = uri;
			this.expect = expect;
			this.method = verb;
			this.payload = payload;
		}
	}
	
	Example[] examples;
	
	public Example[] getExamples(RESTRequest r) {
		if ( examples == null) {
			examples = new Example[] {
					new Example( "GET", "upper: Basic", "/rest/upper",null, "CHUCKSTEAK IS GREAT"),
					new Example( "GET", "upper: One path argument", "/rest/upper/steak", null, "STEAK"),
					new Example( "GET","upper: Two path arguments", "/rest/upper/steak1/steak2",null, "STEAK1 and STEAK2"),
					new Example( "GET", "upper2: Query parameter", "/rest/upper2?upper2=test",null, "TEST"),
					new Example( "GET", "upper3: One path argument", "/rest/upper3/TesT",null, "TEST"),
					new Example( "GET", "upper3: One path argument", "/rest/upper3/TesT2",null, "TEST2"),
					new Example( "GET", "upper3: Another path argument", "/rest/upper3/TesT2",null, "TEST2"),
					
					new Example( "DELETE", "upper3: Path argument", "/rest/upper3/TesT2", null, ""),
					
					new Example( "POST", "upper4: Path argument", "/rest/upper4/Sam", null, "SAM"),			
					new Example( "POST", "upper4: Another path argument", "/rest/upper4/Sam2", null, "SAM2"),

					new Example( "PUT", "upper4: Another path argument", "/rest/upper4/Sam", null, "SAM"),
					new Example( "PUT", "upper4: Another path argument", "/rest/upper4/Sam2", null, "SAM2"),

					new Example( "POST", "upper5: with payload", "/rest/upper5", payload("{\"input\":\"Sam3\",\"output\":\"SAM3\"}"), "SAM3"),			
			};			
		}
		return examples;
	}

	private Map<String, String> payload(String string) {
		if ( string == null)
			return Collections.emptyMap();
		
		try {
			return dtos.decoder( new TypeReference<Map<String,String>>() {}).get(string);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
