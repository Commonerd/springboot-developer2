package me.commonerd.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.User;
import me.commonerd.springbootdeveloper.dto.AddArticleRequest;
import me.commonerd.springbootdeveloper.dto.UpdateArticleRequest;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import me.commonerd.springbootdeveloper.repository.ImageRepository;
import me.commonerd.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }


    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

//    @DisplayName("addArticle: 아티클 추가에 성공한다.")
//    @Test
//    public void addArticle() throws Exception {
//        // given
//        final String url = "/api/articles";
//        final String title = "title";
//        final String content = "content";
//        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
//
//        final String requestBody = objectMapper.writeValueAsString(userRequest);
//
//        Principal principal = Mockito.mock(Principal.class);
//        Mockito.when(principal.getName()).thenReturn("username");
//
//        // when
//        ResultActions result = mockMvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .principal(principal)
//                .content(requestBody));
//
//        // then
//        result.andExpect(status().isCreated());
//
//        List<Article> articles = blogRepository.findAll();
//
//        assertThat(articles.size()).isEqualTo(1);
//        assertThat(articles.get(0).getTitle()).isEqualTo(title);
//        assertThat(articles.get(0).getContent()).isEqualTo(content);
//    }


    @DisplayName("addArticle: 아티클 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        // 임의의 바이트 배열 생성 (예: 100바이트)
        byte[] dummyImageBytes = new byte[100];


        // 이미지 파일 테스트용 MultipartFile 생성
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFiles",
                "test-image.jpg",
                "image/jpeg",
                dummyImageBytes); // 실제 이미지 바이트 배열

        MockMultipartHttpServletRequestBuilder multipartRequestBuilder = (MockMultipartHttpServletRequestBuilder) multipart(url)
                .file(imageFile)
                .param("title", title)
                .param("content", content);


        final AddArticleRequest userRequest = new AddArticleRequest(title, content, null);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");


        // when
//        ResultActions result = mockMvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .principal(principal)
//                .content(requestBody));
        ResultActions result = mockMvc.perform(multipartRequestBuilder.principal(principal));


        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 아티클 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

//    @DisplayName("findArticle: 아티클 단건 조회에 성공한다.")
//    @Test
//    public void findArticle() throws Exception {
//        // given
//        final String url = "/api/articles/{id}";
//        Article savedArticle = createDefaultArticle();
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
//                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
//
//    }

    @DisplayName("findArticle: 아티클 단건 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // given
        Article savedArticle = createArticleWithImage();

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/articles/{id}", savedArticle.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle())
                /*.andExpect(jsonPath("$.imageFiles[0].filename").value(savedArticle.getImageFiles().get(0).getFilename())*/);
    }




//    @DisplayName("deleteArticle: 아티클 삭제에 성공한다.")
//    @Test
//    public void deleteArticle() throws Exception {
//        // given
//        final String url = "/api/articles/{id}";
//        Article savedArticle = createDefaultArticle();
//
//        // when
//        mockMvc.perform(delete(url, savedArticle.getId()))
//                .andExpect(status().isOk());
//
//        // then
//        List<Article> articles = blogRepository.findAll();
//
//        assertThat(articles).isEmpty();
//    }

    @DisplayName("deleteArticle: 아티클 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createArticleWithImage(); // 글 생성과 함께 이미지 첨부파일을 가지는 글을 생성함

        // when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        // then
        List<Article> articles = blogRepository.findAll();
        List<ImageFile> imageFiles = imageRepository.findAll();

        assertThat(articles).isEmpty();
        assertThat(imageFiles).isEmpty(); // 첨부파일도 삭제되었는지 검증
    }



//    @DisplayName("updateArticle: 아티클 수정에 성공한다.")
//    @Test
//    public void updateArticle() throws Exception {
//        // given
//        final String url = "/api/articles/{id}";
//        Article savedArticle = createDefaultArticle();
//
//        final String newTitle = "new title";
//        final String newContent = "new content";
//
//        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);
//
//        // when
//        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request)));
//
//        // then
//        result.andExpect(status().isOk());
//
//        Article article = blogRepository.findById(savedArticle.getId()).get();
//
//        assertThat(article.getTitle()).isEqualTo(newTitle);
//        assertThat(article.getContent()).isEqualTo(newContent);
//    }

    @DisplayName("updateArticle: 아티클 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";
        byte[] newImageBytes = new byte[100]; // 실제 이미지 바이트 배열

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent, null);

        // 이미지 파일 테스트용 MultipartFile 생성
        MockMultipartFile newImageFile = new MockMultipartFile(
                "newImageFiles",
                "test-new-image.jpg",
                "image/jpeg",
                newImageBytes);

        // when
        ResultActions result = mockMvc.perform(multipart(url, savedArticle.getId())
                .file(newImageFile)
                .param("title", newTitle)
                .param("content", newContent));

        // then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
        // 여기서 이미지 관련 검증 로직을 추가하면 됩니다.
    }




    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }

    private Article createArticleWithImage() {
        // 이미지 파일 테스트용 MultipartFile 생성
        byte[] imageBytes = new byte[100];
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFiles",
                "test-image.jpg",
                "image/jpeg",
                imageBytes);

        // 아티클 생성 및 이미지 파일 설정
        Article article = Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build();

        ImageFile image = new ImageFile();
        image.setFilename(imageFile.getOriginalFilename());
        image.setArticle(article);

        article.setImageFiles(Collections.singletonList(image));

        return blogRepository.save(article);
    }

}
