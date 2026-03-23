package kr.ac.knu.comit.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode implements ErrorCode {
    /*
     * 도메인 기반 code 분리
     * common domain Code : C001 ~ C099
     */

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C000", "서버 내부 오류가 발생했습니다."),

    /*
     * 503 SERVICE_UNAVAILABLE: 서비스 일시 불가
     */
    REDIS_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "C001", "일시적으로 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;

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