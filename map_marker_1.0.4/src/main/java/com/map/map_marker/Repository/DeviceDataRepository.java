package com.map.map_marker.Repository;

import com.map.map_marker.Entity.IotData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceDataRepository extends JpaRepository<IotData, Long> {
    // 获取最新的一条设备数据
    IotData findTop1ByOrderByTimestampDesc();

    // 使用 Pageable 获取最新的n条设备记录
    default List<IotData> findLatestDeviceData(int n) {
        // 显式指定按 timestamp 降序排序
        Page<IotData> page = findAll(PageRequest.of(0, n, Sort.by(Sort.Order.desc("timestamp"))));
        return page.getContent(); // 返回当前页的内容
    }

    // 可以添加自定义查询方法
}
