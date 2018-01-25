package info.slotik.toys.messaging;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

interface Request<T>
{
    ResponseEntity<T> exchange(TestRestTemplate template, URI baseURI);
}
