package com.github.binarywang.demo.spring.service;

import com.github.binarywang.demo.spring.beans.Gps;
import com.github.binarywang.demo.spring.dao.GpsRecordDao;
import com.github.binarywang.demo.spring.dto.Temperature;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;


/**
 * @author huyong
 * @since 2018/1/9
 */
@Service
public class MqttClientService {
    public static final String HOST = "tcp://pi-home.cn:61613";
    public static final String TOPIC = "temperature";
    public static final String TOPIC_2 = "light";
    public static final String TOPIC_GPS = "GPS_MSG";
    public static final String TOPIC_GPS_RSP = "GPS_MSG_RSP";
    private static final String clientid = "wechat";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "admin";
    private String passWord = "password";

    private ScheduledExecutorService scheduler;
    public boolean wait = false;
    public String response;

    @Autowired
    GpsRecordDao recordDao;

    @Autowired
    WeixinService weixinService;
    public MqttClientService() {
//        start();
    }

    public void start() {
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String msgContent = new String(mqttMessage.getPayload());
//                    System.out.println("收到的消息：" + msgContent);
                    if (s == null) return;
                    response = msgContent;
                    if (s.equals(TOPIC)) {
                        Temperature.tempValue = msgContent;
                    } else if (s.equals(TOPIC_2)) {
                        Temperature.lightValue = msgContent;
                    } else if (s.equals(TOPIC_GPS_RSP)) {
                        //收到消息处理逻辑
                    } else if (s.equals(TOPIC_GPS)) {
                        Temperature.gpsValue = msgContent;
                        String[] strs = msgContent.split(",");
                        System.out.println("当前位置：" + msgContent);
//                        Gps gps = (Gps) JSON.parse(msgContent);
//                        Gps gps = JSON.parseObject(msgContent, new TypeReference<Gps>() {
//                        });
//                        System.out.println("当前位置：lat:" + gps.getLat() + " lon:" + gps.getLon());
                        Gps gps = new Gps();
                        gps.setLon(strs[0]);
                        gps.setLat(strs[1]);
                        if (strs.length ==3){
                            gps.setStartFlag(1);
                        }else {
                            gps.setStartFlag(0);
                        }
                        recordDao.insert(gps);
                    }

//                    String content = "当前温度：" + Temperature.tempValue + "度";
//                    System.out.println(content);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttTopic topic = client.getTopic(TOPIC);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "close".getBytes(), 2, true);

            client.connect(options);
            //订阅消息
            int[] Qos = {1, 1, 1};
//            String[] topic1 = {TOPIC, TOPIC_2};
//            client.subscribe(topic1, Qos);
            //订阅消息
            String[] topicGPS = {TOPIC, TOPIC_GPS, TOPIC_GPS_RSP};
            client.subscribe(topicGPS, Qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String msg) {
        wait = true;
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        mqttMessage.setRetained(false);
        mqttMessage.setQos(1);
        try {
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException {
        String v = "10.75";
        MqttClientService server = new MqttClientService();
        server.start();
        System.out.println(Float.parseFloat(v));
    }
}
