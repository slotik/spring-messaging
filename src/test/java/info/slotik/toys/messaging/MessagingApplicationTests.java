package info.slotik.toys.messaging;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.repository.MessageRepository;
import org.junit.Before;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessagingApplicationTests
{
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
    public void context_loads()
    {
    }

    @Test
    public void initially_no_messages_are_present()
    {
        assertNoMessages();
    }

    @Test
    public void a_single_message_can_be_created_and_subsequently_retrieved()
    {
        Message inputMessage = Message.fromData("user1", "content1");
        addMessageAndAssertResponseIsCorrect(inputMessage);
        assertMessages(inputMessage);
    }

    @Test
    public void several_messages_can_be_created_and_subsequently_retrieved()
    {
        Message[] inputMessages = {
            Message.fromData("user1", "content1"),
            Message.fromData("user2", "yes! content1!"),
            Message.fromData("user1", "content2"),
            Message.fromData("user2", "no! content3!"),
            Message.fromData("user3", "content3"),
            Message.fromData("user2", "content3"),
            Message.fromData("user3", "content3"),
        };
        addMessagesAndAssertResponseIsCorrect(inputMessages);
        assertMessages(inputMessages);
    }

    private void addMessagesAndAssertResponseIsCorrect(Message... messages)
    {
        for (Message each : messages)
        {
            addMessageAndAssertResponseIsCorrect(each);
        }
    }

    private void addMessageAndAssertResponseIsCorrect(Message inputMessage)
    {
        ResponseEntity<Message> response = attemptAddMessage(
            Requests.authorization(inputMessage.getUserId()),
            inputMessage);
        Message result = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(result.getId());
        assertEquals(inputMessage.getUserId(), result.getUserId());
        assertEquals(inputMessage.getContent(), result.getContent());
        assertEquals(
            baseURI().resolve(URI.create(String.format("%s/%d", basePath, result.getId()))),
            response.getHeaders().getLocation());
    }

    private ResponseEntity<Message> attemptAddMessage(String authorizationHeader, Message message)
    {
        return Requests
            .addMessageRequest(authorizationHeader, message)
            .execute(template, baseURI());
    }

    private void assertNoMessages()
    {
        assertMessages();
    }

    private void assertMessages(Message... expectedMessages)
    {
        assertMessages(Arrays.asList(expectedMessages));
    }

    private void assertMessages(List<Message> expectedMessages)
    {
        List<Message> actualMessages = getAllMessages().getBody();
        assertEquals(
            expectedMessages.size() + " messages expected",
            expectedMessages.size(),
            actualMessages.size());
        for (int i = 0; i < expectedMessages.size(); i++)
        {
            assertMessageEquals(expectedMessages.get(i), actualMessages.get(i));
        }
    }

    private void assertMessageEquals(Message expected, Message actual)
    {
        assertNotNull("Message ID is null", actual.getId());
        assertEquals(
            "User ID mismatch",
            expected.getUserId(),
            actual.getUserId());
        assertEquals(
            "Message content mismatch",
            expected.getContent(),
            actual.getContent());
    }

    private ResponseEntity<List<Message>> getAllMessages()
    {
        return Requests.getAllMessages().execute(template, baseURI());
    }

    private URI baseURI()
    {
        return URI.create("http://localhost:" + port + basePath);
    }
}
