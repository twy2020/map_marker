package com.map.map_marker.Repository;

import com.map.map_marker.Entity.IotData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface IotDataRepository extends JpaRepository<IotData, Long> {

    @Query("SELECT d.deviceId, COUNT(d) FROM IotData d GROUP BY d.deviceId")
    List<Object[]> findDeviceRecordCounts();

    // 根据设备ID查询所有数据
    List<IotData> findByDeviceId(Long deviceId);
}
