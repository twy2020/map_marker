package com.map.map_marker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        // 返回的 "login" 会自动映射到 templates/login.html（假设使用 Thymeleaf）
        return "login";
    }
}
