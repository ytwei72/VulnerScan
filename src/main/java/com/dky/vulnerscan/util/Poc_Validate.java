package com.dky.vulnerscan.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.json.JSONException;
//import org.json.JSONObject;
/**
 * Created by devicesearch on 2016/12/19.
 */
public class Poc_Validate {

    public static JSONObject poc_status_message(JSONObject poc_status, String vul_id) {

        JSONObject poc_status_message = new JSONObject();
        int class_flag=-1;
        int def_init_flag=-1;
        int def_verify_flag=-1;
        int ret_data_flag=-1;
        int ret_flag=-1;
        int class_line=-1;
        int def_init_line=-1;
        int def_verify_line=-1;
        int ret_data_line=-1;
        int ret_line=-1;
        String class_name="";
        String done_status="no";
        try {
            done_status=(String) poc_status.get("done");

            JSONObject class_status = (JSONObject) poc_status.get("class_status");
            JSONObject def_init_status = (JSONObject) poc_status.get("def_init_status");
            JSONObject def_verify_status = (JSONObject) poc_status.get("def_verify_status");
            JSONObject ret_data_status = (JSONObject) poc_status.get("ret_data_status");
            JSONObject ret_status = (JSONObject) poc_status.get("ret_status");

            class_flag=(int) class_status.get("validate");
            def_init_flag=(int) def_init_status.get("validate");
            def_verify_flag=(int) def_verify_status.get("validate");
            ret_data_flag=(int) ret_data_status.get("validate");
            ret_flag=(int) ret_status.get("validate");

            class_line=(int) class_status.get("line");
            def_init_line=(int) def_init_status.get("line");
            def_verify_line=(int) def_verify_status.get("line");
            ret_data_line=(int) ret_data_status.get("line");
            ret_line=(int) ret_status.get("line");

            class_name= (String) class_status.get("class_name");
        } catch (JSONException e) {
        }

        List<String> message = new ArrayList<String>();
        if (done_status.equals("no")) {
            message.add("验证程序代码处理过程出现异常！");
            poc_status_message.put("status", 0);
        }
        else {
            if (class_flag>0 && def_init_flag>0 && def_verify_flag>0 && ret_data_flag>0 && ret_flag>0) {

                if (class_flag==1 && def_init_flag==1 && def_verify_flag==1 && ret_data_flag==1 && ret_flag==1) {
                    if (ret_line>ret_data_line && ret_data_line>def_verify_line && def_verify_line>class_line && def_init_line>class_line && class_line>-1)
                    {
                        if (class_name.equals(vul_id.replace("-","_"))) {
                            message.add("验证程序代码包含并符合所要求的规范！");
                            poc_status_message.put("status", 1);
                        } else {
                            message.add("类名与漏洞编号不匹配！");
                            poc_status_message.put("status", -1);
                        }
                    } else {
                        message.add("代码中的类、方法或变量定义语句顺序有误！");
                        poc_status_message.put("status", -1);
                    }
                } else {
                    poc_status_message.put("status", -1);
                    if (class_flag>1) {
                        message.add("class类定义不唯一！");
                    }
                    if (def_init_flag>1) {
                        message.add("__init__方法定义不唯一！");
                    }
                    if (def_verify_flag>1) {
                        message.add("verify方法定义不唯一！");
                    }
                    if (ret_data_flag>1) {
                        message.add("verify中ret返回结果定义定义不唯一！");
                    }
                    if (ret_flag>1) {
                        message.add("verify中return语句重复！");
                    }
                }

            } else {
                poc_status_message.put("status", -1);
                switch (class_flag) {
                    case 0:message.add("class类定义语句有误！");
                        break;
                    case -1:message.add("缺少class类定义语句！");
                        break;
                }
                switch (def_init_flag) {
                    case 0:message.add("__init__初始化方法定义语句有误！");
                        break;
                    case -1:message.add("缺少__init__初始化方法定义语句！");
                        break;
                }
                switch (def_verify_flag) {
                    case 0:message.add("verify方法定义语句有误！");
                        break;
                    case -1:message.add("缺少verify方法定义语句！");
                        break;
                }
                switch (ret_data_flag) {
                    case 0:message.add("ret返回结果定义语句有误！");
                        break;
                    case -1:message.add("verify中缺少ret返回结果定义语句！");
                        break;
                }
                switch (ret_flag) {
                    case 0:message.add("return语句有误！");
                        break;
                    case -1:message.add("verify中缺少return语句！");
                        break;
                }
            }
        }
        poc_status_message.put("message", message);
        return poc_status_message;
    }
    public static JSONObject poc_validate(List<String> str_line) {

        JSONObject poc_status = new JSONObject();
        JSONObject class_status = new JSONObject();
        JSONObject def_init_status = new JSONObject();
        JSONObject def_verify_status = new JSONObject();
        JSONObject ret_data_status = new JSONObject();
        JSONObject ret_status = new JSONObject();
        String class_name="";

        int class_count=0;
        int def_init_count=0;
        int def_verify_count=0;
        int ret_data_count=0;
        int ret_count=0;

        try {
            class_status.put("validate", -1);
            class_status.put("line", 0);
            class_status.put("class_name", class_name);

            def_init_status.put("validate", -1);
            def_init_status.put("line", 0);

            def_verify_status.put("validate", -1);
            def_verify_status.put("line", 0);

            ret_data_status.put("validate", -1);
            ret_data_status.put("line", 0);

            ret_status.put("validate", -1);
            ret_status.put("line", 0);

            poc_status.put("done", "no");
        } catch (JSONException e) {}

        String class_pattern = "(class *)(\\w*)( *\\( *\\w* *\\)) *: *($|\\#)";
        Pattern class_r = Pattern.compile(class_pattern);
        String def_init_pattern = "^( {4}|\\t)(def *)(__init__)(\\(self *, *host *, *port *(= *\\d{1,5} *)?, *\\*args *, *\\*\\*kwargs *\\)) *: *($|\\#)";
        Pattern def_init_r = Pattern.compile(def_init_pattern);
        String def_verify_pattern = "^( {4}|\\t)(def *)(verify)(\\(self *,? *\\)) *: *($|\\#)";
        Pattern def_verify_r = Pattern.compile(def_verify_pattern);
        String ret_data_pattern = "^( {4}|\\t)( {4}|\\t)(ret *= *\\{\"status\" *: *False *, *\"data\" *: *None *\\} *)($|\\#)";
        Pattern ret_data_r = Pattern.compile(ret_data_pattern);
        String ret_pattern = "^( {4}|\\t)( {4}|\\t)(return *ret *)($|\\#)";
        Pattern ret_r = Pattern.compile(ret_pattern);

        Boolean verify_scope=false;
        for (int i=0;i<str_line.size();i++) {
            Matcher class_m = class_r.matcher(str_line.get(i));
            Matcher def_init_m = def_init_r.matcher(str_line.get(i));
            Matcher def_verify_m = def_verify_r.matcher(str_line.get(i));
            Matcher ret_data_m = ret_data_r.matcher(str_line.get(i));
            Matcher ret_m = ret_r.matcher(str_line.get(i));
            if (str_line.get(i).startsWith("class")) {
                try {
                    if (class_m.find()) {
                        class_count++;
                        class_name=class_m.group(2);
                        class_status.put("class_name", class_name);
                    }
                    class_status.put("validate", class_count);
                    class_status.put("line", i);
                } catch (JSONException e) {}
            }
            else if (str_line.get(i).indexOf("def")>=0) {

                if (str_line.get(i).indexOf("init")>=0) {
                    try {
                        if (def_init_m.find()) {
                            def_init_count++;
                        }
                        def_init_status.put("validate", def_init_count);
                        def_init_status.put("line", i);
                    } catch (JSONException e) {}
                } else if (str_line.get(i).indexOf("verify")>=0) {
                    try {
                        if (def_verify_m.find()) {
                            verify_scope=true;
                            def_verify_count++;
                        }
                        def_verify_status.put("validate", def_verify_count);
                        def_verify_status.put("line", i);
                    } catch (JSONException e) {}
                }
            }
            else if (verify_scope==true && str_line.get(i).indexOf("ret")>=0 && str_line.get(i).indexOf("status")>=0) {
                try {
                    if (ret_data_m.find()) {
                        ret_data_count++;
                    }
                    ret_data_status.put("validate", ret_data_count);
                    ret_data_status.put("line", i);
                } catch (JSONException e) {}
            }
            else if (verify_scope==true && str_line.get(i).indexOf("return")>=0) {
                try {
                    if (ret_m.find()) {
                        ret_count++;
                    }
                    ret_status.put("validate", ret_count);
                    ret_status.put("line", i);
                } catch (JSONException e) {}
            }
        }
        try {
            poc_status.put("done", "yes");
        } catch (JSONException e) {}

        try {
            poc_status.put("class_status", class_status);
            poc_status.put("def_init_status", def_init_status);
            poc_status.put("def_verify_status", def_verify_status);
            poc_status.put("ret_data_status", ret_data_status);
            poc_status.put("ret_status", ret_status);
        } catch (JSONException e) { }

        return poc_status;
    }
}
