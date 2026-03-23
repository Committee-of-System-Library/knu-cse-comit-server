package kr.ac.knu.comit.post.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ── 단건 조회 ──────────────────────────────────────────────────────────────

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Post> findActiveById(@Param("id") Long id);

    // ── 목록 조회 (No-offset cursor pagination) ────────────────────────────────
    //
    // Offset 방식은 LIMIT 10 OFFSET 10000 처럼 앞 레코드를 스캔하므로 데이터 증가 시 O(n) 열화.
    // cursor(lastId) 방식은 WHERE id < :cursor 인덱스 탐색이므로 항상 O(log n).
    //
    // JOIN FETCH p.member → 작성자 닉네임을 N+1 없이 한 번에 로드.

    @Query("""
            SELECT p FROM Post p
            JOIN FETCH p.member
            WHERE p.boardType = :boardType
              AND p.deletedAt IS NULL
              AND p.id < :cursorId
            ORDER BY p.id DESC
            """)
    List<Post> findByCursor(@Param("boardType") BoardType boardType,
                            @Param("cursorId") Long cursorId,
                            Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            JOIN FETCH p.member
            WHERE p.boardType = :boardType
              AND p.deletedAt IS NULL
            ORDER BY p.id DESC
            """)
    List<Post> findFirstPage(@Param("boardType") BoardType boardType,
                             Pageable pageable);

    // ── 원자적 카운터 업데이트 ───────────────────────────────────────────────────
    //
    // like_count = like_count + 1 은 DB에서 원자적으로 실행된다.
    // 애플리케이션에서 read → increment → write 하면 동시 요청 시 lost update 발생.
    // @Modifying + JPQL UPDATE 로 DB에 단일 쿼리 위임.

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);
}
