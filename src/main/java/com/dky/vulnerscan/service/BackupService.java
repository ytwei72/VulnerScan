package com.dky.vulnerscan.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dky.vulnerscan.entity.Backup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.entity.PageBean;

@Service
public class BackupService {

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String pass;

    @Value("${spring.datasource.url}")
    private String jdbcurl;

    //备份文件夹中得到备份文件列表
    @SuppressWarnings("finally")
    public List<Backup> sub_find() {

        final List<Backup> backupList=new ArrayList<Backup>();
        String user_home = System.getProperty("user.home");
        String path=user_home+System.getProperty("file.separator")+".backdata";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String[] files = dir.list();

        for (String file : files) {

            File f = new File(path +System.getProperty("file.separator")+ file);
            if (!file.endsWith(".sql") || !file.contains("_")) {//不是要求的数据库备份文件
                try {
                    f.delete();
                } catch (Exception e) {}
                finally {
                    continue;
                }
            }
            String fileName = file.substring(0,file.lastIndexOf("."));//文件名部分
            //计算文件名中包含的"_"字符个数
            int count = fileName.lastIndexOf("_") - fileName.indexOf("_");
            if (count<2) {//不是要求的数据库备份文件
                try {
                    f.delete();
                } catch (Exception e) {}
                finally {
                    continue;
                }
            }
            String[] array = new String[count+1];//定义一个字符串数组，装载分割出的"count+1"个子字符串
            String[] strs = fileName.split("_");
            int i = 0;
            for (String str : strs) {
                array[i] = str;//将分割开的子字符串放入数组中
                i++;
            }
            if ("null".equals(array[0]))
            {
                //说明该文件文件名异常，也并非所要求的备份文件
                try {
                    f.delete();
                } catch (Exception e) {}
                finally {
                    continue;//不是要求的数据库备份文件
                }
            }
            else
            {
                Integer id = Integer.parseInt(array[0]);
                String time = array[1];
                String remark = array[2];
                //每次得到一条记录都是一个新对象，实例化以后加入列表中
                Backup re_backup = new Backup();
                re_backup.setId(id);
                re_backup.setTime(time);
                re_backup.setRemark(remark);
                backupList.add(re_backup);
            }
        }
        return backupList;
    }

    @SuppressWarnings("finally")
    public List<Backup> find(PageBean pageBean, Backup s_backup) {

        List<Backup> backupList=sub_find();
        final List<Backup> re_backupList=new ArrayList<Backup>();
        final List<Backup> T_backupList=new ArrayList<Backup>();
        int order=0;
        int size = 0, start = 0;
        if(pageBean!=null){
            size = pageBean.getPageSize();
            start = pageBean.getStart();
        }
        else {
            size = count(s_backup);
        }
        for (Backup backup : backupList) {

            String filename=backup.getId().toString()+"_"+backup.getTime()+"_"+backup.getRemark();
            if (s_backup.getTime()!=null) {
                if (filename.indexOf(s_backup.getTime())==-1) {//成立说明不包含查询内容，即不在文件夹中
                    continue;
                }
            }
            T_backupList.add(backup);
        }
        //得到查询所选页面内的记录（从start开始到start+size）
        for (Backup t_backup : T_backupList) {
            order++;
            if (order > start && order <= start+size) {
                re_backupList.add(t_backup);
            }
        }
        return re_backupList;
    }

    public int count(Backup s_backup) {

        int num=0;
        List<Backup> backupList = sub_find();
        for (Backup backup : backupList) {
            String filename=backup.getId().toString()+"_"+backup.getTime()+"_"+backup.getRemark();
            if (s_backup.getTime()!=null) {
                if (filename.indexOf(s_backup.getTime())==-1) {//成立说明不包含查询内容，即不在文件夹中
                    continue;
                }
            }
            num++;
        }
        return num;
    }

