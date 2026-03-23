package kr.ac.knu.comit.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * INSERT IGNORE: DB UNIQUE(post_id, member_id) 제약을 활용한 원자적 삽입.
     *
     * - 반환값 1 → 삽입 성공 (좋아요)
     * - 반환값 0 → 이미 존재 (좋아요 취소 처리)
     *
     * DataIntegrityViolationException 대신 INSERT IGNORE를 쓰는 이유:
     * 예외 발생 시 트랜잭션이 롤백 마킹되어 추가 작업이 불가능하지만,
     * INSERT IGNORE는 정상 흐름 안에서 결과값으로만 판단할 수 있다.
     */
    @Modifying(clearAutomatically = true)
    @Query(
            value = "INSERT IGNORE INTO post_like (post_id, member_id, created_at) VALUES (:postId, :memberId, NOW())",
            nativeQuery = true
    )
    int insertIgnore(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PostLike pl WHERE pl.postId = :postId AND pl.memberId = :memberId")
    void deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
