package com.nsk.common.sf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsk.common.sf.bean.DeliverConsigneeInfo;
import com.nsk.common.sf.bean.OrderRequest;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.dto.CargoInfoDto;
import com.sf.dto.WaybillDto;
import com.sf.util.Base64ImageTools;
import com.sf.util.MyJsonUtil;
import org.dom4j.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 不考虑字母单类型（物流拆单）
 *
 * @author Niusk
 * @create 2018/11/8
 */
public class SfExpressService {

    private static final String CLIENT_CODE = "HJBTZ";
    private static final String EXPRESS_CHECK_CODE = "7Qh2iQiYTtOqEttO8NNHsOTiDg1j7PGS";
    private static final String PRINT_CHECK_CODE = "5A9F833897A1A394FCDCD4B32A6167F9";
    private static final String EXPRESS_URL = "http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService";

    /*********3联210 丰密运单**************/
    /**
     * 调用打印机 不弹出窗口 适用于批量打印【三联单】
     */
    String url10 = "http://localhost:4040/sf/waybill/print?type=V3.0.FM_poster_100mm210mm&output=noAlertPrint";
    /**
     * 调用打印机 弹出窗口 可选择份数 适用于单张打印【三联单】
     */
    String url11 = "http://localhost:4040/sf/waybill/print?type=V3.0.FM_poster_100mm210mm&output=print";

    /**
     * 直接输出图片的BASE64编码字符串 可以使用html标签直接转换成图片【三联单】
     */
    private static final String PRINT_URL = "http://localhost:4040/sf/waybill/print?type=V3.0.FM_poster_100mm210mm&output=image";

