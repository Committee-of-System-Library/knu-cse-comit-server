package kr.ac.knu.comit.global.auth;

import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @AuthenticatedMember 파라미터 → MemberPrincipal 주입
 *
 * TODO: KNU CSE SSO Starter 연동 후 SecurityContextHolder에서 실제 JWT claims 추출로 교체.
 *       현재는 Request Attribute 기반 구현 (SSO Filter가 "memberPrincipal" attribute를 세팅한다고 가정).
 */
@Component
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String PRINCIPAL_ATTRIBUTE = "memberPrincipal";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedMember.class)
                && parameter.getParameterType().equals(MemberPrincipal.class);
    }

    @Override
    public MemberPrincipal resolveArgument(MethodParameter parameter,
                                           ModelAndViewContainer mavContainer,
                                           NativeWebRequest webRequest,
                                           WebDataBinderFactory binderFactory) {
        Object principal = webRequest.getAttribute(PRINCIPAL_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (principal == null) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED);
        }
        return (MemberPrincipal) principal;
    }
}
