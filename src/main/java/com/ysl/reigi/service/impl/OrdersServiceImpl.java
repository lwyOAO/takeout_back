package com.ysl.reigi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ysl.reigi.common.BaseContext;
import com.ysl.reigi.common.CustomException;
import com.ysl.reigi.common.R;
import com.ysl.reigi.dto.DishDto;
import com.ysl.reigi.dto.OrderDto;
import com.ysl.reigi.mapper.OrdersMapper;
import com.ysl.reigi.pojo.*;
import com.ysl.reigi.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/24 16:29
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Override
    @Transactional
    public void submit(Orders orders) {
//        获得当前用户id
        Long userId = BaseContext.getCurrentId();
//        查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if (shoppingCarts==null||shoppingCarts.size()==0){
            throw new CustomException("购物车为空，不能下单");
        }
//        查询用户数据
        User user = userService.getById(userId);
//        查询用户地址
        Long addressBookId = orders.getAddressBookId();
        if (addressBookId==null){
            throw new CustomException("请选择地址");
        }
            AddressBook addressBook = addressBookService.getById(addressBookId);
        long orderId = IdWorker.getId();//        生成订单号

        AtomicInteger amount = new AtomicInteger(0);//这个类型的变量计算金额，防止高并发 保证安全

//        编辑购物车数据 存为集合 输出 OrderDetail
        List<OrderDetail> orderDetails=shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
//            每次遍历后 累加上金额（addAndGet）  每个的金额*份数（multiply）
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

//       设置order数据 向订单表插入数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);
//        向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
//        清空已购买的购物车数据
        shoppingCartService.remove(wrapper);
    }

    @Override
    public Page listPage(int page, int pageSize) {

        Page pageInfo = new Page(page, pageSize);

//        获得当前用户id
        Long userId = BaseContext.getCurrentId();
//        构造分页器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        wrapper.orderByDesc(Orders::getOrderTime);

        LambdaQueryWrapper<OrderDetail> wrapper1 = new LambdaQueryWrapper<>();
        List<OrderDto> orderDtoList = new ArrayList<>();
        OrderDto orderDto = new OrderDto();

        List<Orders> list1 = list(wrapper);
        System.out.println(" List1"+list1);

        for (int i=0;i<list1.size();i++){
            Orders orders = list1.get(i);

            orderDto.setOrderTime(orders.getOrderTime());
            orderDto.setStatus(orders.getStatus());
            orderDto.setNumber(orders.getNumber());
            orderDto.setAmount(orders.getAmount());

            List<OrderDetail> list2 = orderDetailService.list(wrapper1);
            orderDto.setOrderDetails(list2);
            orderDtoList.add(orderDto);

        }

        pageInfo.setRecords(orderDtoList);
        return pageInfo;
    }

}
