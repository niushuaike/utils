package com.nsk.common.sf.bean;

/**
 * @author Niusk
 * @create 2018/11/8
 */
public class OrderResponse {

    /**
     * 客户订单号
     */
    private String orderid;

    /**
     * 顺丰运单号
     */
    private String mailno;

    /**
     * 顺丰签回单服务运单号
     */
    private String retrunTrackingNo;

    /**
     * 原寄地区域代码
     */
    private String origincode;

    /**
     * 目的地区域代码
     */
    private String destcode;

    /**
     * 筛单结果：
     * <p>
     * 1：人工确认
     * 2：可收派
     * 3：不可以收派
     */
    private String filterresult;

    /**
     * 如果filter_result=3时为必填，不可以收派的原因代码：
     * <p>
     * 1：收方超范围
     * 2：派方超范围
     * 3-：其它原因
     */
    private String remark;

    /**
     * 代理单号
     */
    private String agentMailno;

    /**
     * 地址映射码
     */
    private String mappingMark;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    public String getRetrunTrackingNo() {
        return retrunTrackingNo;
    }

    public void setRetrunTrackingNo(String retrunTrackingNo) {
        this.retrunTrackingNo = retrunTrackingNo;
    }

    public String getOrigincode() {
        return origincode;
    }

    public void setOrigincode(String origincode) {
        this.origincode = origincode;
    }

    public String getDestcode() {
        return destcode;
    }

    public void setDestcode(String destcode) {
        this.destcode = destcode;
    }

    public String getFilterresult() {
        return filterresult;
    }

    public void setFilterresult(String filterresult) {
        this.filterresult = filterresult;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAgentMailno() {
        return agentMailno;
    }

    public void setAgentMailno(String agentMailno) {
        this.agentMailno = agentMailno;
    }

    public String getMappingMark() {
        return mappingMark;
    }

    public void setMappingMark(String mappingMark) {
        this.mappingMark = mappingMark;
    }
}
