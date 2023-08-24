package me.commonerd.springbootdeveloper.repository;

import me.commonerd.springbootdeveloper.domain.Article;
import me.commonerd.springbootdeveloper.domain.ImageFile;
import me.commonerd.springbootdeveloper.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByArticle(Article article);


}
