package info.slotik.toys.messaging;

import info.slotik.toys.messaging.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessagingApplicationTests
{
    @LocalServerPort
    private int port;

    @Value("${messages.base.path}")
    private String basePath;

    @Autowired
    private TestRestTemplate template;

    @Test
    public void context_loads()
    {
    }

    @Test
    public void initially_no_messages_are_retrieved()
    {
        List<Message> messages = getAllMessages();
        assertEquals("No messages expected", 0, messages.size());
    }

    private List<Message> getAllMessages()
    {
        String uri = "http://localhost:" + port + basePath;
        return template
            .exchange(uri, HttpMethod.GET, null, messageListType())
            .getBody();
    }

    private ParameterizedTypeReference<List<Message>> messageListType()
    {
        return new ParameterizedTypeReference<List<Message>>()
        {
        };
    }
}
