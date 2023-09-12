package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.mapper.OrderDetailMapper;
import com.ysl.reigi.pojo.OrderDetail;
import com.ysl.reigi.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 17:28
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService{
}
