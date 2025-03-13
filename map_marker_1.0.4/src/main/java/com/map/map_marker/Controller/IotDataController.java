package com.map.map_marker.Controller;

import com.map.map_marker.Entity.IotData;
import com.map.map_marker.Service.DeviceMarkerService;
import com.map.map_marker.Service.IotDataSearchService;
import com.map.map_marker.Service.IotDataFetchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/iot-data")
public class IotDataController {

    @Autowired
    private IotDataFetchService iotDataService;
    @Autowired
    private IotDataSearchService iotDataSearchService;
    @Autowired
    private DeviceMarkerService deviceMarkerService;

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchDataAndSave() {
        try {
            iotDataService.fetchDataAndSave();
            return ResponseEntity.ok("Data fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching and saving data: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<IotData> getLatestIotData() {
        try {
            // 调用新的服务类获取最新设备数据
            IotData latestDeviceData = iotDataSearchService.getLatestDeviceData();

            if (latestDeviceData == null) {
                return ResponseEntity.status(404).body(null);  // 如果没有数据，返回 404
            }

            return ResponseEntity.ok(latestDeviceData);  // 返回最新的设备数据

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 如果发生异常，返回 500 错误
        }
    }

    @GetMapping("/markers")
    public ResponseEntity<List<IotData>> getDeviceMarkers(@RequestParam int n) {
        try {
            // 获取最新的n条设备数据的经纬度信息
            List<IotData> deviceCoordinates = deviceMarkerService.getLatestDeviceCoordinates(n);

            if (deviceCoordinates.isEmpty()) {
                return ResponseEntity.status(404).body(null);  // 如果没有数据，返回 404
            }

            return ResponseEntity.ok(deviceCoordinates);  // 返回设备的经纬度数据

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 如果发生异常，返回 500 错误
        }
    }
}
