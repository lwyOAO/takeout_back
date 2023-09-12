package com.ysl.reigi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ysl.reigi.dto.SetmealDto;
import com.ysl.reigi.pojo.Category;
import com.ysl.reigi.pojo.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    /**
     * @description: 新增套餐 保存套餐和菜品的关联关系
     * @author: YSL   setmealDto
     * @time: 2023/2/20 9:52
     */

    void saveWithDish(SetmealDto setmealDto);
    /**
     * @description: 修改操作 数据回显
     * @author: YSL id
     * @time: 2023/2/20 9:52
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * @description: 修改操作 多表修改
     * @author: YSL setmealDto
     * @time: 2023/2/20 9:52
     */
     void updateByIdWithDish(SetmealDto setmealDto);
    /**
     * @description: 删除操作 多表修改
     * @author: YSL setmealDto
     * @time: 2023/2/20 9:52
     */
    void deleteByIdWithDish(Long ids);
}

