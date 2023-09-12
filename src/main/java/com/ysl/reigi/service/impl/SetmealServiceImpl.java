package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.common.R;
import com.ysl.reigi.dto.SetmealDto;
import com.ysl.reigi.mapper.CategoryMapper;
import com.ysl.reigi.mapper.SetmealMapper;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Dish;
import com.ysl.reigi.pojo.Setmeal;
import com.ysl.reigi.pojo.SetmealDish;
import com.ysl.reigi.service.CategoryService;
import com.ysl.reigi.service.DishService;
import com.ysl.reigi.service.SetmealDishService;
import com.ysl.reigi.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/9 20:24
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * @description: 新增套餐 保存套餐和菜品的关联关系
     * @author: YSL   setmealDto
     * @time: 2023/2/20 9:52
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐基本信息setmeal inset操作
        this.save(setmealDto);
//        获取setmeal_dish 集合
        List<SetmealDish> list=setmealDto.getSetmealDishes();
        list.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
//        保存套餐和菜品的关联信息 setmeal_dish inset操作
        setmealDishService.saveBatch(list);
    }
    /**
     * @description: 修改操作 数据回显
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //        查询套餐信息
        Setmeal setmeal=this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
//        查询菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }
    /**
     * @description: 修改操作  多表操作
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    @Override
    public void updateByIdWithDish(SetmealDto setmealDto) {
//        更新setmeal表
        this.updateById(setmealDto);
//        删除原来的 setmeal_dish 表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //        添加当前提交的菜品数据---inset操作
        //        使用stream map遍历口味 并给ID赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * @description: 删除操作 多表修改
     * @author: YSL setmealDto
     * @time: 2023/2/20 9:52
     */
    @Override
    public void deleteByIdWithDish(Long ids) {
//        先删除setmeal表的数据
        this.removeById(ids);
//        通过id删除 setmeal_dish 表数据 delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
    }
}
