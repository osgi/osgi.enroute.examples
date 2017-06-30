package osgi.enroute.examples.rest.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.dto.DTO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import aQute.bnd.http.HttpClient;
import aQute.bnd.http.HttpRequest;
import aQute.lib.json.JSONCodec;

public class RestExamplesTest {

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	@Before
	public void checkContext() {
		Assert.assertNotNull(context);
	}

	@Test
	public void testGetUpper() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper/");

		String result = client.build().get(String.class).go(url);

		Assert.assertEquals("\"CHUCKSTEAK IS GREAT\"", result);

		client.close();
	}

	@Test
	public void testGetUpperWithArg() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper/steak");

		String result = client.build().get(String.class).go(url);

		Assert.assertEquals("\"STEAK\"", result);

		client.close();
	}

	@Test
	public void testGetUpperWithTwoArgs() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper/steak1/steak2");

		String result = client.build().get(String.class).go(url);

		Assert.assertEquals("\"STEAK1 and STEAK2\"", result);

		client.close();
	}

	@Test
	public void testGetUpper2() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper2/?upper2=test");

		String result = client.build().get(String.class).go(url);

		Assert.assertEquals("\"TEST\"", result);

		client.close();
	}

	public static class History extends DTO {
		public String input;
		public String output;
	}

	@Test
	public void testGetUpper3() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper3/TesT");

		HttpRequest<History> httpRequest = client.build().get(History.class);

		History result = httpRequest.go(url);

		Assert.assertNotNull(result);

		Assert.assertEquals("TesT", result.input);

		Assert.assertEquals("TEST", result.output);

		url = new URI("http://localhost:8080/rest/upper3/TesT2");

		result = httpRequest.go(url);

		Assert.assertNotNull(result);

		Assert.assertEquals("TesT2", result.input);

		Assert.assertEquals("TEST2", result.output);

		client.close();
	}

	@Test
	public void testDeleteUpper3() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper3/TesT2");

		Object result = client.build().delete().go(url);

		Assert.assertNotNull(result);

		client.close();
	}

	@Test
	public void testPostUpper4() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper4/Sam");

		HttpRequest<Object> request = client.build().post();

		Object result = request.go(url);

		String jsonResult = new JSONCodec().enc().to().put(result).toString();

		Assert.assertEquals("{\"input\":\"Sam\",\"output\":\"SAM\"}", jsonResult);

		url = new URI("http://localhost:8080/rest/upper4/Sam2");

		result = request.go(url);

		jsonResult = new JSONCodec().enc().to().put(result).toString();

		Assert.assertEquals("{\"input\":\"Sam2\",\"output\":\"SAM2\"}", jsonResult);

		client.close();
	}

	@Test
	public void testPutUpper4() throws Exception {
		HttpClient client = new HttpClient();

		URI url = new URI("http://localhost:8080/rest/upper4/Sam");

		HttpRequest<Object> request = client.build().put();

		Object result = request.go(url);

		String jsonResult = new JSONCodec().enc().to().put(result).toString();

		Assert.assertEquals("{\"input\":\"Sam\",\"output\":\"SAM\"}", jsonResult);

		url = new URI("http://localhost:8080/rest/upper4/Sam2");

		result = request.go(url);

		jsonResult = new JSONCodec().enc().to().put(result).toString();

		Assert.assertEquals("{\"input\":\"Sam2\",\"output\":\"SAM2\"}", jsonResult);

		client.close();
	}

	@Test
	public void testPostUpper5() throws Exception {
		URL url = new URL("http://localhost:8080/rest/upper5/");

		String body = "{\"input\":\"Sam3\",\"output\":\"SAM3\"}";

		String response = doVerbWithBody(url, "POST", body);

		Assert.assertEquals("{\"input\":\"Sam3\",\"output\":\"SAM3\"}", response);
	}

	@Test
	public void testPutUpper5() throws Exception {
		URL url = new URL("http://localhost:8080/rest/upper5/");

		String body = "{\"input\":\"Sam3\",\"output\":\"SAM3\"}";

		String response = doVerbWithBody(url, "PUT", body);

		Assert.assertEquals(body, response);
	}

	private String doVerbWithBody(URL url, String verb, String body) throws Exception {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod(verb);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type","application/json");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

		Assert.assertEquals(200, con.getResponseCode());

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
}
