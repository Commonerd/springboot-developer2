package me.commonerd.springbootdeveloper.repository;

import me.commonerd.springbootdeveloper.domain.Follow;
import me.commonerd.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // フォローとフォロワーでレコードを探す
    Follow findByFollowingAndFollowed(String following, String followed);

    // 現在ログインしているユーザーのフォローしているユーザー数の照会
    @Query(value = "SELECT COUNT(*) FROM follow WHERE following_user_id = ?1", nativeQuery = true)
    Long countByFollowing(String followingUser);

    // 現在ログインしているユーザーのをフォローしているユーザー数の照会
    @Query(value = "SELECT COUNT(*) FROM follow WHERE followed_user_id = ?1", nativeQuery = true)
    Long countByFollowed(String followedUser);


    // 現在ログインしているユーザーのフォローアップリストの照会
    @Query("SELECT f.followed FROM Follow f WHERE f.following = :userId")
    List<String> findFollowingByUserId(@Param("userId") String userId);

    // 現在ログインしているユーザーのフォロワー一覧を照会
    @Query("SELECT f.following FROM Follow f WHERE f.followed = :userId")
    List<String> findFollowersByUserId(@Param("userId") String userId);

    boolean existsByFollowingAndFollowed(String following, String followed);


}
