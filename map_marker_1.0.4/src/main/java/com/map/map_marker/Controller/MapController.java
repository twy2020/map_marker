package com.map.map_marker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/map")
    public String showMap() {
        // 这里的返回值是指向 templates 文件夹中的 map.html 页面
        return "map";
    }
}

