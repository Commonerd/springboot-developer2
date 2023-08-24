package me.commonerd.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.User;
import me.commonerd.springbootdeveloper.dto.LikeRequest;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import me.commonerd.springbootdeveloper.repository.LikeRepository;
import me.commonerd.springbootdeveloper.repository.UserRepository;
import me.commonerd.springbootdeveloper.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    LikeService likeService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    LikeRepository likeRepository;

    @Autowired
    BlogRepository blogRepository;

    User user;

    private Principal principal;


    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        likeRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("author1@gmail.com")
                .password("test")
                .build());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("likeArticle: いいね登録 成功")
    @Test
    public void likeArticle() throws Exception {
        // given
        final Long articleId = 1L;
        final String userId = "testuser@gmail.com";
        final String following = "author1@gmail.com";

        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setArticleId(articleId);
        likeRequest.setUserId(userId);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(userId);

        // when
        ResultActions result = mockMvc.perform(post("/likeArticle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likeRequest))
                .principal(principal));

        // then
        result.andExpect(status().isOk());
    }

    @DisplayName("cancelLikeArticle: いいねキャンセル 成功")
    @Test
    public void cancelLikeArticle() throws Exception {
        // given
        final Long articleId = 1L;
        final String userId = "testuser@gmail.com";
        final String following = "author1@gmail.com";

        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setArticleId(articleId);
        likeRequest.setUserId(userId);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(userId);

        // まず いいね　登録
        mockMvc.perform(post("/likeArticle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(likeRequest))
                        .principal(principal))
                .andExpect(status().isOk());

        // when
        ResultActions result = mockMvc.perform(delete("/cancelLikeArticle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likeRequest))
                .principal(principal));

        // then
        result.andExpect(status().isOk());

    }


    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
