package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.dto.DishDto;
import com.ysl.reigi.mapper.CategoryMapper;
import com.ysl.reigi.mapper.DishMapper;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Dish;
import com.ysl.reigi.pojo.DishFlavor;
import com.ysl.reigi.service.CategoryService;
import com.ysl.reigi.service.DishFlavorService;
import com.ysl.reigi.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * @description: 新增菜品 同时保存口味数据
     * @author: YSL
     * @time: 2023/2/9 20:24
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品信息到 菜品表
        this.save(dishDto);
//        保存后 获取菜品ID
        Long dishId = dishDto.getId();
//        菜品口味  前端的数据 只有name value 没有菜品ID 所以要设置菜品ID 然后对应口味
        List<DishFlavor> flavors = dishDto.getFlavors();
//        使用stream map遍历口味 并给ID赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

//        保存菜品口味数据 到口味表 dish_flavor
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * @description: 根据id查询 菜品表和口味表
     * @author: YSL
     * @time: 2023/2/9 20:24
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        查询菜品信息表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
//        把查出的数据 拷贝给dishDto （设置里面的值）
        BeanUtils.copyProperties(dish, dishDto);

//        查询当前菜品的口味信息 dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    public void updateByIdWithFlavor(DishDto dishDto) {
//        更新dish表
        this.updateById(dishDto);
//       清理当前菜品对应的口味数据---dish_flavor的delete的操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        添加当前提交的口味数据---inset操作
        //        使用stream map遍历口味 并给ID赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public void deleteByIdWithFlavor(Long ids) {
//        根据ID删除dish表
        this.removeById(ids);
//       根据ID 删除当前菜品对应的口味表数据---dish_flavor的delete的操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
    }




}
