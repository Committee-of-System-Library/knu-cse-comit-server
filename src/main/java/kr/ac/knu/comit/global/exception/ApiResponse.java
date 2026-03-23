package kr.ac.knu.comit.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final ResultType result;
    private final T data;
    private String code;
    private String message;

    public static <S> ApiResponse<S> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null, null);
    }

    public static <S> ApiResponse<S> success(S data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null, null);
    }

    public static <S> ApiResponse<S> success(String message) {
        return new ApiResponse<>(ResultType.SUCCESS, null, null, message);
    }

    public static <S> ApiResponse<S> success(S data, String message) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null, message);
    }

    public static ApiResponse<?> error(ErrorCode error) {
        return new ApiResponse<>(ResultType.FAIL, null, error.getCode(), error.getMessage());
    }

    public static ApiResponse<?> error(MethodArgumentNotValidException error) {
        String message = Stream.concat(
                        error.getBindingResult().getFieldErrors().stream()
                                .map(e -> e.getField() + ": " + e.getDefaultMessage()),
                        error.getBindingResult().getGlobalErrors().stream()
                                .map(e -> e.getObjectName() + ": " + e.getDefaultMessage())
                )
                .collect(Collectors.joining(", "));
        if (message.isBlank()) {
            message = "요청 값이 유효하지 않습니다.";
        }
        return new ApiResponse<>(ResultType.FAIL, null, null, message);
    }

    public static ApiResponse<?> error(Exception error) {
        return new ApiResponse<>(ResultType.FAIL, null, null, error.getMessage());
    }
}