    public Boolean add_backup(Backup backup) {

        String[] array = new String[4];
        int i=0;
        StringTokenizer token = new StringTokenizer(jdbcurl, ":");
        while (token.hasMoreTokens()) {
            array[i] = token.nextToken();//将分割开的子字符串放入数组中
            i++;
        }
        String url_ip = array[2].substring(array[2].lastIndexOf("/")+1,array[2].length());
        String url_port = array[3].substring(0,array[3].indexOf("/"));
        String database = "";
        if (array[3].indexOf("?")==-1) {
            database=array[3].substring(array[3].indexOf("/")+1,array[3].length());
        } else {
            database=array[3].substring(array[3].indexOf("/")+1,array[3].indexOf("?"));
        }
        String command_path="mysqldump";
        String OS = System.getProperty("os.name").toLowerCase();
        String user_home = System.getProperty("user.home");
        String path=user_home+System.getProperty("file.separator")+".backdata";
        if (OS.indexOf("linux")>=0){
            command_path="mysqldump";
        }
        else if (OS.indexOf("windows")>=0){
            command_path="\"C:\\Program Files\\MySQL\\MySQL Workbench 6.3 CE\\mysqldump.exe\"";
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filepath=path+System.getProperty("file.separator")+backup.getId().toString()+"_"+backup.getTime()+"_"+backup.getRemark()+".sql";
        String backup_command = command_path+ " --user="+user +
                " --password="+ pass +" --host="+url_ip+" --protocol=tcp --port="+url_port+" --result-file="+filepath+
                " --default-character-set=utf8 --routines --events --triggers "+database+" vulnerability";

        String[] cmd = { "/bin/sh", "-c", backup_command };
        cmd[0]="";
        if (OS.indexOf("linux")>=0){
            cmd[0]="/bin/sh";
            cmd[1]="-c";
        }
        else if (OS.indexOf("windows")>=0){
            cmd[0]="cmd";
            cmd[1]="/c";
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process =
                    runtime.exec(cmd);

            int exitVal = process.waitFor();
            if (exitVal==0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean recover(Backup backup){

        String[] array = new String[4];
        int i=0;
        StringTokenizer token = new StringTokenizer(jdbcurl, ":");
        while (token.hasMoreTokens()) {
            array[i] = token.nextToken();//将分割开的子字符串放入数组中
            i++;
        }
        String url_ip = array[2].substring(array[2].lastIndexOf("/")+1,array[2].length());
        String url_port = array[3].substring(0,array[3].indexOf("/"));
        String database = "";
        if (array[3].indexOf("?")==-1) {
            database=array[3].substring(array[3].indexOf("/")+1,array[3].length());
        } else {
            database=array[3].substring(array[3].indexOf("/")+1,array[3].indexOf("?"));
        }

        String command_path="mysql";
        String OS = System.getProperty("os.name").toLowerCase();
        String user_home = System.getProperty("user.home");
        String path=user_home+System.getProperty("file.separator")+".backdata";
        if (OS.indexOf("linux")>=0){
            command_path="mysql";
        }
        else if (OS.indexOf("windows")>=0){
            command_path="\"C:\\Program Files\\MySQL\\MySQL Workbench 6.3 CE\\mysql.exe\"";
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filename = path + System.getProperty("file.separator")+backup.getId().toString()+"_"+
                backup.getTime()+"_"+backup.getRemark()+".sql";

        if (filename == "") {
            return false;
        }
        String recover_command = command_path+" -u "+
                user+" --password="+pass+" --host="+url_ip+" --protocol=tcp --port="+url_port+
                " --default-character-set=utf8 --comments -D"+database+" < "  + filename;

        String[] cmd = { "/bin/sh", "-c", recover_command };
        cmd[0]="";
        if (OS.indexOf("linux")>=0){
            cmd[0]="/bin/sh";
            cmd[1]="-c";
        }
        else if (OS.indexOf("windows")>=0){
            cmd[0]="cmd";
            cmd[1]="/c";
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc =
                    runtime.exec(cmd);

            int exitVal = proc.waitFor();
            if (exitVal==0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean delete(Backup backup) {

        String OS = System.getProperty("os.name").toLowerCase();
        String user_home = System.getProperty("user.home");
        String path=user_home+System.getProperty("file.separator")+".backdata";
        String filename = path + System.getProperty("file.separator")+backup.getId().toString()+"_"+backup.getTime()+"_"+backup.getRemark()+".sql";
        try {
            File f = new File(filename);
            boolean result = f.delete();
            System.out.println(result+"\n");
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public Backup getbackupByNum() {

        List<Backup> backupList = sub_find();
        int filenum =backupList.size();
        Backup backup = new Backup();
        backup.setId(filenum+1);
        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String datetime = tempDate.format(new java.util.Date());
        backup.setTime(datetime);
        return backup;
    }

}
