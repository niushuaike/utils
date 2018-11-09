package com.nsk.common.sf.bean;

/**
 * @author Niusk
 * @create 2018/11/8
 */
public class DeliverConsigneeInfo {

    /**
     * 公司
     */
    private String company;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话（必填）
     */
    private String tel;

    /**
     * 手机（非必填）
     */
    private String mobile;

    /**
     * 国家
     */
    private String country = "中国";

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县/区
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 国家城市代码（若跨境，则必填）
     */
    private String shipperCode;

    /**
     * 邮编（若跨境，则必填）
     */
    private String postCode;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
