package com.map.map_marker.Service;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.iot.model.v20180120.QueryDevicePropertyStatusRequest;
import com.aliyuncs.iot.model.v20180120.QueryDevicePropertyStatusResponse;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.map.map_marker.Entity.IotData;
import com.map.map_marker.Repository.DeviceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class IotDataFetchService {

    @Value("${aliyun.iot.regionId}")
    private String regionId;

    @Value("${aliyun.iot.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.iot.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.iot.iotInstanceId}")
    private String iotInstanceId;

    @Value("${aliyun.iot.deviceName}")
    private String deviceName;

    @Value("${aliyun.iot.productKey}")
    private String productKey;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    private DefaultAcsClient client;
    private static final Logger logger = LoggerFactory.getLogger(IotDataFetchService.class);

    @PostConstruct
    public void init() {
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
    }

    @Scheduled(fixedRateString = "${task.scheduled.interval}")
    public void fetchDataAndSave() {
        QueryDevicePropertyStatusRequest request = new QueryDevicePropertyStatusRequest();
        request.setRegionId(regionId);
        request.setIotInstanceId(iotInstanceId);
        request.setDeviceName(deviceName);
        request.setProductKey(productKey);

        try {
            // 发送请求获取响应
            QueryDevicePropertyStatusResponse response = client.getAcsResponse(request);
            String responseJson = JSON.toJSONString(response); // 将响应对象转为字符串
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(responseJson);
            JsonObject data = (JsonObject) jsonObject.get("data");

            if (data == null || !data.has("list")) {
                logger.error("Missing or empty 'list' in the response data.");
                return;
            }

            JsonArray list = (JsonArray) data.get("list");
            //logger.info("Response JSON: {}", responseJson); // 打印整个响应

            // 查找包含时间戳的字段
            long deviceTimestamp = 0;
            for (int i = 0; i < list.size(); i++) {
                JsonObject item = list.get(i).getAsJsonObject();
                if (item != null && item.has("time")) {
                    String timeStampStr = item.get("time").getAsString();
                    deviceTimestamp = Long.parseLong(timeStampStr);
                    break; // 找到第一个包含 time 字段的项，退出循环
                }
            }

            if (deviceTimestamp == 0) {
                logger.error("No valid 'time' field found in the response data.");
                return; // 如果没有找到有效的时间戳，退出方法
            }

            // 将时间戳转换为 LocalDateTime（设备数据时间）
            LocalDateTime deviceTimestampDateTime = Instant.ofEpochMilli(deviceTimestamp)
                    .atZone(ZoneOffset.ofHours(8)) // 根据时区调整为 UTC+8
                    .toLocalDateTime();

            // 获取数据库中最新一条记录的设备时间戳
            IotData latestDeviceData = deviceDataRepository.findTop1ByOrderByTimestampDesc();
            if (latestDeviceData != null) {
                LocalDateTime lastDeviceTimestamp = latestDeviceData.getDeviceTimestamp();
                // 新数据的时间戳必须大于数据库中最新记录的时间戳才能保存
                if (deviceTimestampDateTime.isBefore(lastDeviceTimestamp) || deviceTimestampDateTime.isEqual(lastDeviceTimestamp)) {
                    // logger.info("No new data to save, device timestamp is not greater than the last saved timestamp.");
                    return; // 如果新数据的时间戳小于或等于数据库中的时间戳，则不保存数据
                }
            }


            // 获取当前时间（数据入库时间）
            LocalDateTime now = LocalDateTime.now();

            // 提取其他数据并做空值检查
            String temperature = getStringValueFromJsonArray(list, 0);
            String humidity = getStringValueFromJsonArray(list, 1);
            //String ledStatus = getStringValueFromJsonArray(list, 2);
            String windSpeed = getStringValueFromJsonArray(list, 2);
            String windDirection = getStringValueFromJsonArray(list, 3);
            String longitude = getStringValueFromJsonArray(list, 4);
            String latitude = getStringValueFromJsonArray(list, 5);

            // 如果经纬度为0，则不保存记录
            if ("0.0".equals(longitude) || "0.0".equals(latitude)) {
                logger.info("Skipping record with invalid longitude or latitude (0.0).");
                return; // 不保存这条记录
            }

            String weather = "";

            if (temperature == null || humidity == null || windSpeed == null || windDirection == null || longitude == null || latitude == null) {
                weather = "天气数据不完整";
            } else {
                double temp = Double.parseDouble(temperature);
                double hum = Double.parseDouble(humidity);
                double wind = Double.parseDouble(windSpeed);

                if (temp > 30) {
                    weather = "高温天气，注意防暑降温";
                } else if (temp < 0) {
                    weather = "寒冷天气，注意防寒";
                } else if (hum > 80) {
                    weather = "湿度过高，可能会有雨";
                } else if (hum < 20) {
                    weather = "湿度过低，注意补水";
                } else if (wind > 10) {
                    weather = "强风警告，注意安全";
                } else if (wind > 5) {
                    weather = "有风，适合户外活动";
                } else {
                    weather = "阴天天气，注意保暖";
                }
            }


            // 创建实体对象并填充数据
            IotData iotData = new IotData();
            iotData.setTemperature(parseDouble(temperature));
            iotData.setHumidity(parseInteger(humidity));
            //iotData.setLedStatus(parseBoolean(ledStatus));
            iotData.setWindSpeed(parseDouble(windSpeed));
            iotData.setWindDirection(windDirection);  // 风向直接作为字符串
            iotData.setLongitude(parseDouble(longitude));
            iotData.setLatitude(parseDouble(latitude));
            iotData.setDeviceTimestamp(deviceTimestampDateTime); // 使用从设备获取的时间戳
            iotData.setTimestamp(now); // 使用系统当前时间作为入库时间
            iotData.setWeather(weather);


            // 保存数据到数据库
            deviceDataRepository.save(iotData);
            logger.info("Device data saved successfully: {}", iotData);

        } catch (Exception e) {
            logger.error("Error fetching and saving data", e);
        }
    }

    // 从 JsonArray 中提取字符串值，避免空值
    private String getStringValueFromJsonArray(JsonArray list, int index) {
        if (list != null && list.size() > index) {
            JsonObject item = list.get(index).getAsJsonObject();
            if (item != null && item.has("value") && !item.get("value").isJsonNull()) {
                return item.get("value").getAsString();
            }
        }
        return ""; // 默认返回空字符串
    }

    // 安全地解析 Double 类型，防止解析错误
    private Double parseDouble(String value) {
        try {
            return value != null && !value.isEmpty() ? Double.valueOf(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;  // 如果解析失败，返回默认值
        }
    }

    // 安全地解析 Integer 类型，防止解析错误
    private Integer parseInteger(String value) {
        try {
            return value != null && !value.isEmpty() ? Integer.valueOf(value) : 0;
        } catch (NumberFormatException e) {
            return 0;  // 如果解析失败，返回默认值
        }
    }
}
