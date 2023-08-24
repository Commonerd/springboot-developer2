package me.commonerd.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.VideoFile;
import me.commonerd.springbootdeveloper.dto.AddArticleRequest;
import me.commonerd.springbootdeveloper.dto.ArticleResponse;
import me.commonerd.springbootdeveloper.dto.UpdateArticleRequest;
import me.commonerd.springbootdeveloper.service.BlogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

//    @PostMapping("/api/articles")
//    public ResponseEntity<Article> addArticle(@ModelAttribute AddArticleRequest request, Principal principal) {
//        Article savedArticle = blogService.save(request, principal.getName());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(savedArticle);
//    }

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam(value = "imageFiles[]", required = false) List<MultipartFile> imageFiles,
                                              Principal principal) throws Exception {
        AddArticleRequest request = new AddArticleRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setImageFiles(imageFiles);

        Article savedArticle = blogService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }


    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }


    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PostMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestParam("title") String title,
                                                 @RequestParam("content") String content,
                                                 @RequestParam(value = "imageFiles[]", required = false) List<MultipartFile> imageFiles
                                                 ) throws Exception {


        UpdateArticleRequest request = new UpdateArticleRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setImageFiles(imageFiles);


        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }

    @DeleteMapping("/api/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        blogService.deleteImage(id);
        return ResponseEntity.ok().build();
    }


}
