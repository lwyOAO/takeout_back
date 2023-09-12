package com.ysl.reigi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.ysl.reigi.common.R;
import com.ysl.reigi.dto.OrderDto;
import com.ysl.reigi.pojo.Orders;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    Page listPage(int page, int pageSize);
}
