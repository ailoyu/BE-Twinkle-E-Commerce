package com.twinkle.shopapp.filters;

import com.twinkle.shopapp.component.JwtTokenUtils;
import com.twinkle.shopapp.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
// mỗi request phải dc kiểm JWT token ở class này
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                        throws ServletException, IOException {

        try {
            // Các danh sách API được pass ko cần sử dụng token
            if(isBypassToken(request)){
                // Xác thực JWT và cho pass qua
                filterChain.doFilter(request, response);
                return;
            }

            // BearerToken ban đầu
            final String bearerToken = request.getHeader("Authorization");

            // Check token cho các API còn lại
            if(bearerToken == null || !bearerToken.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa được xác thực!");
                return;
            }


            // Tách chuỗi token đằng sau chữ "Bearer"
            String token = bearerToken.substring(7);
            // Lấy phoneNumber ra từ token
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            if(phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null){
                // Kiểm tra nếu chua authenticate, thì bắt đầu authenticate
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                // Kiểm tra phoneNumber có trùng ko và token hết hạn chưa?
                if(jwtTokenUtils.validateTokenAndCheckPhoneNumber(token, userDetails)){
                    // Bắt đầu authentication
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            // Xác thực JWT và cho pass qua
            filterChain.doFilter(request, response);

        }catch (Exception e){
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa được xác thực!");
        }


    }

    private boolean isBypassToken(@NonNull HttpServletRequest request){
        // Các danh sách API được pass ko cần sử dụng token
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/orders", apiPrefix), "POST"),
                Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                Pair.of(String.format("%s/providers", apiPrefix), "GET"),
//                Pair.of(String.format("%s/vn_pay", apiPrefix), "GET"),
                Pair.of(String.format("%s/products/best-sellers", apiPrefix), "GET"),
                Pair.of(String.format("%s/products/products-from-category/**", apiPrefix), "GET")

        );
        for(Pair<String, String> bypassToken : bypassTokens){
            System.out.println(request.getServletPath());
            System.out.println(bypassToken.getLeft());
            if(request.getServletPath().contains(bypassToken.getLeft()) &&
                    request.getMethod().equals(bypassToken.getRight())){
                // Xác thực JWT và cho pass qua
                return true;
            }
        }
        return false;
    }





}