    /**
     * 物流下单，下单之前首先查询订单，若不存在，在进行下单操作
     *
     * @param orderRequest 下单对象封装
     */
    public void order(OrderRequest orderRequest) {
        //下单之前先查询订单是否已经物流下单
        String respQueryXml = CallExpressServiceTools.callSfExpressServiceByCSIM(EXPRESS_URL, getOrderSearchServiceRequestXml(CLIENT_CODE, orderRequest.getOrderId()), CLIENT_CODE, EXPRESS_CHECK_CODE);
        if (respQueryXml != null) {
            System.out.println("--------------------------------------");
            System.out.println("返回查询报文: " + respQueryXml);
            System.out.println("--------------------------------------");

            Document queryDocument = null;
            try {
                queryDocument = DocumentHelper.parseText(respQueryXml);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            Element queryRoot = queryDocument.getRootElement();
            Element queryHead = queryRoot.element("Head");
            if ("OK".equalsIgnoreCase(queryHead.getStringValue())) {
                Element orderResponse = queryRoot.element("Body").element("OrderResponse");
                List<Attribute> attributes = orderResponse.attributes();

                //todo 订单已物流下单，在此进行运单打印
                for (int i = 0; i < attributes.size(); i++) {
                    Attribute attribute = attributes.get(i);
                    System.out.println(attribute.getName() + "  :  "
                            + attribute.getValue());
                }
            } else {
                Element error = queryRoot.element("ERROR");
                Attribute code = error.attribute("code");
                //订单未找到进行下单操作
                if ("6150".equals(code.getValue())) {
                    String respXml = CallExpressServiceTools.callSfExpressServiceByCSIM(EXPRESS_URL, getOrderServiceRequestXml(orderRequest), CLIENT_CODE, EXPRESS_CHECK_CODE);
                    if (respXml != null) {
                        System.out.println("--------------------------------------");
                        System.out.println("返回下单报文: " + respXml);
                        System.out.println("--------------------------------------");

                        Document document = null;
                        try {
                            document = DocumentHelper.parseText(respXml);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                        Element root = document.getRootElement();
                        Element head = root.element("Head");
                        if ("OK".equalsIgnoreCase(head.getStringValue())) {
                            Element orderResponse = root.element("Body").element("OrderResponse");
                            Element rlsInfo = orderResponse.element("rls_info");
                            if ("OK".equals(rlsInfo.attribute("invoke_result").getValue())) {
                                Element rlsDetail = rlsInfo.element("rls_detail");
                                List<Attribute> attributes = rlsDetail.attributes();
                                Map<String, String> rlsDetailParams = new HashMap<>(30);
                                for (int i = 0; i < attributes.size(); i++) {
                                    Attribute attribute = attributes.get(i);
                                    System.out.println(attribute.getName() + "  :  " + attribute.getValue());
                                    rlsDetailParams.put(attribute.getName(), attribute.getValue());
                                }
                                try {
                                    System.out.println("开始打印！");
                                    wayBillPrinter(url11, CLIENT_CODE, PRINT_CHECK_CODE, orderRequest, rlsDetailParams);
                                    System.out.println("打印完毕！");
                                } catch (IOException e) {
                                    System.out.println("打印失败！");
                                }
                            }

                        } else {
                            System.out.println("下单失败！");
                        }
                    }
                }
                System.out.println(error.getText());
            }
        }
    }

    /**
     * 不考虑签单返还业务
     *
     * @param reqUrl          运单打印请求接口
     * @param clientCode      客户编号
     * @param checkWord       校验码
     * @param orderRequest    下单信息
     * @param rlsDetailParams 面单信息
     * @throws IOException
     */
    public void wayBillPrinter(String reqUrl, String clientCode, String checkWord, OrderRequest orderRequest, Map<String, String> rlsDetailParams) throws IOException {

        /**注意 需要使用对应业务场景的url  **/

        //电子面单顶部是否需要logo
        //true 需要logo  false 不需要logo
        boolean topLogo = true;
        if (reqUrl.contains("V2.0") && topLogo) {
            reqUrl = reqUrl.replace("V2.0", "V2.1");
        }

        if (reqUrl.contains("V3.0") && topLogo) {
            reqUrl = reqUrl.replace("V3.0", "V3.1");
        }

        System.out.println(reqUrl);

        URL myURL = new URL(reqUrl);

        //其中127.0.0.1:4040为打印服务部署的地址（端口如未指定，默认为4040），
        //type为模板类型（支持两联、三联，尺寸为100mm*150mm和100mm*210mm，type为poster_100mm150mm和poster_100mm210mm）
        //A5 poster_100mm150mm   A5 poster_100mm210mm
        //output为输出类型,值为print或image，如不传，
        //默认为print（print 表示直接打印，image表示获取图片的BASE64编码字符串）
        //V2.0/V3.0模板顶部是带logo的  V2.1/V3.1顶部不带logo

        HttpURLConnection httpConn = (HttpURLConnection) myURL.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "text/plain;charset=utf-8");

        httpConn.setConnectTimeout(5000);
        httpConn.setReadTimeout(2 * 5000);

        List<WaybillDto> waybillDtoList = new ArrayList<WaybillDto>();
        WaybillDto dto = new WaybillDto();

        //这个必填
        //对应clientCode
        dto.setAppId(clientCode);
        //对应checkWord
        dto.setAppKey(checkWord);

        dto.setMailNo(rlsDetailParams.get("waybillNo"));

        //收件人信息
        DeliverConsigneeInfo consigneeInfo = orderRequest.getConsigneeInfo();

        dto.setConsignerProvince(consigneeInfo.getProvince());
        dto.setConsignerCity(consigneeInfo.getCity());
        dto.setConsignerCounty(consigneeInfo.getCounty());
        dto.setConsignerAddress(consigneeInfo.getAddress());
        dto.setConsignerCompany(consigneeInfo.getCompany());
        dto.setConsignerMobile(consigneeInfo.getMobile());
        dto.setConsignerName(consigneeInfo.getContact());
        dto.setConsignerShipperCode(consigneeInfo.getShipperCode());
        dto.setConsignerTel(consigneeInfo.getTel());


        //寄件人信息
        DeliverConsigneeInfo deliverInfo = orderRequest.getDeliverInfo();

        dto.setDeliverProvince(deliverInfo.getProvince());
        dto.setDeliverCity(deliverInfo.getCity());
        dto.setDeliverCounty(deliverInfo.getCounty());
        dto.setDeliverCompany(deliverInfo.getCompany());
        dto.setDeliverAddress(deliverInfo.getAddress());
        dto.setDeliverName(deliverInfo.getContact());
        dto.setDeliverMobile(deliverInfo.getMobile());
        dto.setDeliverShipperCode(deliverInfo.getShipperCode());
        dto.setDeliverTel(deliverInfo.getTel());

        //目的地代码 参考顺丰地区编号
        dto.setDestCode(rlsDetailParams.get("destCityCode"));
        //原寄地代码 参考顺丰地区编号
        dto.setZipCode(rlsDetailParams.get("sourceCityCode"));

        // 陆运E标示, ***丰密运单该字段无效
        //dto.setElectric("E");

        //快递类型.现没有返回对应字段，写死为1类型
        //1 ：标准快递   2.顺丰特惠   3： 电商特惠   5：顺丰次晨  6：顺丰即日  7.电商速配   15：生鲜速配
        dto.setExpressType(1);

        //月结卡号
        //dto.setInsureValue("501");//声明货物价值的保价金额,只需填金额,单位元
        dto.setMonthAccount(orderRequest.getCustId());
        dto.setPayMethod(orderRequest.getPayMethod());


        /**丰密运单相关-如非使用丰密运单模板 不需要设置以下值*****************************************************************************************/

        //时效类型
        dto.setProCode(rlsDetailParams.get("proCode"));

        //目的地，不可为空，若无则打印目的地城市代码
        dto.setDestRouteLabel(rlsDetailParams.get("destRouteLabel"));

        //进港信息
        dto.setCodingMapping(rlsDetailParams.get("codingMapping"));

        //出港信息
        dto.setCodingMappingOut(rlsDetailParams.get("codingMappingOut"));

        //原寄地中转场
        dto.setSourceTransferCode(rlsDetailParams.get("sourceTransferCode"));

        //单元区域编码，就是地址处的一个水印
        dto.setDestTeamCode(rlsDetailParams.get("destTeamCode"));

        //对应下订单设置路由标签返回字段twoDimensionCode 该参数是丰密面单的二维码图
        //k1:目的地中转场代码,k2:目的地网点代码（当无法获取时，直接填入目的地城市代码）,k3:单元区域,k4: 时效类型（纯字母代码，T1/T4/T6/T8）,k5:运单号,k6:A/B/空
        dto.setQRCode(rlsDetailParams.get("twoDimensionCode"));

        dto.setAbFlag(rlsDetailParams.get("abFlag"));

        //类型图标，固定
        dto.setPrintIcon(rlsDetailParams.get("printIcon"));
        dto.setXbFlag(rlsDetailParams.get("xbFlag"));

        //客户个性化Logo 必须是个可以访问的图片本地路径地址或者互联网图片地址
        dto.setCustLogo("C:\\Users\\Administrator\\Desktop\\bug图片\\sf-jyj.png");


        //加密项
        //加密寄件人及收件人名称
        dto.setEncryptCustName(false);
        //加密寄件人及收件人联系手机
        dto.setEncryptMobile(false);


        CargoInfoDto cargo = new CargoInfoDto();
        cargo.setCargo("饰品");
        cargo.setCargoCount(1);
        cargo.setCargoUnit("件");
        cargo.setSku("");
        cargo.setRemark("");


        List<CargoInfoDto> cargoInfoList = new ArrayList<CargoInfoDto>();
        cargoInfoList.add(cargo);

        dto.setCargoInfoDtoList(cargoInfoList);
        waybillDtoList.add(dto);

        System.out.println("请求参数： " + MyJsonUtil.object2json(dto));

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, waybillDtoList);

        httpConn.getOutputStream().write(stringWriter.toString().getBytes());
        httpConn.getOutputStream().flush();
        httpConn.getOutputStream().close();
        InputStream in = httpConn.getInputStream();

        BufferedReader in2 = new BufferedReader(new InputStreamReader(in));

        String y = "";


        String strImg = "";
        while ((y = in2.readLine()) != null) {

            strImg = y.substring(y.indexOf("[") + 1, y.length() - "]".length() - 1);
            if (strImg.startsWith("\"")) {
                strImg = strImg.substring(1, strImg.length());
            }
            if (strImg.endsWith("\"")) {
                strImg = strImg.substring(0, strImg.length() - 1);
            }

        }


        //将换行全部替换成空
        strImg = strImg.replace("\\n", "");

        System.out.println(strImg);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String dateStr = format.format(new Date());

        if (strImg.contains("\",\"")) {
            //如子母单及签回单需要打印两份或者以上
            String[] arr = strImg.split("\",\"");

            /**输出图片到本地 支持.jpg、.png格式**/
            for (int i = 0; i < arr.length; i++) {
                Base64ImageTools.generateImage(arr[i].toString(), "D:\\qiaoWay" + dateStr + "-" + i + ".jpg");

            }
        } else {
            Base64ImageTools.generateImage(strImg, "D:\\qiaoWaybill" + dateStr + ".jpg");

        }
    }

