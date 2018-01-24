package info.slotik.toys.messaging.entity;

import info.slotik.toys.messaging.util.Defense;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Message
{
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    @NotNull
    private String userId;

    @Column
    @NotNull
    private String content;

    private Message()
    {
    }

    public Long getId()
    {
        return id;
    }

    public String getContent()
    {
        return content;
    }

    public String getUserId()
    {
        return userId;
    }

    public String toString() {
        return String.format("[#%05d] %s: %s", id, userId, content);
    }

    public static Message fromData(String userId, String content)
    {
        return create(null, userId, content);
    }

    static Message create(Long id, String userId, String content)
    {
        Defense.notNull("User identifier cannot be null", userId);
        Defense.notNull("Message content cannot be null", content);

        Message result = new Message();
        result.id = id;
        result.userId = userId;
        result.content = content;
        return result;
    }
}
