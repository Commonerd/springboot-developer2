package me.commonerd.springbootdeveloper.controller;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import lombok.RequiredArgsConstructor;
import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.dto.ArticleListViewResponse;
import me.commonerd.springbootdeveloper.dto.ArticleViewResponse;
import me.commonerd.springbootdeveloper.service.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

   /*@GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }*/

    @GetMapping("/articles")
    public String getArticles(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<ArticleListViewResponse> pagedArticles = blogService.findPaginated(page, 9)
                .map(ArticleListViewResponse::new);
        model.addAttribute("pagedArticles", pagedArticles);

        return "articleList";
    }


    // 2023.06.05. by commonerd : 제목으로 검색기능 추가, index-100
/*    //>>>>> 100
    @GetMapping("/search-articles")
    public String getArticlesBySearch(@RequestParam(name = "title", required = false) String title,
                                      *//*@RequestParam(name = "content", required = false) String content,*//*
                                      Model model) {
        List<ArticleListViewResponse> articles = blogService.findSearch(
                        title != null ? title : ""
                        *//*content != null ? content : ""*//*
                )
                .stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);
        return "articleListSearch";
    }
    //<<<<< 100*/

    //2023.06.06.by commonerd : 작성자,제목,내용 검색기능 추가, index-100
    //>>>>> 100
    @GetMapping("/search-articles")
    public String searchArticles(@RequestParam(name = "keyword", required = false) String keyword,
                                 Model model) {
        List<ArticleListViewResponse> pagedArticles = blogService.searchArticles(keyword);
        model.addAttribute("pagedArticles", pagedArticles);
        return "articleListSearch";
    }
    //<<<<< 100

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }


    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }

    @GetMapping("/rankings")
    @ResponseBody
    public List<Object[]> getArticleRanking() {
        //List<Object[]> ranking = blogService.findArticleRanking();
        //model.addAttribute("ranking", ranking);
        return blogService.findArticleRanking();
    }


}
