package com.dky.vulnerscan.service;

import com.dky.vulnerscan.dao.EmailDao;
import com.dky.vulnerscan.dao.TaskDao;
import com.dky.vulnerscan.dao.UserDao;
import com.dky.vulnerscan.entity.ReceiverEmail;
import com.dky.vulnerscan.entity.User;
import com.dky.vulnerscan.util.Constant;
import com.dky.vulnerscan.util.SendEmailUtil;
import com.dky.vulnerscan.util.SimpleMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;


@Service
public class EmailService {
    @Autowired
    private EmailDao emailDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskDao taskDao;

    private SendEmailUtil sendEmailUtil;

    //发送邮件（附件报表）
    public HashMap<String, Object> sendEmail(int taskID, String userName) {
        SimpleMail simpleMail = new SimpleMail();
        String taskReportUrl = taskDao.getTaskReportUrl(taskID);
        User user = userDao.getUserByUserName(userName);
        String emailName = user.getEmailName();
        HashMap<String, Object> msgBiz = new HashMap<>();
        ArrayList<ReceiverEmail> receiverEmails = emailDao.getUserSendEmails(userName);
        msgBiz.put("bizNo", -1);
        if (taskReportUrl.length() <= 0) {
            msgBiz.put("bizMsg", "该任务下的报表不存在");
            return msgBiz;
        }
        if (emailName.length() <= 0) {
            msgBiz.put("bizMsg", "该用户下的发件人地址为空");
            return msgBiz;
        }
        if (receiverEmails.size() <= 0) {
            msgBiz.put("bizMsg", "该用户下的收件人地址为空");
            return msgBiz;
        }
        simpleMail.setUsername(emailName);
        simpleMail.setPassword(user.getEmailPasswd());
        simpleMail.setProtocol("smtp");
        String host = emailName.substring(emailName.indexOf("@") + 1);
        simpleMail.setHost("smtp" + "." + host);
        simpleMail.setFrom(emailName);
        String toTmails = "";
        for (int i = 0; i < receiverEmails.size(); i++) {
            toTmails += receiverEmails.get(i).getReceiverEmailAddress() + ",";
        }
        simpleMail.setTo(toTmails.substring(0, toTmails.length() - 1));
        simpleMail.setSubject(user.getEmailSubject());
        simpleMail.setContent(user.getEmailContent());
        simpleMail.setTaskReportPath(taskReportUrl);
        sendEmailUtil = new SendEmailUtil(simpleMail);
        if (!sendEmailUtil.sendEmail()) {
            msgBiz.put("bizNo", -1);
            msgBiz.put("bizMsg", "邮件发送失败");
            return msgBiz;
        }
        msgBiz.put("bizNo", 1);
        msgBiz.put("bizMsg", "成功发送");
        return msgBiz;
    }

    //获得指定用户下的收件人列表
    public ArrayList<HashMap<String, String>> getUserSendEmails(String userName) {
        ArrayList<HashMap<String, String>> receiverEmailsMap = new ArrayList<>();
        ArrayList<ReceiverEmail> receiverEmails = emailDao.getUserSendEmails(userName);
        for (int i = 0; i < receiverEmails.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("receiverName", receiverEmails.get(i).getReceiverName());
            map.put("receiverAddr", receiverEmails.get(i).getReceiverEmailAddress());
            receiverEmailsMap.add(map);
        }
        return receiverEmailsMap;
    }

    //修改邮箱信息
    public HashMap<String,Object> changeEmail(String sendAddr, String sendPS, String confirmPS,String emailSubject,String emailContent,String userName) {
        HashMap<String,Object> map=new HashMap<>();
        if (userDao.hasAlreadyExitsChecker(userName) == 0) {
            map.put("bizNo",-1);
            map.put("bizMsg","该用户名不存在");
            return map;
        }
        if (!sendPS.equals(confirmPS)) {
            map.put("bizNo",-1);
            map.put("bizMsg","两次输入密码不一样");
            return map;
        }
        userDao.changeEmail(sendAddr,sendPS,emailSubject,emailContent,userName);
        map.put("bizNo",1);
        map.put("bizMsg","修改成功");
        return map;
    }

    //添加一个邮件收件人
    public int addToemail(String userName, String receiverName, String receiverEmailAddress) {
        if (emailDao.hasAlreadyExitsEmailAddress(receiverEmailAddress) > 0) {
            return Constant.FAIL;
        }
        emailDao.addEmailAddress(userName, receiverName, receiverEmailAddress);
        return Constant.SUCCESS;
    }

    //删除一个邮件收件人
    public int deleteToemail(String userName, String receiverEmailAddress) {
        if (userDao.hasAlreadyExitsChecker(userName) == 0) {
            return Constant.FAIL;
        }
        emailDao.deleteToEmail(userName, receiverEmailAddress);
        return Constant.SUCCESS;
    }

    //删除指定用户下的所有邮件收件人
    public void deleteUserNameAllToemails(String userName) {
        emailDao.deleteToEmailByuserName(userName);
    }
}
