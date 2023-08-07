package me.commonerd.springbootdeveloper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.Follow;
import me.commonerd.springbootdeveloper.domain.User;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import me.commonerd.springbootdeveloper.repository.FollowRepository;
import me.commonerd.springbootdeveloper.repository.UserRepository;
import me.commonerd.springbootdeveloper.service.BlogService;
import me.commonerd.springbootdeveloper.service.FollowService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    BlogService blogService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    FollowService followService;

    @Autowired
    private UserRepository userRepository;

    User user;

    private Principal principal;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        followRepository.deleteAll();
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

    @DisplayName("follow: フォローアップ追加 成功")
    @Test
    public void follow() throws Exception {
        // given
        final Long articleId = 1L;
        final String following = "testuser1@gmail.com";
        final String followed = "author1@gmail.com";

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(following);


        // when
        ResultActions result = mockMvc.perform(post("/follow/" + articleId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal));

        // then
        result.andExpect(status().is2xxSuccessful());
        List<Follow> follows = followRepository.findAll();

        assertThat(follows.size()).isEqualTo(1);
        assertThat(follows.get(0).getFollowing()).isEqualTo(following);
        assertThat(follows.get(0).getFollowed()).isEqualTo(followed);

    }

    @DisplayName("unfollow: アンフォロー成功")
    @Test
    public void unfollow() throws Exception {
        // given
        final Long articleId = 1L;
        final String following = "testuser1@gmail.com";
        final String followed = "author1@gmail.com";

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(following);


        // when
        ResultActions result = mockMvc.perform(delete("/unfollow/" + articleId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal));

        // then
        result.andExpect(status().is2xxSuccessful());
        List<Follow> follows = followRepository.findAll();
        assertThat(follows).isEmpty();
    }

    @DisplayName("unfollowList: フォロワーリストからアンフォロー成功")
    @Test
    public void unfollowList() throws Exception {
        // given
        final String following = "testuser1@gmail.com";
        final String followed = "author1@gmail.com";

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(following);

        // Request Bodyに伝えるデータ構成(JSON形式でマップを生成)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", followed);


        // when
        ResultActions result = mockMvc.perform(delete("/list-unfollow/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody))
                .principal(principal));

        // then
        result.andExpect(status().isOk());

        // Followオブジェクトが削除されたか確認
        List<Follow> follows = followRepository.findAll();
        assertThat(follows).isEmpty();


    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);

    }

}



