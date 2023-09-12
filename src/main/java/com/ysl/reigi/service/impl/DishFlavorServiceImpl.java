package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.pojo.DishFlavor;
import com.ysl.reigi.mapper.DishFlavorMapper;
import com.ysl.reigi.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
