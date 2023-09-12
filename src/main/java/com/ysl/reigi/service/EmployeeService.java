package com.ysl.reigi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ysl.reigi.pojo.Employee;
import org.springframework.stereotype.Service;

public interface EmployeeService extends IService<Employee> {
    void deleteByID(Long id);
}
