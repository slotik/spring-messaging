package info.slotik.toys.messaging;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.security.WebSecurity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.util.List;

class Requests
{
    static String authorization(String userId)
    {
        return WebSecurity.AUTHORIZATION_TYPE + " " + userId;
    }

    static Request<List<Message>> getAllMessages()
    {
        return (template, baseURI) ->
            template.exchange(baseURI, HttpMethod.GET, null, messageListType());
    }


    static Request<Message> getMessage(long id)
    {
        return (template, baseURI) ->
        {
            URI uri = messageURI(baseURI, id);
            return template.exchange(uri, HttpMethod.GET, null, Message.class);
        };
    }

    static Request<Message> addMessage(String authorizationHeader, Message message)
    {
        return (template, baseURI) ->
        {
            RequestEntity<Message> request = RequestEntity
                .post(baseURI)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .body(message);
            return template.exchange(baseURI, HttpMethod.POST, request, Message.class);
        };
    }

    static Request<Object> updateMessage(String authorizationHeader, long id, Message message)
    {
        return (template, baseURI) ->
        {
            URI uri = messageURI(baseURI, id);
            RequestEntity<Message> request = RequestEntity
                .put(uri)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .body(message);
            return template.exchange(uri, HttpMethod.PUT, request, Object.class);
        };
    }

    static Request<Object> deleteMessage(String authorizationHeader, long id)
    {
        return (template, baseURI) ->
        {
            URI uri = messageURI(baseURI, id);
            RequestEntity<?> request = RequestEntity
                .delete(uri)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .build();
            return template.exchange(uri, HttpMethod.DELETE, request, Object.class);
        };
    }

    static Request<Object> addInitialThenUpdateToMessage(String authorizationHeader, Message message)
    {
        return (template, baseURI) ->
        {
            Message initialMessage = Message.fromData(message.getUserId(), "initial");
            Message added = addMessage(authorization(message.getUserId()), initialMessage)
                .exchange(template, baseURI)
                .getBody();
            return updateMessage(authorizationHeader, added.getId(), message)
                .exchange(template, baseURI);
        };
    }

    public static Request<Object> addInitialThenDeleteMessage(String authorizationHeader)
    {
        return (template, baseURI) ->
        {
            String userId = "user";
            Message initialMessage = Message.fromData(userId, "initial");
            Message added = addMessage(authorization(userId), initialMessage)
                .exchange(template, baseURI)
                .getBody();
            return deleteMessage(authorizationHeader, added.getId())
                .exchange(template, baseURI);
        };
    }

    private static URI messageURI(URI baseURI, long id)
    {
        return URI.create(String.format("%s/%d", baseURI.toString(), id));
    }

    private static ParameterizedTypeReference<List<Message>> messageListType()
    {
        return new ParameterizedTypeReference<List<Message>>()
        {
        };
    }
}
