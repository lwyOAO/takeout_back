package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ysl.reigi.common.BaseContext;
import com.ysl.reigi.common.R;
import com.ysl.reigi.pojo.Dish;
import com.ysl.reigi.pojo.Orders;
import com.ysl.reigi.service.OrderDetailService;
import com.ysl.reigi.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/24 16:31
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    //   后台 订单信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        log.info("{}", number);
        log.info("{}{}", beginTime, endTime);
        Page pageInfo = new Page(page, pageSize);


        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
//        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
//        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        factory.setFormatterRegistrars();
//
        if (beginTime != null || endTime != null) {
            LocalDateTime dateTime1 = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime dateTime2 = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("{}{}", dateTime1, dateTime2);
            queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
            queryWrapper.ge(Orders::getOrderTime, dateTime1).le(Orders::getCheckoutTime, dateTime2);
            queryWrapper.orderByDesc(Orders::getOrderTime);
            //        orderByDesc 以创建时间分类
            ordersService.page(pageInfo, queryWrapper);
        } else {
            queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
            ordersService.page(pageInfo, queryWrapper);
            //        orderByDesc 以创建时间分类
            queryWrapper.orderByDesc(Orders::getOrderTime);
            ordersService.page(pageInfo, queryWrapper);
        }
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("派送成功");
    }

    @GetMapping("/userPage")
    public R<Page> list(int page, int pageSize){
        Page page1 = ordersService.listPage(page, pageSize);
        return R.success(page1);
    }
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }


}
