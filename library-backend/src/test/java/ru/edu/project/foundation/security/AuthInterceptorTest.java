package ru.edu.project.foundation.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AuthInterceptorTest {

    private AuthInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new AuthInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void preflight_OPTIONS_is_allowed() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void no_session_returns_401() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(null);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).sendError(anyInt(), anyString());
    }

    @Test
    void authenticated_without_role_requirement_is_allowed() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(AuthInterceptor.SESSION_USER)).thenReturn(1L);
        when(request.getSession(false)).thenReturn(session);

        HandlerMethod hm = mock(HandlerMethod.class);
        when(hm.getMethodAnnotation(RequireRole.class)).thenReturn(null);

        assertTrue(interceptor.preHandle(request, response, hm));
    }

    @Test
    void wrong_role_returns_403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(AuthInterceptor.SESSION_USER)).thenReturn(1L);
        when(session.getAttribute(AuthInterceptor.SESSION_ROLE)).thenReturn("USER");
        when(request.getSession(false)).thenReturn(session);

        RequireRole rr = mock(RequireRole.class);
        when(rr.value()).thenReturn("LIBRARIAN");
        HandlerMethod hm = mock(HandlerMethod.class);
        when(hm.getMethodAnnotation(RequireRole.class)).thenReturn(rr);

        assertFalse(interceptor.preHandle(request, response, hm));
        verify(response).sendError(anyInt(), anyString());
    }

    @Test
    void matching_role_is_allowed() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(AuthInterceptor.SESSION_USER)).thenReturn(1L);
        when(session.getAttribute(AuthInterceptor.SESSION_ROLE)).thenReturn("LIBRARIAN");
        when(request.getSession(false)).thenReturn(session);

        RequireRole rr = mock(RequireRole.class);
        when(rr.value()).thenReturn("LIBRARIAN");
        HandlerMethod hm = mock(HandlerMethod.class);
        when(hm.getMethodAnnotation(RequireRole.class)).thenReturn(rr);

        assertTrue(interceptor.preHandle(request, response, hm));
    }
}
