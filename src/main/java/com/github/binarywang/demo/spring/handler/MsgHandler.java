package com.github.binarywang.demo.spring.handler;

import com.github.binarywang.demo.spring.builder.TextBuilder;
import com.github.binarywang.demo.spring.dto.Temperature;
import com.github.binarywang.demo.spring.service.WeixinService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.PostConstruct;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Binary Wang
 */
@Component
public class MsgHandler extends AbstractHandler {

//    @Autowired
//    MqttClientService service;

//    @Autowired
//    GpsRecordDao recordDao;

    @PostConstruct
    public void afterInit() {
//        service.start();
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        WeixinService weixinService = (WeixinService) wxMpService;

        if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                && weixinService.hasKefuOnline()) {
            return WxMpXmlOutMessage
                    .TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
        } else if (StringUtils.equals(wxMessage.getContent(), "温度")) {
            String content = "当前温度：" + Temperature.tempValue + "度";
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "光照")) {
            String content = "当前光照：" + Temperature.lightValue;
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "位置")) {
            String content = "当前位置：" + Temperature.gpsValue;
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "红色")) {
//            service.publish("color", "r");
            String content = "红色";
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "绿色")) {
//            service.publish("color", "g");
            String content = "绿色";
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "蓝色")) {
//            service.publish("color", "b");
            String content = "蓝色";
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else if (StringUtils.equals(wxMessage.getContent(), "开关")) {
//            service.publish("GPS_SUB", "GPS_SUB");
            String content = "GPS_SUB";
            return new TextBuilder().build(content, wxMessage, weixinService);
        } else {
            String content = "请输入[温度]获取当前温度";
            return new TextBuilder().build(content, wxMessage, weixinService);
        }

        //TODO 组装回复消息
//    String content = "回复信息内容";


    }

}
