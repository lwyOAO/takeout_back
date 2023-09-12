package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.mapper.AddressBookMapper;
import com.ysl.reigi.pojo.AddressBook;
import com.ysl.reigi.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 14:30
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {
}
