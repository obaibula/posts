package dev.demo.denson.post;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    private List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        posts = List.of(
            new Post(1, 1, "Hello, World!", "This is my first post.", null),
            new Post(2, 1, "Second Post", "This is my second post.", null)
        );
    }

    // REST API

    // list
    @Test
    void shouldFindAllPosts() throws Exception {
        var jsonResponse = """
            [
                {
                    "id":1,
                    "userId":1,
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "version": null
                },
                {
                    "id":2,
                    "userId":1,
                    "title":"Second Post",
                    "body":"This is my second post.",
                    "version": null
                }
            ]
            """;

        when(postRepository.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        Post post = new Post(1, 1, "Test Title", "Test Body", null);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        String json = STR. """
                {
                    "id":\{ post.id() },
                    "userId":\{ post.userId() },
                    "title":"\{ post.title() }",
                    "body":"\{ post.body() }",
                    "version": null
                }
                """ ;

        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(json));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get("/api/posts/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenPostIsValid() throws Exception {
        var post = new Post(3, 1, "NEW TITLE", "NEW BODY", null);
        when(postRepository.save(post)).thenReturn(post);
        String json = STR. """
                {
                    "id":\{ post.id() },
                    "userId":\{ post.userId() },
                    "title":"\{ post.title() }",
                    "body":"\{ post.body() }",
                    "version": null
                }
                """ ;

        mockMvc.perform(post("/api/posts")
                .contentType(APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreatPostWhenPostIsInvalid() throws Exception {
        var post = new Post(3, 1, "", "", null);
        when(postRepository.save(post)).thenReturn(post);
        String json = STR. """
                {
                    "id":\{ post.id() },
                    "userId":\{ post.userId() },
                    "title":"\{ post.title() }",
                    "body":"\{ post.body() }",
                    "version": null
                }
                """ ;

        mockMvc.perform(post("/api/posts")
                .contentType(APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        var updated = new Post(1, 1, "This is a new title", "this is a new body", 1);

        when(postRepository.findById(1)).thenReturn(Optional.of(updated));
        when(postRepository.save(updated)).thenReturn(updated);
        String requestBody = STR. """
                {
                    "id":\{ updated.id() },
                    "userId":\{ updated.userId() },
                    "title":"\{ updated.title() }",
                    "body":"\{ updated.body() }",
                    "version":\{ updated.version() }
                }
                """ ;

        mockMvc.perform(put("/api/posts/1")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());

    }

    @Test
    void shouldDeletePostWhenGivenValidId() throws Exception{
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isNoContent());

        verify(postRepository, only()).deleteById(1);
    }

}
