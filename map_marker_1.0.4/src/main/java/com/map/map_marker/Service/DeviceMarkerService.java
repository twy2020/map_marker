package com.map.map_marker.Service;

import com.map.map_marker.Entity.IotData;
import com.map.map_marker.Repository.DeviceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceMarkerService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    // 获取最近n条设备数据的经纬度信息
    public List<IotData> getLatestDeviceCoordinates(int n) {
        return deviceDataRepository.findLatestDeviceData(n);  // 调用新的查询方法
    }
}
