package com.map.map_marker.Service;

import com.map.map_marker.Entity.IotData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.map.map_marker.Repository.DeviceDataRepository;

import java.util.List;

@Service
public class IotDataSearchService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    // 获取最新设备数据（按时间戳倒序，获取最新的记录）
    public IotData getLatestDeviceData() {
        // 获取最新的一条设备数据
        return deviceDataRepository.findTop1ByOrderByTimestampDesc(); // 获取按时间戳倒序排列的第一条数据
    }
}
