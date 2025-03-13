package com.map.map_marker.Repository;

import com.map.map_marker.Entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRecordRepository extends JpaRepository<SearchHistory, Long> {
}
