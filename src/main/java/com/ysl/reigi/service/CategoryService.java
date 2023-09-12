package com.ysl.reigi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ysl.reigi.mapper.CategoryMapper;
import com.ysl.reigi.pojo.Category;

public interface CategoryService extends IService<Category> {
//    根据id 删除分类
    public void remove(Long id);
}
