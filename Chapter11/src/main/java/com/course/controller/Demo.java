package com.course.controller;

import com.course.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j
@RestController
@Api(value="v1", description = "只是我的第一个版本的demo")
@RequestMapping("v1")
public class Demo {

    //首先获取一个执行sql的语句的对象

    @Autowired
    private SqlSessionTemplate template;

    @RequestMapping(value = "/getUserCount",method = RequestMethod.GET)
    @ApiOperation(value = "可以获取到的用户数",httpMethod = "GET")
    public int getUserCount(){
        return template.selectOne("getUserCount");
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ApiOperation(value = "增加用户", httpMethod = "POST")
    public int addUser(@RequestBody User user) {
        return template.insert("addUser", user);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    @ApiOperation(value = "更新用户", httpMethod = "POST")
    public int updateUser(@RequestBody User user) {
        return template.update("updateUser", user);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
    @ApiOperation(value = "删除用户", httpMethod = "GET")
    public <integer> int delUser(@RequestBody integer id) {
        return template.delete("deleteUser", id);
    }

}
