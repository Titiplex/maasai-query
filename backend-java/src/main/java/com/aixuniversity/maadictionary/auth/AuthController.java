package com.aixuniversity.maadictionary.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager auth;
    private final JwtUtil jwt;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDto dto) {
        Neo4jProperties.Authentication a = auth.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        SecurityProperties.User u = (SecurityProperties.User) a.getPrincipal();
        return Map.of("token", jwt.generate(u.getUsername(),
                u.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList()));
    }
}
