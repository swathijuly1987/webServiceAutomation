package trimbleserviceautomation.serviceautomation;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SampleTest {
	@Test
	public void verifyEmailAddress() {
		// Specify the base URL to the RESTful web service
		RestAssured.baseURI = "http://jsonplaceholder.typicode.com";

		// Get the RequestSpecification of the request that you want to sent
		// to the server. The server is specified by the BaseURI that we have
		// specified in the above step.
		RequestSpecification httpRequest = RestAssured.given();

		// Make sure you specify the resource name.
		Response response = httpRequest.get("/users");

		// Response.asString method will directly return the content of the body
		// as String.
		int userId = 0;

		List<String> jresponse = response.jsonPath().getList("$");
		int size = jresponse.size();
		for (int i = 0; i < size; i++) {

			String username = response.jsonPath().getString("username[" + i + "]");
			if (username.contains("Samantha")) {
				userId = Integer.parseInt(response.jsonPath().getString("id[" + i + "]"));
				Assert.assertTrue(true, "user name found in the list");
				break;
			}
		}

		// Step 2 to identify the post written by user.

		RestAssured.baseURI = "http://jsonplaceholder.typicode.com";

		// Get the RequestSpecification of the request that you want to sent
		// to the server. The server is specified by the BaseURI that we have
		// specified in the above step.
		RequestSpecification httpRequestforPost = RestAssured.given();
		Response responseForPost = httpRequestforPost.get("/posts");

		// Response.asString method will directly return the content of the body
		// as String.
		ArrayList<Integer> postId = new ArrayList<Integer>();
		int userIdForPost = 0;
		List<String> jresponseForPost = responseForPost.jsonPath().getList("$");
		int sizeOfPost = jresponseForPost.size();
		for (int i = 0; i < sizeOfPost; i++) {
			userIdForPost = Integer.parseInt(responseForPost.jsonPath().getString("userId[" + i + "]"));
			if (userIdForPost == userId) {
				postId.add(Integer.parseInt(responseForPost.jsonPath().getString("id[" + i + "]")));
			}
		}

		// step3 to validate the email address.

		RestAssured.baseURI = "http://jsonplaceholder.typicode.com";

		// Get the RequestSpecification of the request that you want to sent
		// to the server. The server is specified by the BaseURI that we have
		// specified in the above step.
		RequestSpecification httpRequestforComments = RestAssured.given();
		Response responseForComments = httpRequestforComments.get("/comments");

		int commentId;
		List<String> jresponseForComment = responseForComments.jsonPath().getList("$");
		int sizeOfComment = jresponseForComment.size();
		SoftAssert sa = new SoftAssert();
		for (int i = 0; i < postId.size(); i++) {
			for (int j = 0; j < sizeOfComment; j++) {
				commentId = Integer.parseInt(responseForComments.jsonPath().getString("postId[" + j + "]"));

				if (commentId == postId.get(i)) {
					String email = responseForComments.jsonPath().getString("email[" + j + "]");
					boolean flag = isValid(email);
					System.out.println("Validated the mail address" + email + " :IS VALID:" + flag
							+ " For Given Comment id:" + commentId);
					sa.assertTrue(flag, "Validated the mail address" + email + " : For Given Comment id:" + commentId);

				}
			}
		}
	}

	static boolean isValid(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(regex);
	}
}
