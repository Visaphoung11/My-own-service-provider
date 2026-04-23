//package com.smart.service.security;
//
//import com.smart.service.entity.RoleEntity;
//import com.smart.service.entity.UserEntity;
//import com.smart.service.enums.enums;
//import com.smart.service.repository.RoleRepository;
//import com.smart.service.repository.UserRepository;
//import com.smart.service.service.JwtService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Value;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
//        private final UserRepository userRepository;
//        private final RoleRepository roleRepository;
//        private final JwtService jwtService;
//        @Value("${app.frontend.url}")
//        private String frontendUrl;
//
//        @Override
//        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                        Authentication authentication) throws IOException {
//
//                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//                String email = oauthUser.getAttribute("email");
//                String name = oauthUser.getAttribute("name");
//
//                // 1. Check if user exists
//                UserEntity user = userRepository.findByEmail(email);
//
//                // 2. If user is null, we MUST create and SAVE them, then assign it back to
//                // 'user'
//                if (user == null) {
//                        RoleEntity userRole = roleRepository.findByName(enums.USER);
//
//                        user = UserEntity.builder()
//                                        .fullName(name)
//                                        .email(email)
//                                        .status("ACTIVE")
//                                        .build();
//
//                        user.addRole(userRole);
//
//                        // CRITICAL: Re-assign the saved user so it is no longer null
//                        user = userRepository.save(user);
//                }
//
//                // 3. Generate Token (Now guaranteed that 'user' is not null)
//                String token = jwtService.generateToken(user);
//
//                // 4. Create the Cookie
//                // Cookie cookie = new Cookie("jwt_token", token);
//                // cookie.setHttpOnly(true); // Secure against XSS
//                // cookie.setSecure(true); // Set to true in production (HTTPS)
//                // cookie.setPath("/"); // Available for all backend paths
//                // cookie.setMaxAge(86400); // 1 day
//                // response.addCookie(cookie);
//                String cookie = "jwt_token=" + token +
//                                "; Path=/" +
//                                "; HttpOnly" +
//                                "; Secure" +
//                                "; SameSite=None" +
//                                "; Max-Age=86400";
//
//                response.setHeader("Set-Cookie", cookie);
//
//                // 5. Redirect to React Dashboard (or your local dev port)
//                // If using React, this is usually http://localhost:5173/dashboard
//                response.sendRedirect(frontendUrl);
//        }
//}


package com.smart.service.security;

import com.smart.service.entity.RoleEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.enums;
import com.smart.service.repository.RoleRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 1. User persistence logic (Keeping your existing logic)
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            RoleEntity userRole = roleRepository.findByName(enums.USER);
            user = UserEntity.builder()
                    .fullName(name)
                    .email(email)
                    .status("ACTIVE")
                    .build();
            user.addRole(userRole);
            user = userRepository.save(user);
        }

        // 2. Token Generation
        String token = jwtService.generateToken(user);

        // 3. Create the Production-Ready Cookie
        // SameSite=None + Secure is required for cross-domain redirects (Render -> Vercel)
        String cookieHeader = String.format(
                "jwt_token=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=86400",
                token
        );
        response.setHeader("Set-Cookie", cookieHeader);

        // 4. DYNAMIC REDIRECT LOGIC
        // Default to the Vercel URL (e.g., https://soksabay-go-booking-tour.vercel.app)
        String targetUrl = frontendUrl;

        // Check if the login started from your local machine
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("localhost")) {
            targetUrl = "http://localhost:5173";
        }

        // Redirect to your landing page
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}