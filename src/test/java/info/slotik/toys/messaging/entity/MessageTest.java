package info.slotik.toys.messaging.entity;

import info.slotik.toys.messaging.util.DefenseException;
import org.junit.Test;

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
}
