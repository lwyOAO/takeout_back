package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.mapper.UserMapper;
import com.ysl.reigi.pojo.User;
import com.ysl.reigi.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 11:15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
