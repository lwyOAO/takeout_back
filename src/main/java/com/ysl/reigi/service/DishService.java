package com.ysl.reigi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ysl.reigi.dto.DishDto;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Dish;

public interface DishService extends IService<Dish> {

    //    添加新方法
//    新增菜品，同时插入菜品对应的口味数据  操作两张表 dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //   修改时 根据id回显数据 查询两张表 dish、dish_flavor
    DishDto getByIdWithFlavor(Long id);

    //修改操作 操作两张表 dish、dish_flavor
    void updateByIdWithFlavor(DishDto dishDto);

    //删除操作 操作两张表 dish、dish_flavor
    void deleteByIdWithFlavor(Long ids);

}
