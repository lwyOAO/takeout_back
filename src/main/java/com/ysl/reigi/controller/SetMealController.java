package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ysl.reigi.common.R;
import com.ysl.reigi.dto.DishDto;
import com.ysl.reigi.dto.SetmealDto;
import com.ysl.reigi.pojo.*;
import com.ysl.reigi.service.CategoryService;
import com.ysl.reigi.service.SetmealDishService;
import com.ysl.reigi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 套餐管理
 * @author: YSL
 * @time: 2023/2/20 9:52
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    //    套餐信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * @description: 新增 setmealDto
     * @author: YSL
     * @time: 2023/2/20 9:52
     */
    @PostMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * @description: 修改操作 数据回显
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getByIdWithDish(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * @description: 修改操作  多表操作
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    @PutMapping
//    进行操作时，删除 spring Cache 缓存到redis 的 setMealCache 里所有的key
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> updateByIdWithDish(@RequestBody SetmealDto setmealDto) {
//        修改套餐
        long id = Thread.currentThread().getId();
        log.info("id{}", id);
        setmealDto.setUpdateUser(id);
        setmealService.updateByIdWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * @description: 根据id 删除套餐
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    @DeleteMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> deleteByIdWithDish(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            setmealService.deleteByIdWithDish(ids[i]);
        }
        return R.success("删除成功");
    }

    @PostMapping("status" + "/{status}")
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> status(@PathVariable Integer status, Long[] ids) {
        Setmeal setmeal = new Setmeal();
        for (int i = 0; i < ids.length; i++) {
            setmeal.setStatus(status);
            setmeal.setId(ids[i]);
            setmealService.updateById(setmeal);
        }
        return R.success("成功");
    }

    @GetMapping("/list")
//   使用 Spring Cache 缓存到redis
    @Cacheable(value = "setMealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")//R返回值 不能序列化，会500
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(Setmeal::getStatus, 1);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }
    @GetMapping("/dish/{categoryId}")
    public R<List<SetmealDish>> list(@PathVariable Long categoryId) {
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(categoryId!=null,SetmealDish::getSetmealId,categoryId);
       wrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        return R.success(list);
    }
}
