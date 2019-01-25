package com.dky.vulnerscan.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.util.MailSSLSocketFactory;

public class SendEmailUtil {

    private SimpleMail simpleMail;
    private boolean flag;

    public SendEmailUtil(SimpleMail sm) {
        setSimpleMail(sm);
        setFlag(false);
    }

    // 获得邮件服务器环境信息
    private Session getSession() {
        Properties mailProps = new Properties();
        if (simpleMail.getFrom().endsWith("qq.com")) {
                mailProps.put("mail.smtp.ssl.enable", "true"); // ssl加密
                mailProps.put("mail.smtp.port", 465);
        } else {
            mailProps.put("mail.smtp.port", 25);
        }
        mailProps.put("mail.smtp.auth", "true");// 向SMTP服务器提交用户认证
        mailProps.put("mail.transport.protocol", simpleMail.getProtocol());// 指定发送邮件协议
        mailProps.put("mail.host", simpleMail.getHost());// SMTP服务器主机地址
        Session session = Session.getDefaultInstance(mailProps,
                new MyAuthenticator(simpleMail.getUsername(), simpleMail.getPassword())); // 拿session的时候传入Authenticator子类进行验证
        return session;
    }

    // 获得邮件信息
    private MimeMessage getTextMessage() {
        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(simpleMail.getFrom()));// 发送方邮件地址
            String to = simpleMail.getTo();
            String[] toArr = to.split(",");
            InternetAddress[] address = new InternetAddress[toArr.length];
            for (int i = 0; i < toArr.length; i++) {
                address[i] = new InternetAddress(toArr[i]);
            }
            message.setRecipients(RecipientType.TO, address); // 接收方邮件地址
            message.setSubject(simpleMail.getSubject());
            message.setSentDate(new Date());// 发送时间
            MimeMultipart mmp = new MimeMultipart("mixed");// MIME消息头组合类型是mixed(html+附件)
            MimeBodyPart conBodyPart = new MimeBodyPart();
            conBodyPart.setContent(simpleMail.getContent(), "text/html;charset=utf-8");
            mmp.addBodyPart(conBodyPart);
            if (simpleMail.getTaskReportPath() != null && simpleMail.getTaskReportPath().length() > 0) { // 附件
                MimeBodyPart attachedBodyPart = getAttachedBodyPart(simpleMail.getTaskReportPath());
                mmp.addBodyPart(attachedBodyPart);
            }
            message.setContent(mmp); // 发送内容
            message.saveChanges();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return message;
    }

    // 处理附件
    private static String doHandlerFileName(String filePath) {
        String fileName = filePath;
        if (null != filePath && !"".equals(filePath)) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    // 带有附件
    private static MimeBodyPart getAttachedBodyPart(String filePath)
            throws MessagingException, UnsupportedEncodingException {
        MimeBodyPart attached = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(filePath);
        attached.setDataHandler(new DataHandler(fds));
        String fileName = doHandlerFileName(filePath);
        attached.setFileName(MimeUtility.encodeWord(fileName));// 处理附件文件的中文名问题
        return attached;
    }

    // 发邮件
    public boolean sendEmail() {
        Transport transport;
        try {
            transport = getSession().getTransport();
            transport.connect(); // 建立与指定的SMTP服务器的连接
            MimeMessage message = getTextMessage();
            transport.sendMessage(message, message.getRecipients(RecipientType.TO));// 发给所有指定的收件人
            transport.close();// 关闭连接
            setFlag(true);
            return flag;
        } catch (MessagingException e) {
            e.printStackTrace();
            return flag;
        }
    }

    public SimpleMail getSimpleMail() {
        return simpleMail;
    }

    public void setSimpleMail(SimpleMail simpleMail) {
        this.simpleMail = simpleMail;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
