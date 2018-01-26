[![Build Status](https://travis-ci.org/slotik/spring-messaging.svg?branch=master)](https://travis-ci.org/slotik/spring-messaging)
# Spring Messaging
A simple message store implemented as a Spring application.

It provides a simple REST API for adding, retrieving, updating and deleting messages.
Each message has an author and only the author may edit or delete his own messages.
Authentication and authorization is implemented in a completely insecure and non-standard way, mostly just to demonstrate how the code might be structured.

## Requirements to build

* JDK 8 or above

## Build and Test

```
./mvnw install
```

## Run

```
./mvnw spring-boot:run
```

### Adding a message:

```
$ curl -H "Content-Type: application/json" -H "Authorization: Silly user" -X POST http://localhost:8080/messages -d '{ "userId": "user", "content": "sample content" }'
{"id":1,"userId":"user","content":"sample content"}

$ curl -H "Content-Type: application/json" -H "Authorization: Silly anotherUser" -X POST http://localhost:8080/messages -d '{ "userId": "anotherUser", "content": "another content" }'
{"id":2,"userId":"anotherUser","content":"another content"}
```
### Listing messages:

```
$ curl -X GET localhost:8080/messages
[{"id":1,"userId":"user","content":"sample content"},{"id":2,"userId":"anotherUser","content":"another content"}]
```

### Updating a message:

```
$ curl -H "Content-Type: application/json" -H "Authorization: Silly user" -X PUT http://localhost:8080/messages/1 -d '{ "userId": "user", "content": "some new content" }'
$ curl -X GET localhost:8080/messages/1
{"id":1,"userId":"user","content":"new content"}
```

### Deleting a message:

```
$ curl -H "Content-Type: application/json" -H "Authorization: Silly user" -X DELETE http://localhost:8080/messages/1
$ curl -X GET localhost:8080/messages
[{"id":2,"userId":"anotherUser","content":"another content"}]
```
