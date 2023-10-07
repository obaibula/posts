package dev.demo.denson.post;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(NOT_FOUND)
class PostNotFoundException extends RuntimeException{

}
