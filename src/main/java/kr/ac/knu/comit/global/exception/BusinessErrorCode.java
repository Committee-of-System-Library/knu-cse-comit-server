package kr.ac.knu.comit.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 현재 애플리케이션에서 사용하는 비즈니스 에러 코드의 중앙 정의.
 *
 * @implNote {@code code}의 접두 문자는 소속 도메인을 나타낸다.
 * 공통 도메인은 {@code C}, 회원은 {@code M}, 게시글은 {@code P},
 * 댓글은 {@code CM}을 사용한다.
 */
public enum BusinessErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C000", "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,                   "C001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,                         "C002", "접근 권한이 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,        "M001", "존재하지 않는 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT,        "M002", "이미 사용 중인 닉네임입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST,       "M003", "닉네임은 1~50자이어야 합니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND,           "P001", "존재하지 않는 게시글입니다."),
    INVALID_TITLE(HttpStatus.BAD_REQUEST,          "P002", "제목은 1~255자이어야 합니다."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST,        "P003", "내용을 입력해주세요."),
    TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,     "P004", "태그는 최대 5개까지 입력할 수 있습니다."),
    INVALID_TAG(HttpStatus.BAD_REQUEST,            "P005", "태그는 1~20자이어야 합니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,        "CM001", "존재하지 않는 댓글입니다."),
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "CM002", "댓글 내용을 입력해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    BusinessErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status.value();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
