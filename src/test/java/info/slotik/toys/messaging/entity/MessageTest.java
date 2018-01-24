package info.slotik.toys.messaging.entity;

import info.slotik.toys.messaging.util.DefenseException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageTest
{
    @Test(expected = DefenseException.class)
    public void throws_an_exception_when_null_user_id_is_supplied()
    {
        Message.fromData(null, "content");
    }

    @Test(expected = DefenseException.class)
    public void throws_an_exception_when_null_content_is_supplied()
    {
        Message.fromData("userId", null);
    }

    @Test
    public void can_retrieve_stored_data()
    {
        String userId = "user";
        String content = "content";
        Message message = Message.fromData(userId, content);

        assertNull("ID on a data-only message is not null", message.getId());
        assertEquals("User ID mismatch", userId, message.getUserId());
        assertEquals("Message content mismatch", content, message.getContent());
    }

    @Test
    public void can_retrieve_stored_data_as_well_as_id()
    {
        Long messageId = 42L;
        String userId = "user";
        String content = "content";
        Message message = Message
            .fromData(userId, content)
            .withId(messageId);

        assertEquals("Message ID mismatch", messageId, message.getId());
        assertEquals("User ID mismatch", userId, message.getUserId());
        assertEquals("Message content mismatch", content, message.getContent());
    }
}
