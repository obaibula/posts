package dev.demo.denson.post;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id)
            .orElseThrow(PostNotFoundException::new));
    }

    @ResponseStatus(CREATED)
    @PostMapping
    Post create(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @PutMapping({"/{id}"})
    Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
        var existing = postRepository.findById(id);
        if (existing.isPresent()) {
            var updated = new Post(
                existing.get().id(),
                existing.get().userId(),
                post.title(),
                post.body(),
                existing.get().version()
            );
            return postRepository.save(updated);
        } else {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }
}
