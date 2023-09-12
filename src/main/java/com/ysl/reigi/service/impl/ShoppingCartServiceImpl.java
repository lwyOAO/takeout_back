package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.mapper.ShoppingCartMapper;
import com.ysl.reigi.pojo.ShoppingCart;
import com.ysl.reigi.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 14:22
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
