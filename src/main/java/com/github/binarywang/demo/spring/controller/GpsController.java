package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.beans.Gps;
import com.github.binarywang.demo.spring.dao.GpsRecordDao;
import com.github.binarywang.demo.spring.json.RequestDate;
import com.github.binarywang.demo.spring.utils.GpsTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huyong
 * @since 2018/2/16
 */
@RestController
@RequestMapping("/gps")
public class GpsController {

    @Autowired
    GpsRecordDao gpsRecordDao;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/create")
    public String testCreate() {
        Gps gps = new Gps();
        gps.setLon("1000");
        gps.setLat("200");
        gpsRecordDao.insert(gps);
        return "success";
    }

    @RequestMapping("/getGpsList")
    public List<Gps> getGpsList(@RequestBody RequestDate requestDate) {
        logger.debug("startDate:" + requestDate.getStartDate());
        logger.debug("endDate:" + requestDate.getEndDate());
        List<Gps> orginalGps = gpsRecordDao.getByDate(requestDate.getStartDate(), requestDate.getEndDate());
//        return null;
        List<Gps> resultList = new ArrayList<>();
        for (Gps gps : orginalGps) {
            Gps newGps = new Gps();
            double[] gpss = GpsTransfer.wgs2gcj(Double.parseDouble(gps.getLon()), Double.parseDouble(gps.getLat()));
            newGps.setLat(String.valueOf(gpss[0]));
            newGps.setLon(String.valueOf(gpss[1]));
            resultList.add(newGps);
        }
        logger.debug("resultList size:" + resultList.size());
        return resultList;

    }

    @RequestMapping("/getCurrentGps")
    public Gps getCurrentGps() {
        Gps orginalGps = gpsRecordDao.getCurrentGps();
        Gps newGps = new Gps();
        double[] gpss = GpsTransfer.wgs2gcj(Double.parseDouble(orginalGps.getLon()), Double.parseDouble(orginalGps.getLat()));
        newGps.setLat(String.valueOf(gpss[0]));
        newGps.setLon(String.valueOf(gpss[1]));
        logger.debug("current gps:" + newGps.getLat() + " " + newGps.getLon());
        return newGps;

    }
}
