package com.ysl.reigi.dto;

import com.ysl.reigi.pojo.Setmeal;
import com.ysl.reigi.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
