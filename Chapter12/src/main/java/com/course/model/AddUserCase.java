package com.course.model;

import lombok.Data;

@Data
public class AddUserCase {
    private int id;
    private String userName;
    private String password;
    private String sex;
    private String age;
    private String permission;
    private String isDelete;
    private String expected;

    @Override
    public String toString() {
        return (
                "{"+"id:"+id+","+
                    "userName:"+userName+","+
                    "password:"+password+","+
                    "sex:"+sex+","+
                    "age:"+age+","+
                    "permission:"+permission+","+
                    "isDelete:"+isDelete+","+
                    "expected:"+expected+"}"
        );
    }
}
