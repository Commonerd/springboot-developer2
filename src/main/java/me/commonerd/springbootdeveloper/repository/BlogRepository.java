package me.commonerd.springbootdeveloper.repository;

import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.dto.ArticleListViewResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Article, Long> {

    //2023.06.06.by commonerd : 작성자,제목,내용 검색기능 추가, index-100
    //>>>>> 100
    @Query("SELECT a " +
            "FROM Article a " +
            "WHERE (a.author LIKE %:keyword%) " +
            "OR (a.title LIKE %:keyword%) " +
            "OR (a.content LIKE %:keyword%)")
    List<ArticleListViewResponse> searchArticles(@Param("keyword") String keyword); // @Param 추가해야 빈 생성됨
    //<<<<< 100

    @Query("SELECT a.author, count(*) FROM Article a GROUP BY author ORDER BY count(*) DESC LIMIT 5")
    List<Object[]> findTop5Ranking();

}
