package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.common.CustomException;
import com.ysl.reigi.mapper.CategoryMapper;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Dish;
import com.ysl.reigi.pojo.Setmeal;
import com.ysl.reigi.service.CategoryService;
import com.ysl.reigi.service.DishService;
import com.ysl.reigi.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/9 20:24
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * @description: 根据ID删除分类 删除之前需要进行判断
     * @Parme:  categoryId
     * @time: 2023/2/9 20:24
     */
    @Override
    public void remove(Long id) {
//        1.查询当前分类是否关联菜品，如果已经关联，抛出业务异常 使用Dish 中 categoryId
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
//        添加查询条件 根据 categoryId
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1=dishService.count(dishLambdaQueryWrapper);
        if (count1>0){
//            有数据 关联了菜品  抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

//        2.查询当前分类是否关联套餐，如果已经关联，抛出业务异常 Setmeal 中 categoryId
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
//        添加查询条件 根据 categoryId
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2=setmealService.count(setmealLambdaQueryWrapper);

        if (count2>0){
//            有数据 关联了套餐  抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
//        正常删除
        super.removeById(id);
    }
}
