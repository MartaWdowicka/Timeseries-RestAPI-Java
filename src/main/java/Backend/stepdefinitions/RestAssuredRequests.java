package Backend.stepdefinitions;

import java.net.ConnectException;
import org.json.JSONObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Backend.enums.ResponseCodes.ResponseCode;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import Abstract.TestContext;

public class RestAssuredRequests extends CommonSteps {
	private static final Logger log = LoggerFactory.getLogger(RestAssuredRequests.class);
	private TestContext testContext;

	public RestAssuredRequests(TestContext testContext) {
        super(testContext);
    }

	// Purposefully sending Bad Request. OkHttp3's validation doesn't allow it. 
	// Bad Request: Timeseries API accept GET only with parameters in url not in body
	public void executeBadRequest(String startDate, String endDate) throws Exception {
        RequestSpecification requestSpecification = RestAssured.given().log().ifValidationFails();
        JSONObject requestParameters = new JSONObject();
        requestParameters.put("start_date",  startDate);
        requestParameters.put("end_date",  endDate);
        requestSpecification
                .header("apikey", testContext.getAPIKey())
                .body(requestParameters.toMap())
                .then().log().ifError();
        try {
        	Response response = requestSpecification.get();
        	Assert.assertEquals(ResponseCode.getResponseCode("BADREQUEST"), response.statusCode());
        } catch (ConnectException e) {
        	log.error("Test Ended - Sometimes server refuses connection as a response to Bad Request. This was one of these times.");
        }
	}
}