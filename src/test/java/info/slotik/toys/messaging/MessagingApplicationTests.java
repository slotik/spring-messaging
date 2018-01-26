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
import java.util.ArrayList;
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
        Message[] inputMessages = getSampleMessages();
        addMessagesAndAssertResponseIsCorrect(inputMessages);
        assertMessages(inputMessages);
    }

    @Test
    public void can_retrieve_individual_messages()
    {
        List<Message> messages = addMessagesAndAssertResponseIsCorrect(getSampleMessages());
        for (Message each : messages)
        {
            assertSingleMessage(each.getId(), each);
        }
    }

    @Test
    public void non_existing_message_is_not_found()
    {
        ResponseEntity<Message> response = Requests
            .getMessage(777)
            .exchange(template, baseURI());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void can_update_own_message()
    {
        String userId = "someUser123";
        Message input = Message.fromData(userId, "initial content");
        Message initial = Requests
            .addMessage(Requests.authorization(userId), input)
            .exchange(template, baseURI())
            .getBody();

        Message updated = Message.fromData(userId, "updated content");
        ResponseEntity<?> response = Requests
            .updateMessage(Requests.authorization(userId), initial.getId(), updated)
            .exchange(template, baseURI());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertSingleMessage(initial.getId(), updated);
        assertMessages(updated);
    }

    @Test
    public void cannot_update_another_users_message()
    {
        String ownerId = "someUser123";
        Message input = Message.fromData(ownerId, "initial content");
        Message initial = Requests
            .addMessage(Requests.authorization(ownerId), input)
            .exchange(template, baseURI())
            .getBody();

        String updaterId = "anotherUser123";
        Message updated = Message.fromData(updaterId, "updated content");
        ResponseEntity<?> response = Requests
            .updateMessage(Requests.authorization(updaterId), initial.getId(), updated)
            .exchange(template, baseURI());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void cannot_update_non_existing_mesage() {
        String updaterId = "user123";
        long invalidId = 777L;
        Message updated = Message.fromData(updaterId, "updated content");
        ResponseEntity<?> response = Requests
            .updateMessage(Requests.authorization(updaterId), invalidId, updated)
            .exchange(template, baseURI());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void can_delete_own_message()
    {
        String userId = "someUser123";
        Message input = Message.fromData(userId, "content");
        Message initial = Requests
            .addMessage(Requests.authorization(userId), input)
            .exchange(template, baseURI())
            .getBody();

        ResponseEntity<?> response = Requests
            .deleteMessage(Requests.authorization(userId), initial.getId())
            .exchange(template, baseURI());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void cannot_delete_another_users_message()
    {
        String ownerId = "someUser123";
        Message input = Message.fromData(ownerId, "content");
        Message initial = Requests
            .addMessage(Requests.authorization(ownerId), input)
            .exchange(template, baseURI())
            .getBody();

        String deleterId = "anotherUser123";
        ResponseEntity<?> response = Requests
            .deleteMessage(Requests.authorization(deleterId), initial.getId())
            .exchange(template, baseURI());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void cannot_delete_non_existing_mesage() {
        String deleterId = "user123";
        long invalidId = 777L;
        ResponseEntity<?> response = Requests
            .deleteMessage(Requests.authorization(deleterId), invalidId)
            .exchange(template, baseURI());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Message[] getSampleMessages()
    {
        return new Message[]{
            Message.fromData("user1", "content1"),
            Message.fromData("user2", "yes! content1!"),
            Message.fromData("user1", "content2"),
            Message.fromData("user2", "no! content3!"),
            Message.fromData("user3", "content3"),
            Message.fromData("user2", "content3"),
            Message.fromData("user3", "content3"),
        };
    }

    private List<Message> addMessagesAndAssertResponseIsCorrect(Message... messages)
    {
        List<Message> result = new ArrayList<>();
        for (Message each : messages)
        {
            result.add(addMessageAndAssertResponseIsCorrect(each));
        }
        return result;
    }

    private Message addMessageAndAssertResponseIsCorrect(Message inputMessage)
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

        return result;
    }

    private ResponseEntity<Message> attemptAddMessage(String authorizationHeader, Message message)
    {
        return Requests
            .addMessage(authorizationHeader, message)
            .exchange(template, baseURI());
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

    private void assertSingleMessage(long messageId, Message expected)
    {
        Message actual = getMessage(messageId).getBody();
        assertMessageEquals(expected, actual);
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
        return Requests.getAllMessages().exchange(template, baseURI());
    }

    private ResponseEntity<Message> getMessage(long id)
    {
        return Requests.getMessage(id).exchange(template, baseURI());
    }

    private URI baseURI()
    {
        return URI.create("http://localhost:" + port + basePath);
    }
}
