package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ysl.reigi.common.R;
import com.ysl.reigi.dto.DishDto;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Dish;
import com.ysl.reigi.pojo.DishFlavor;
import com.ysl.reigi.service.CategoryService;
import com.ysl.reigi.service.DishFlavorService;
import com.ysl.reigi.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/10 13:44
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;


    //    菜品信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * @description: 新增菜品 操作两种表 口味表和菜品表  使用Dto 数据模型
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) throws InterruptedException {
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        dishService.updateByIdWithFlavor(dishDto);
//        延迟双删
        Thread.sleep(50);
        return R.success("添加成功");

    }


    /**
     * @description: 根据ID查询菜品信息和口味信息 修改时数据回显
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);

    }
    /**
     * @description: 修改操作
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) throws InterruptedException {

        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        long id = Thread.currentThread().getId();
        dishDto.setUpdateUser(id);
        dishService.updateByIdWithFlavor(dishDto);
//        延迟双删
        Thread.sleep(50);
        redisTemplate.delete(key);
        return R.success("修改成功");
    }
    /**
     * @description: 根据ID删除两张表数据
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
    @DeleteMapping
    public R<String> deleteById(Long[] ids) throws InterruptedException {

        for (int i=0;i<ids.length;i++){
            Dish byId = dishService.getById(ids[i]);
            String key="dish_"+byId.getCategoryId()+"_"+byId.getStatus();
            redisTemplate.delete(key);
            dishService.deleteByIdWithFlavor(ids[i]);
            Thread.sleep(50);
            redisTemplate.delete(key);
        }

        return R.success("删除成功");
    }
    /**
     * @description: 根据ID进行停售
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
    @PostMapping("/status"+"/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids) throws InterruptedException {
//        log.info(dish.toString());
        Dish dish=new Dish();
        for (int i=0;i<ids.length;i++){
            Dish byId = dishService.getById(ids[i]);
            String key="dish_"+byId.getCategoryId()+"_1";
            redisTemplate.delete(key);
            dish.setStatus(status);
            dish.setId(ids[i]);
          dishService.updateById(dish);
            Thread.sleep(50);
            redisTemplate.delete(key);
        }
        return R.success("成功");
    }
    /**
     * @description: 根据条件 查询对应的菜品数据
     * @author: YSL
     * @time: 2023/2/10 13:44
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list( Dish dish){
//        //        条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
////        构造条件
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
////        添加查询状态为1  起售状态
//        queryWrapper.eq(Dish::getStatus,1);
//
////        通过Sort 进行升序排序，相同时使用UpdateTime进行降序排序
//        queryWrapper.orderByAsc(Dish::getPrice).orderByDesc(Dish::getUpdateTime);
////        查询出
//        List<Dish> list=dishService.list(queryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        List<DishDto> dishDtoList=null;
//        构造 缓存的key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
//        优化 从redis 获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList!=null){
//            redis 缓存存在 返回数据
            return R.success(dishDtoList);
        }
//        不存在 查询数据库
//
        //        条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        构造条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        添加查询状态为1  起售状态
        queryWrapper.eq(Dish::getStatus,1);

//        通过Sort 进行升序排序，相同时使用UpdateTime进行降序排序
        queryWrapper.orderByAsc(Dish::getPrice).orderByDesc(Dish::getUpdateTime);
//        查询出
        List<Dish> list=dishService.list(queryWrapper);

         dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

//       不存在  将查询到的信息写入redis中 设置过期时间 60分钟
         redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
