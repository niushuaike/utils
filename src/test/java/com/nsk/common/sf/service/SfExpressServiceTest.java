package com.nsk.common.sf.service;

import com.nsk.common.sf.bean.DeliverConsigneeInfo;
import com.nsk.common.sf.bean.OrderRequest;
import org.junit.Test;

import java.util.UUID;

/**
 * @author Niusk
 * @create 2018/11/8
 */
public class SfExpressServiceTest {

    public static void main(String[] args) {
        new SfExpressServiceTest().testOrder();
    }


    @Test
    public void testOrder() {
        //Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(UUID.randomUUID().toString().replace("-", ""));
//        orderRequest.setOrderId("9b5061d7dd7745058bd19db078f9a889");
        orderRequest.setExpressType(1);
        orderRequest.setPayMethod(1);
        orderRequest.setCustId("7551234567");

        DeliverConsigneeInfo deliverInfo = new DeliverConsigneeInfo();
        deliverInfo.setCompany("黄金宝");
        deliverInfo.setContact("金有金");
        deliverInfo.setTel("0755-33123457");
        deliverInfo.setMobile("13200002222");
        deliverInfo.setProvince("浙江省");
        deliverInfo.setCity("杭州市");
        deliverInfo.setCounty("上城区");
        deliverInfo.setAddress("上城区泰合广场");
        orderRequest.setDeliverInfo(deliverInfo);

        DeliverConsigneeInfo consigneeInfo = new DeliverConsigneeInfo();
        consigneeInfo.setCompany("知音科技");
        consigneeInfo.setContact("刘总");
        consigneeInfo.setTel("0755-33123456");
        consigneeInfo.setMobile("13100002222");
        consigneeInfo.setProvince("上海");
        consigneeInfo.setCity("上海市");
        consigneeInfo.setCounty("浦东新区");
        consigneeInfo.setAddress("北蔡镇上海科技大学");
        orderRequest.setConsigneeInfo(consigneeInfo);
        //Act
        new SfExpressService().order(orderRequest);
        //Assert
    }

}
