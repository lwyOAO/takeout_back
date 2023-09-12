package com.ysl.reigi.dto;

import com.ysl.reigi.pojo.DishFlavor;
import com.ysl.reigi.pojo.OrderDetail;
import com.ysl.reigi.pojo.Orders;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/28 10:33
 */
@Data
public class OrderDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    //订单号
    private String number;

    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;


    //下单用户id
    private Long userId;

    //地址id
    private Long addressBookId;


    //下单时间

    private LocalDateTime orderTime;


    //结账时间
    private LocalDateTime checkoutTime;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime beginTime;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime endTime;

    //支付方式 1微信，2支付宝
    private Integer payMethod;


    //实收金额
    private BigDecimal amount;

    //备注
    private String remark;

    //用户名
    private String userName;

    //手机号
    private String phone;

    //地址
    private String address;

    //收货人
    private String consignee;

    private List<OrderDetail> orderDetails = new ArrayList<>();
}
