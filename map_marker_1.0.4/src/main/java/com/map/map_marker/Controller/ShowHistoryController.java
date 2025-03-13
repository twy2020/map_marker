package com.map.map_marker.Controller;

import com.map.map_marker.WebSoket.MyWebSocketHandler;
import com.map.map_marker.Entity.SearchHistory;
import com.map.map_marker.Repository.SearchRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class ShowHistoryController {

    @Autowired
    private SearchRecordRepository searchRecordRepository;

    @PostMapping
    public SearchHistory saveSearchRecord(@RequestBody SearchHistory record) {
        record.setTimestamp(LocalDateTime.now());
        SearchHistory savedRecord = searchRecordRepository.save(record);

        // 通过 WebSocket 发送新记录到所有连接的客户端
        MyWebSocketHandler.sendMessageToAll(recordToJson(savedRecord));

        return savedRecord;
    }

    @GetMapping
    public List<SearchHistory> getAllSearchRecords() {
        return searchRecordRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteSearchRecord(@PathVariable Long id) {
        searchRecordRepository.deleteById(id);
    }

    // 将记录转换为 JSON 字符串（可使用 JSON 序列化库）
    private String recordToJson(SearchHistory record) {
        // 这里简单示例，建议使用 ObjectMapper 或类似工具
        return "{\"id\":" + record.getId() + ",\"timestamp\":\"" + record.getTimestamp() + "\",\"longitude\":" + record.getLongitude() + ",\"latitude\":" + record.getLatitude() + "}";
    }
}
