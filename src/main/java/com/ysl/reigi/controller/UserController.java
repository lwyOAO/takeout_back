//package com.ysl.reigi.controller;
//
//import com.aliyuncs.exceptions.ClientException;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.ysl.reigi.common.R;
//import com.ysl.reigi.pojo.User;
//import com.ysl.reigi.service.UserService;
//import com.ysl.reigi.utils.ValidateCodeUtils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @description:
// * @author: YSL
// * @time: 2023/2/27 11:14
// */
//@Slf4j
//@RestController
//@RequestMapping("/user")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//    /**
//     * @description: 发送短信
//     * @author: YSL @RequestBody User user, HttpSession session
//     * @time: 2023/2/20 9:52
//     */
//    @PostMapping("/sendMsg")
//    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws ClientException {
////        生成四位随机验证码
//        String phone = user.getPhone();
//        if (StringUtils.isNotEmpty(phone)){
//            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            log.info("code={}",code);
////        调用阿里云短信服务API
////            AliyunSmsUtils.sendSms(phone,code); 发送一次手机扣费 后端验证注释掉
//
////        将验证码保存到session中
////        session.setAttribute(phone,code);
//
////            优化将 验证码缓存redis中 设置国企时间 60s
//            redisTemplate.opsForValue().set(phone,code,60, TimeUnit.SECONDS);
//            return R.success("手机验证码短信发送成功");
//    }
//        return R.error("短信发送失败");
//    }
//    /**
//     * @description: 验证登录
//     * @author: YSL 使用map接收参数并加以验证
//     * @time: 2023/2/20 9:52
//     */
//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map, HttpSession session){
//        log.info("{}",map);
////        从map 获取手机号和验证码
//        String phone = map.get("phone").toString();
////        获取session中的code key为phone的value
//        String code = map.get("code").toString();
////        进行验证码比对(页面提交的验证码与Session进行比对)
////        Object codeInSession = session.getAttribute(phone);
////        优化 从redis中取出 验证码
//        String redisCode = (String) redisTemplate.opsForValue().get(phone);
////        校验
//        if (redisCode!=null&&redisCode.equals(code)){
////            成功登录
////          1.判断是否为新用户，如果是新用户 自动注册
//            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
////            根据手机号查询 用户是否已经注册过
//            wrapper.eq(User::getPhone,phone);
//            User user = userService.getOne(wrapper);
//            if (user==null){
////                新用户 自动注册
//                 user = new User();
//                user.setPhone(phone);
//                user.setStatus(1);
//                userService.save(user);
//            }
//            session.setAttribute("user",user.getId());
////            登录成功 删除redis中的验证码缓存
//            redisTemplate.delete(phone);
//
//          return   R.success(user);
//        }
//        return R.error("登录失败,验证码失效");
//    }
//    @PostMapping("/loginout")
//    public R<String> loginout(HttpServletRequest request){
////        退出后清理 session中的 用户ID 和 验证码
//        request.getSession().removeAttribute("user");
//        request.getSession().removeAttribute("phone");
//        return R.success("已退出");
//    }
//}
package com.ysl.reigi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ysl.reigi.common.R;
import com.ysl.reigi.pojo.User;
import com.ysl.reigi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request,@RequestBody User user){
        //1、将页面提交的密码password进行md5加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,user.getPhone());
        User user1 = userService.getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(user1 == null){
            return R.error("登录失败");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if(!user1.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //5、查看用户状态，如果为已禁用状态，则返回用户已禁用结果
        if(user1.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //6、登录成功，将用户id存入Session并返回登录成功结果
        request.getSession().setAttribute("user",user1.getId());
        return R.success(user1);
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录用户的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        // 1、检查手机号是否已被注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getPhone());
        User existingUser = userService.getOne(queryWrapper);
        if (existingUser != null) {
            return R.error("手机号已被注册");
        }
        // 2、注册用户
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes())); // 密码加密
        boolean success = userService.save(user);
        if (success) {
            return R.success("注册成功");
        } else {
            return R.error("注册失败");
        }
    }

    /**
     * 修改用户信息
     * @param
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody User user) {
        log.info(user.toString());
        // 从Session中获取当前登录用户的信息
        Object userObj = request.getSession().getAttribute("user");
        if (userObj != null && userObj instanceof Long) {
            Long userId = (Long) userObj;
            // 根据用户ID从数据库获取用户信息
            User currentUser = userService.getById(userId);
            if (currentUser != null) {

                // 更新用户信息
                //currentUser.setPhone(user.getPhone());
                //currentUser.setPassword(user.getPassword());
                currentUser.setName(user.getName());
                currentUser.setIdNumber(user.getIdNumber());
                currentUser.setSex(user.getSex());

                // 将密码进行MD5加密
                //var password = user.getPassword();
                //password = DigestUtils.md5DigestAsHex(password.getBytes());
                //currentUser.setPassword(password);

                // 保存到数据库
                boolean success = userService.updateById(currentUser);

                if (success) {
                    // 更新Session中的用户信息
                    request.getSession().setAttribute("user", currentUser);
                    return R.success("用户信息修改成功");
                } else {
                    return R.error("用户信息修改失败");
                }
            } else {
                return R.error("用户不存在");
            }
        } else {
            return R.error("用户未登录");
        }
    }

    /**
     * 查询用户信息
     * @param request
     * @return
     */
    @GetMapping("/search")
    public R<User> search(HttpServletRequest request) {
        // 从Session中获取当前登录用户的id
        Object userObj = request.getSession().getAttribute("user");
        if (userObj != null && userObj instanceof Long) {
            Long userId = (Long) userObj;

            // 根据用户ID从数据库获取用户信息
            User user = userService.getById(userId);
            if (user != null) {
                return R.success(user);
            } else {
                return R.error("用户不存在");
            }
        } else {
            return R.error("用户未登录");
        }
    }

}
