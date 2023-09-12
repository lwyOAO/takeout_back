package com.ysl.reigi.controller;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ysl.reigi.common.R;
import com.ysl.reigi.pojo.User;
import com.ysl.reigi.service.UserService;
import com.ysl.reigi.utils.ValidateCodeUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: YSL
 * @time: 2023/2/27 11:14
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * @description: 发送短信
     * @author: YSL @RequestBody User user, HttpSession session
     * @time: 2023/2/20 9:52
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws ClientException {
//        生成四位随机验证码
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
//        调用阿里云短信服务API
//            AliyunSmsUtils.sendSms(phone,code); 发送一次手机扣费 后端验证注释掉

//        将验证码保存到session中
//        session.setAttribute(phone,code);

//            优化将 验证码缓存redis中 设置国企时间 60s
            redisTemplate.opsForValue().set(phone,code,60, TimeUnit.SECONDS);
            return R.success("手机验证码短信发送成功");
    }
        return R.error("短信发送失败");
    }
    /**
     * @description: 验证登录
     * @author: YSL 使用map接收参数并加以验证
     * @time: 2023/2/20 9:52
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info("{}",map);
//        从map 获取手机号和验证码
        String phone = map.get("phone").toString();
//        获取session中的code key为phone的value
        String code = map.get("code").toString();
//        进行验证码比对(页面提交的验证码与Session进行比对)
//        Object codeInSession = session.getAttribute(phone);
//        优化 从redis中取出 验证码
        String redisCode = (String) redisTemplate.opsForValue().get(phone);
//        校验
        if (redisCode!=null&&redisCode.equals(code)){
//            成功登录
//          1.判断是否为新用户，如果是新用户 自动注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//            根据手机号查询 用户是否已经注册过
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            if (user==null){
//                新用户 自动注册
                 user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
//            登录成功 删除redis中的验证码缓存
            redisTemplate.delete(phone);

          return   R.success(user);
        }
        return R.error("登录失败,验证码失效");
    }
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
//        退出后清理 session中的 用户ID 和 验证码
        request.getSession().removeAttribute("user");
        request.getSession().removeAttribute("phone");
        return R.success("已退出");
    }
}
