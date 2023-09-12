package com.ysl.reigi.controller;

import com.ysl.reigi.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @description: 文件的上传和下载
 * @author: YSL
 * @time: 2023/2/10 14:23
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reigi.path}")
    private String basePath;

    /**
     * @description: 文件的上传
     * @author: YSL
     * @time: 2023/2/10 14:23
     */
    @PostMapping("/upload")
//    参数名 file 必须和前端传过来的name保存一致 才能接收到
//    file 是一个临时文件 需要转存在指定位置 否则本次请求完成后 文件会自动删除
    public R<String> upload(MultipartFile file) {
        log.info("文件上传成功{}", file.toString());
//        原始文件名
        String originalFilename = file.getOriginalFilename();
//        动态获取原始原件名称后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        防止使用原始重名 导致前文件被覆盖 使用随机生成UUID
        String fileName = UUID.randomUUID().toString() + suffix;
//         创建一个目录对象
        File dir = new File(basePath);
//        判断文件目录是否存在
        if (!dir.exists()) {
//            创建目录
            dir.mkdir();
        }
        try {
//            临时文件转存到指定位置 transferTo
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        需要给页面返回文件名称，保存在菜品类中 然后保存到数据库
        return R.success(fileName);
    }

    /**
     * @description: 文件的下载
     * @author: YSL
     * @time: 2023/2/10 14:23
     */
//    通过输出流 显示下载数据
    @GetMapping("download")
    public void download(HttpServletResponse response, String name) {
//        输入流 ，通过输入流 读取文件内容
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(basePath + name));
//        输出流，通过输出流将文件写回浏览器 显示图片 通过response响应对象
            ServletOutputStream outputStream = response.getOutputStream();
//            设置响应的标题类型
            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
//            关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
