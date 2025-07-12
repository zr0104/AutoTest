package com.course.model;

import lombok.Data;

@Data
public class Users {
    private Integer id;
    private String userName;
    private String password;
    private String age;
    private String sex;
    private String permission;
    private String isDelete;
    private String expected;

//    @Override
//    public String toString() {
//        return (
//                "{"+"id:"+id+","+
//                    "userName:"+userName+","+
//                    "password:"+password+","+
//                    "age:"+age+","+
//                    "sex:"+sex+","+
//                    "permission:"+permission+","+
//                    "isDelete:"+isDelete+","+
//                    "permission:"+permission+"}"
//        );
//    }


}
