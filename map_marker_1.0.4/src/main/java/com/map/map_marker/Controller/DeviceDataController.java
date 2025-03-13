package com.map.map_marker.Controller;

import com.map.map_marker.Entity.IotData;
import com.map.map_marker.Repository.IotDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class DeviceDataController {

    @Autowired
    private IotDataRepository iotDataRepository;

    @GetMapping("/device-data")
    public String viewDeviceData(@RequestParam("deviceId") Long deviceId, Model model) {
        List<IotData> dataList = iotDataRepository.findByDeviceId(deviceId);
        model.addAttribute("deviceDataList", dataList);
        model.addAttribute("deviceId", deviceId);
        return "deviceData"; // 对应 src/main/resources/templates/deviceData.html
    }
}
