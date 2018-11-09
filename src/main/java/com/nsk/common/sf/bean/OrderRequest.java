package com.nsk.common.sf.bean;

import java.util.List;

/**
 * @author Niusk
 * @create 2018/11/8
 */
public class OrderRequest {

    /**
     * 客户订单号
     */
    private String orderId;

    /**
     * 顺丰月结卡号
     */
    private String custId;

    /**
     * 付款方式：1，寄方付；2，收方付；3，第三方付
     */
    private Integer payMethod;

    /**
     * 快递产品编码：1 ：标准快递   2.顺丰特惠   3： 电商特惠   5：顺丰次晨  6：顺丰即日  7.电商速配   15：生鲜速配 等
     */
    private Integer expressType;

    /**
     * 是否需要通过手持终端通知顺丰收件员收件：1，要求；其他为不要求
     */
    private Integer isDoCall = 0;

    /**
     * 是否要求返回顺丰运单号：1，要求；其他为不要求
     */
    private Integer isGenBillNo = 0;

    /**
     * 包裹数，一个包裹对应一个运单号
     */
    private Integer parcelQuantity = 1;

    /**
     * 报关批次
     */
    private String customsBatchs;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发件人信息
     */
    private DeliverConsigneeInfo deliverInfo;

    /**
     * 收件人信息
     */
    private DeliverConsigneeInfo consigneeInfo;

    /**
     * 货物
     */
    private CargoInfo cargoInfo;

    /**
     * 增值服务
     */
    private List<AddedServiceInfo> addedServices;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public Integer getExpressType() {
        return expressType;
    }

    public void setExpressType(Integer expressType) {
        this.expressType = expressType;
    }

    public Integer getIsDoCall() {
        return isDoCall;
    }

    public void setIsDoCall(Integer isDoCall) {
        this.isDoCall = isDoCall;
    }

    public Integer getIsGenBillNo() {
        return isGenBillNo;
    }

    public void setIsGenBillNo(Integer isGenBillNo) {
        this.isGenBillNo = isGenBillNo;
    }

    public Integer getParcelQuantity() {
        return parcelQuantity;
    }

    public void setParcelQuantity(Integer parcelQuantity) {
        this.parcelQuantity = parcelQuantity;
    }

    public String getCustomsBatchs() {
        return customsBatchs;
    }

    public void setCustomsBatchs(String customsBatchs) {
        this.customsBatchs = customsBatchs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public DeliverConsigneeInfo getDeliverInfo() {
        return deliverInfo;
    }

    public void setDeliverInfo(DeliverConsigneeInfo deliverInfo) {
        this.deliverInfo = deliverInfo;
    }

    public DeliverConsigneeInfo getConsigneeInfo() {
        return consigneeInfo;
    }

    public void setConsigneeInfo(DeliverConsigneeInfo consigneeInfo) {
        this.consigneeInfo = consigneeInfo;
    }

    public CargoInfo getCargoInfo() {
        return cargoInfo;
    }

    public void setCargoInfo(CargoInfo cargoInfo) {
        this.cargoInfo = cargoInfo;
    }

    public List<AddedServiceInfo> getAddedServices() {
        return addedServices;
    }

    public void setAddedServices(List<AddedServiceInfo> addedServices) {
        this.addedServices = addedServices;
    }
}
