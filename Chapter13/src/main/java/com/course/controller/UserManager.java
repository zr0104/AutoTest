package com.course.controller;

import com.course.model.Users;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@Api(value = "v1", description = "用户管理系统")
@RequestMapping("v1")
@Log4j2
public class UserManager {

    @Autowired
    private SqlSessionTemplate template; // 假设使用 MyBatis

    public void AuthController(SqlSessionTemplate template) {
        this.template = template;
    }

    @Operation(summary = "用户登录", description = "通过用户名和密码登录，返回登录状态", method = "POST")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(HttpServletResponse response, @RequestBody @Valid Users user) {

        // 1. 查询数据库验证用户
        int count = template.selectOne("login", user);
        log.info("用户登录验证结果: count={}, username={}",count,user.getUserName());

        // 2. 登录成功处理
        if (count == 1) {
            // 设置 Cookie（推荐 HttpOnly + Secure）
            Cookie cookie = new Cookie("login", "true");
            cookie.setPath("/");
            cookie.setHttpOnly(true); // 防止 XSS 攻击
            cookie.setSecure(true);  // 仅 HTTPS 传输（生产环境建议启用）
            response.addCookie(cookie);

            log.info("用户登录成功: username={}",user.getUserName());
            return ResponseEntity.ok(new ApiResponse(true, "登录成功"));
        }

        // 3. 登录失败
        log.warn("用户登录失败: username={}",user.getUserName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "用户名或密码错误"));
    }


    @Operation(summary = "添加用户接口", description = "用户信息插入用户表", method = "POST")
    @PostMapping(value = "/addUser")
    public ResponseEntity<ApiResponse> addUser(HttpServletRequest request, @RequestBody @Valid Users user) {
        try {
//             1. 验证 Cookie（或 Token）
            Boolean isValidSession = verifyCookies(request);
            if (Boolean.FALSE.equals(isValidSession)) {
                log.warn("未授权的访问尝试，用户: {}",user.getUserName());
                return ResponseEntity.status(401).body(new ApiResponse(false, "未授权"));
            }

            // 2. 插入用户数据
            int insertedRows = template.insert("addUser", user);
            if (insertedRows > 0) {
                log.info("用户添加成功，用户名: {}, 插入行数: {}",user.getUserName(),user.getId());
                return ResponseEntity.ok(new ApiResponse(true, "用户添加成功"));
            } else {
                log.warn("用户添加失败，数据库未返回影响行数，用户名: {}",user.getUserName());
                return ResponseEntity.badRequest().body(new ApiResponse(false, "添加用户失败"));
            }
        }catch (Exception e) {
            log.error("添加用户时发生异常，用户名: {}",user.getUserName(),e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "服务器内部错误"));
        }
    }

    @Operation(summary = "获取用户列表", description = "根据条件获取用户信息列表", method = "POST")
    @PostMapping("/getUserInfo")
    public List<Users> getUserInfo(HttpServletRequest request, @RequestBody Users user) {
        if (Boolean.TRUE.equals(verifyCookies(request))) {
            log.warn("Cookie 验证失败，返回空列表");
            return Collections.emptyList();
        }

        List<Users> users = template.selectList("getUserInfo", user);
        log.info("获取用户的数量是：{}", users != null ? users.size() : 0);
        return users != null ? users : Collections.emptyList();
    }

    @Operation(summary = "更新/删除用户接口", description = "通过用户ID更新或删除用户信息，isDelete为1删除", method = "POST")
    @PostMapping("/updateUserInfo")
    public ResponseEntity<ApiResponse> updateUserInfo(
            HttpServletRequest request, @RequestBody Users user) {

        // 1. 验证请求权限
        if (verifyCookies(request)) {
            log.warn("未授权的访问尝试，IP: {}", request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "认证失败，请重新登录"));
        }

        // 2. 参数校验
        if (user.getId() == null || user.getId() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "用户ID不能为空且必须大于0"));
        }

        // 3. 执行数据库操作
        try {
            int affectedRows = template.update("updateUserInfo", user);

            if (affectedRows > 0) {
                String action = (user.getIsDelete() != null && user.getIsDelete().equals("1"))
                        ? "删除" : "更新";
                log.info("用户{}成功，ID: {}, 影响行数: {}", action, user.getId(), affectedRows);

                return ResponseEntity.ok(
                        new ApiResponse(true, action + "用户信息成功: "+affectedRows)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "未找到对应ID的用户"));
            }
        } catch (Exception e) {
            log.error("数据库操作失败: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "服务器内部错误"));
        }
    }


//    // 登录响应封装类
//    private record ApiResponse(boolean success, String message) {
//        public boolean success() {
//            return success;
//        }
//        @Override
//        public String message() {
//            return message;
//        }
//    }

    // 登录响应封装类: 修改发接口返回结果：Expected :{"success":true,"message":"登录成功"} 修改为：true
    private record ApiResponse(boolean success, String message) {
        public boolean success() {
            return success;
        }
        @Override
        public String message() {
            return message;
        }
    }


//    @Autowired
//    private SqlSessionTemplate template;
//
//    @ApiOperation(value = "登录接口", httpMethod = "POST")
//    @RequestMapping(value = "/login")
//    public Boolean login(HttpServletResponse response, @RequestBody Users user) {
//        int i = template.selectOne("login", user);
//        Cookie cookie = new Cookie("login", "true");
//        response.addCookie(cookie);
//        log.info("查询到的结果是："+i);
//        if (i == 1) {
//            log.info("查询到的用户是：" + user.getUserName());
//            return true;
//        }
//        return false;
//    }
//
//    @ApiOperation(value = "添加用户接口", httpMethod = "POST")
//    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
//    public boolean addUser(HttpServletRequest request, @RequestBody Users user) {
//        try {
//            Boolean x = verifyCookies(request);
//            int result = 0;
//            if (x!=null) {
//                result = template.insert("addUser", user);
//            }
//            if (result > 0) {
//                log.info("添加用户的数量是：" + result);
//                return true;
//            }
//            return false;
//        }catch (Exception e) {
//            log.error("Failed to add user", e);
//            return ResponseEntity.status(500).body("Internal Server Error").hasBody();
//        }
//    }

//    @ApiOperation(value = "获取用户接口", httpMethod = "POST")
//    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
//    public List<Users> getUserInfo(HttpServletRequest request, @RequestBody Users user) {
//        Boolean x = verifyCookies(request);
//        if (x == true) {
//            List<Users> users = template.selectList("getUserList", user);
//            log.info("获取用户的数量是：" + users.size());
//            return users;
//        } else {
//            return null;
//        }
//    }

//    @ApiOperation(value = "更新/删除用户接口", httpMethod = "POST")
//    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
//    public int updateUser(HttpServletRequest request, @RequestBody Users user) {
//        Boolean x = verifyCookies(request);
//        int i = 0;
//        if (x == false) {
//            i = template.update("updateUserInfo", user);
//        }
//        log.info("获取用户的数量是：" + i);
//        return i;
//    }

    private Boolean verifyCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            log.info("cookies为空");
            return false;
        }
        for (Cookie cookie:cookies) {
            if (cookie.getName().equals("login") && cookie.getValue().equals("true")) {
                log.info("cookies验证通过");
                return true;
            }
        }
        return false;
    }
}
