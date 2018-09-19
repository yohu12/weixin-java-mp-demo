package com.github.binarywang.demo.spring.json;

import java.util.Date;

/**
 * @author huyong
 * @since 2018/2/16
 */
public class RequestDate {
    private Date startDate;
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
