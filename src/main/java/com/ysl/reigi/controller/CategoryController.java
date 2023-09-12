package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ysl.reigi.common.R;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:新增分类
 * @author: YSL
 * @time: 2023/2/9 20:22
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        Page pageInfo=new Page(page,pageSize);

        categoryService.page(pageInfo);
        return R.success(pageInfo);
    }
    /**
     * @description: 根据ID修改分类信息
     * @author: YSL
     * @time: 2023/2/9 20:22
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        long id = Thread.currentThread().getId();
        category.setUpdateUser(id);
        categoryService.updateById(category);
        return R.success("修改成功");
    }
    /**
     * @description: 自定义remove 调用 根据id实现数据删除
     * @author: YSL
     * @time: 2023/2/9 20:22
     */
    @DeleteMapping
    public R<String> removeById(Long id){

//        categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("删除成功");
    }
//    获取添加菜品的 菜品分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        构造条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
//        通过Sort 进行升序排序，相同时使用UpdateTime进行降序排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
//        查询出
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
