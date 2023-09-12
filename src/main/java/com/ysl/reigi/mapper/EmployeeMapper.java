package com.ysl.reigi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ysl.reigi.pojo.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    @Delete("delete * from employee where id = {id}")
    void deleteByID(Long id);
}
