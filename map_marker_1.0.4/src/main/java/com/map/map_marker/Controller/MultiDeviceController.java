package com.map.map_marker.Controller;

import com.map.map_marker.Entity.IotData;
import com.map.map_marker.Repository.IotDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class MultiDeviceController {

    @Autowired
    private IotDataRepository iotDataRepository;

    @GetMapping("/multi-device-management")
    public String multiDeviceManagement(Model model) {
        List<Object[]> queryResult = iotDataRepository.findDeviceRecordCounts();
        List<Map<String, Object>> deviceRecords = new ArrayList<>();
        for (Object[] row : queryResult) {
            Map<String, Object> record = new HashMap<>();
            record.put("deviceId", row[0]);
            record.put("recordCount", row[1]);
            deviceRecords.add(record);
        }
        model.addAttribute("deviceRecords", deviceRecords);
        return "multiDeviceManagement"; // 对应 multiDeviceManagement.html
    }

    // 添加设备接口
    @PostMapping("/api/device")
    @ResponseBody
    public Map<String, Object> addDevice(@RequestParam("deviceId") Long deviceId) {
        List<IotData> list = iotDataRepository.findByDeviceId(deviceId);
        if (list.isEmpty()) {
            // 创建一条默认记录（示意用，可根据实际需求调整默认数据）
            IotData newData = new IotData();
            newData.setDeviceId(deviceId);
            newData.setTemperature(null);
            newData.setHumidity(null);
            newData.setWindSpeed(null);
            newData.setWindDirection(null);
            newData.setLongitude(null);
            newData.setLatitude(null);
            newData.setWeather(null);
            newData.setTimestamp(LocalDateTime.now());
            newData.setDeviceTimestamp(LocalDateTime.now());
            iotDataRepository.save(newData);
        }
        Long count = (long) iotDataRepository.findByDeviceId(deviceId).size();
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("recordCount", count);
        return result;
    }
}
