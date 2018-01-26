package info.slotik.toys.messaging;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.repository.MessageRepository;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.net.URI;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationFailureTests
{
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @LocalServerPort
    private int port;

    @Value("${messages.base.path}")
    private String basePath;

    @Autowired
    private MessageRepository repository;

    @Autowired
    private TestRestTemplate template = new TestRestTemplate();

    @Before
    public void setUp()
    {
        repository.deleteAll();
    }

    @Test
    @Parameters(source = MissingAuthorizationHeaderRequestProvider.class)
    public void fails_when_authorization_header_is_missing(Request<?> request)
    {
        ResponseEntity<?> response = request.exchange(template, baseURI());
        // FIXME shouldn't this actually be HttpStatus.UNAUTHORIZED?
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    public static class MissingAuthorizationHeaderRequestProvider
    {
        @ParameterProvider
        public static Request<?>[] provide()
        {
            return requestsRequiringAuthorization(null, "user");
        }
    }

    @Test
    @Parameters(source = WrongAuthenticationTypeRequestProvider.class)
    public void fails_when_wrong_authentication_type_is_used(Request<?> request)
    {
        ResponseEntity<?> response = request.exchange(template, baseURI());
        // FIXME shouldn't this actually be HttpStatus.UNAUTHORIZED?
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    public static class WrongAuthenticationTypeRequestProvider
    {
        @ParameterProvider
        public static Request<?>[] provide()
        {
            return requestsRequiringAuthorization("Basic aHR0cHdhdGNoOmY=", "usSseEer");
        }
    }

    @Test
    @Parameters(source = MalformedUserIdRequestProvider.class)
    public void fails_when_user_id_is_malformed(Request<?> request)
    {
        ResponseEntity<?> response = request.exchange(template, baseURI());
        // FIXME shouldn't this actually be HttpStatus.UNAUTHORIZED?
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    public static class MalformedUserIdRequestProvider
    {
        @ParameterProvider
        public static Request<?>[] provide()
        {
            return requestsRequiringAuthorization(Requests.authorization("invalid user id"), "1234user");
        }
    }

    @Test
    @Parameters(source = UserMismatchRequestProvider.class)
    public void fails_when_authenticated_user_does_not_match_user_in_request_body(Request<?> request)
    {
        ResponseEntity<?> response = request.exchange(template, baseURI());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    public static class UserMismatchRequestProvider
    {
        @ParameterProvider
        public static Request<?>[] provide()
        {
            return requestsRequiringAuthorization(Requests.authorization("nonUser"), "userRR");
        }
    }

    private static Request<?>[] requestsRequiringAuthorization(String authorizationHeader, String userId)
    {
        Message message = Message.fromData(userId, "very important content");
        return new Request<?>[]{
            Requests.addMessage(authorizationHeader, message),
            Requests.addInitialThenUpdateToMessage(authorizationHeader, message),
            Requests.addInitialThenDeleteMessage(authorizationHeader),
        };
    }

    private URI baseURI()
    {
        return URI.create("http://localhost:" + port + basePath);
    }

    private @interface ParameterProvider
    {
    }
}
