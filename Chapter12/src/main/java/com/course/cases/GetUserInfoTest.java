package com.course.cases;

import com.course.config.TestConfig;
import com.course.model.GetUserInfoCase;
import com.course.model.User;
import com.course.utils.DatabaseUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetUserInfoTest {
    @Test(dependsOnGroups = "loginTrue",description = "获取userId为1的用户信息")
    public void GetUserInfo() throws IOException, InterruptedException {
        SqlSession sqlSession = DatabaseUtil.getSqlSession();
        GetUserInfoCase getUserInfoCase = sqlSession.selectOne("getUserInfoCase", 1);
        System.out.println(getUserInfoCase.toString());
        System.out.println(TestConfig.getUserInfoUrl);

        //下边为写完接口的代码
        JSONArray resultJson = getJsonResult(getUserInfoCase);

        /**
         * 下边三行可以先讲
         */
        Thread.sleep(2000);
        User user = sqlSession.selectOne(getUserInfoCase.getExpected(),getUserInfoCase);
//        User user = sqlSession.selectOne("getUserInfo",getUserInfoCase);
        System.out.println("自己查库获取用户信息:"+user.toString());

        List userList = new ArrayList();
        userList.add(user);
        JSONArray jsonArray = new JSONArray(userList);
        String jsonArrays= jsonArray.toString();
//        JSONArray expectedArray = new JSONArray(jsonArrays);
        String resultJsons = resultJson.getString(0);
//        JSONArray actualArray = new JSONArray(resultJsons);

        System.out.println("获取用户信息:"+jsonArrays);
        System.out.println("调用接口获取用户信息:"+resultJsons);
//        Assert.assertEquals(jsonArray,resultJson);

        // 断言 actual 包含 expected 的所有元素（忽略顺序和额外元素）
        JSONAssert.assertEquals(
                jsonArrays.toString(),  // 预期 JSON
                resultJsons.toString(),    // 实际 JSON
//                false                 // 是否严格匹配顺序（false 表示不严格）
                JSONCompareMode.LENIENT
        );
    }

    private JSONArray getJsonResult(@NotNull GetUserInfoCase getUserInfoCase) throws IOException {
        HttpPost post = new HttpPost(TestConfig.getUserInfoUrl);
        JSONObject param = new JSONObject();
        param.put("id",getUserInfoCase.getId());
        param.put("userId",getUserInfoCase.getUserId());
        param.put("expected",getUserInfoCase.getExpected());

        //设置请求头信息 设置header
        post.setHeader("content-type","application/json");
        //将参数信息添加到方法中
        StringEntity entity = new StringEntity(param.toString(),"utf-8");
        post.setEntity(entity);

        //声明一个对象来进行响应结果的存储
        String result;
        //设置cookies
        TestConfig.defaultHttpClient.setCookieStore(TestConfig.store);
        //执行post方法
        HttpResponse response = TestConfig.defaultHttpClient.execute(post);
        //获取响应结果
        result = EntityUtils.toString(response.getEntity(),"utf-8");
        System.out.println("调用接口result:"+result);
        List<String> resultList = Collections.singletonList(result);
        JSONArray array = new JSONArray(resultList);
        System.out.println(array.toString());
        return array;

    }

}
