package me.commonerd.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.dto.AddArticleRequest;
import me.commonerd.springbootdeveloper.dto.ArticleListViewResponse;
import me.commonerd.springbootdeveloper.dto.UpdateArticleRequest;
import me.commonerd.springbootdeveloper.repository.BlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Page<Article> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return blogRepository.findAll(pageable);
    }


    /*public List<Article> findSearch(String title) {
        return blogRepository.findByTitleContaining(title);
    }*/

    //2023.06.06.by commonerd : 작성자,제목,내용 검색기능 추가, index-100
    //>>>>> 100
    public List<ArticleListViewResponse> searchArticles(String keyword) {
        //Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return blogRepository.searchArticles(keyword);
    }
    //<<<<< 100

    public List<Object[]> findArticleRanking() {
        return blogRepository.findTop5Ranking();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }




}