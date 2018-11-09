package com.nsk.common.sf.bean;

import java.math.BigDecimal;

/**
 * @author Niusk
 * @create 2018/11/8
 */
public class CargoInfo {

    private String orderId;
    private Integer cargoIndex;
    private String cargo;
    private String cargoUnit;
    private String cargoCount;
    private String cargoWeight;
    private String cargoAmount;
    private Integer parcelQuantity = 1;
    private BigDecimal cargoTotalWeight;
}
