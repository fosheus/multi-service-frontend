package fr.albanj.corelib.servicecore.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RemoteAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public RemoteAuthenticationSuccessHandler(String authSuccessRedirect) {
        super(authSuccessRedirect);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        String token = (String) authentication.getCredentials();

        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        // redirige vers la page définie dans l'application portail
        getRedirectStrategy().sendRedirect(request, response, getDefaultTargetUrl());
    }
}
