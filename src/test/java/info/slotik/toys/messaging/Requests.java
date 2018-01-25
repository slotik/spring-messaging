package info.slotik.toys.messaging;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.security.WebSecurity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.util.List;

class Requests
{
    static String authorization(String userId)
    {
        return WebSecurity.AUTHORIZATION_TYPE + " " + userId;
    }

    static Request<List<Message>> getAllMessages() {
        return (template, baseURI) ->
        template
            .exchange(baseURI, HttpMethod.GET, null, messageListType());
    }

    static Request<Message> addMessageRequest(String authorizationHeader, Message message)
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


    private static ParameterizedTypeReference<List<Message>> messageListType()
    {
        return new ParameterizedTypeReference<List<Message>>()
        {
        };
    }
}
