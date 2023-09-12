package com.ysl.reigi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ysl.reigi.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
