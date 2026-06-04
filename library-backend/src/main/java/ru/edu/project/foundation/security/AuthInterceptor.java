package ru.edu.project.foundation.security;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Централизованная авторизация на основе серверной сессии.
 * Заменяет дублировавшиеся проверки session.getAttribute(...) в каждом методе контроллеров.
 *
 * Применяется ко всем /api/** (кроме публичных эндпоинтов, исключённых в WebConfig):
 *  - требует наличия аутентифицированного пользователя в сессии (иначе 401);
 *  - при наличии аннотации {@link RequireRole} проверяет роль (иначе 403).
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_ROLE = "userRole";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS preflight пропускаем
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object currentUser = session != null ? session.getAttribute(SESSION_USER) : null;

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Авторизуйтесь в системе");
            return false;
        }

        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole != null) {
                Object role = session.getAttribute(SESSION_ROLE);
                if (!requireRole.value().equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Недостаточно прав");
                    return false;
                }
            }
        }
        return true;
    }
}
