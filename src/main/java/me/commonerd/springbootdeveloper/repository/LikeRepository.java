package me.commonerd.springbootdeveloper.repository;

import me.commonerd.springbootdeveloper.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByarticleIdAndUserId(Long articleId, String userId);
    int countByarticleId(Long articleId);

    void deleteByarticleIdAndUserId(Long articleId, String userId);

}