    /**
     * 获取顺丰订单结果查询接口xml
     */
    private String getOrderSearchServiceRequestXml(String clientCode, String orderid) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<Request service='OrderSearchService' lang='zh-CN'>");
        strBuilder.append("<Head>" + clientCode + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<OrderSearch").append(" ");
        strBuilder.append("orderid='" + orderid + "" + "'").append(" > ");
        strBuilder.append("</OrderSearch>");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");
        return strBuilder.toString();
    }

    /**
     * 获取顺丰下订单接口xml
     */
    private String getOrderServiceRequestXml(OrderRequest orderRequest) {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<Request service='OrderService' lang='zh-CN'>");
        strBuilder.append("<Head>" + CLIENT_CODE + "</Head>");
        strBuilder.append("<Body>");
        strBuilder.append("<Order").append(" ");
        strBuilder.append("orderid='" + orderRequest.getOrderId() + "'").append(" ");

        //返回顺丰运单号
        strBuilder.append("is_gen_bill_no='1'").append(" ");

        //快件产品类别，1：顺丰标快
        strBuilder.append("express_type='1'").append(" ");

        //寄件方信息
        DeliverConsigneeInfo deliverInfo = orderRequest.getDeliverInfo();
        strBuilder.append("j_company='" + deliverInfo.getCompany() + "'").append(" ");
        strBuilder.append("j_contact='" + deliverInfo.getContact() + "'").append(" ");
        strBuilder.append("j_tel='" + deliverInfo.getTel() + "'").append(" ");
        strBuilder.append("j_address='" + deliverInfo.getProvince() + deliverInfo.getCity() + deliverInfo.getCounty() + deliverInfo.getAddress() + "'").append(" ");
        //收件方信息
        DeliverConsigneeInfo consigneeInfo = orderRequest.getConsigneeInfo();
        strBuilder.append("d_company='" + consigneeInfo.getCompany() + "'").append(" ");
        strBuilder.append("d_contact='" + consigneeInfo.getContact() + "'").append(" ");
        strBuilder.append("d_tel='" + consigneeInfo.getTel() + "'").append(" ");
        strBuilder.append("d_address='" + consigneeInfo.getProvince() + consigneeInfo.getCity() + consigneeInfo.getCounty() + consigneeInfo.getAddress() + "'").append(" ");

        strBuilder.append(" > ");

        //货物
        strBuilder.append("<Cargo").append(" ");
        strBuilder.append("name='饰品'").append(" ");
        strBuilder.append("count='1'").append(" ");
        strBuilder.append("unit='件'").append(" ");
        strBuilder.append(" > ");
        strBuilder.append("</Cargo>");

        strBuilder.append("</Order>");
        strBuilder.append("</Body>");
        strBuilder.append("</Request>");

        return strBuilder.toString();
    }

}
