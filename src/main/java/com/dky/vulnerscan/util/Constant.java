package com.dky.vulnerscan.util;

public class Constant {
    public static final String USER_CONTEXT = "USER_CONTEXT";//用户对象放到Session中的键名称

    public static final String ADMIN_TYPE = "admin";

    public static final String CHECKER_TYPE = "checker";

    public static final int MAX_LOGIN_NUM = 3;//用户登录失败最多尝试的登录次数

    public static final long LOGIN_INTERVAL = 2 * 60 * 1000;//用户被锁定的时间

    public static final int USER_NOT_EXIST = -1; //用户不存在

    public static final int USER_PASSWD_ERROR = -2; //登录错误

    public static final int USER_LOGIN_SUCCESS = 1; //登录成功

    public static final int USER_LOCKED = -3; //用户被锁定

    public static final int USER_FIRST_LOGIN = -4; //用户初次登录

    public static final int SUCCESS = 1;

    public static final int FAIL = -1;

    public static final long UNIX_DAY_TIME = 24 * 60 * 60 * 1000;

    public static final int TASK_WAITING = -1; //任务等待执行

    public static final int TASK_QUEUE_EMPTY = -2; //任务队列为空

    public static final String CREATE_STATE = "CREATE_STATE";
}
