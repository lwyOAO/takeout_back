package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ysl.reigi.common.BaseContext;
import com.ysl.reigi.common.R;
import com.ysl.reigi.pojo.ShoppingCart;
import com.ysl.reigi.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 14:20
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long id = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,id);
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);

    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {

//        设置用户id，指定当前是那个用户的购物车
        Long id = BaseContext.getCurrentId();
        shoppingCart.setUserId(id);

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, id);

        Long dishId = shoppingCart.getDishId();
//        查询当前菜品是否在购物车中
        if (dishId != null) {
//            添加购物车的是菜品时
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //            添加购物车的是套餐时
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
//
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if (one != null) {
//        如果已存在 在原来的数量上加一
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else {
//        不存在 添加购物车，数量默认是1
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success("添加成功");

    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        if (dishId!=null){
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        Integer number = one.getNumber();
        if (number>1){
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCartService.removeById(one);
        }
        return R.success("成功");
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,id);
        shoppingCartService.remove(wrapper);
        return R.success("清空成功");
    }
}
