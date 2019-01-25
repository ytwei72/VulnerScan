package com.dky.vulnerscan.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dky.vulnerscan.dao.VulnerDao;
import com.dky.vulnerscan.entity.Vulner;
import com.dky.vulnerscan.util.Poc_Validate;
import com.dky.vulnerscan.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.entity.PageBean;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VulnerService{

    @Autowired
	private VulnerDao vulnerDao;

	public List<Vulner> find(PageBean pageBean, Vulner s_vulner) throws Exception {

        StringBuffer sb=new StringBuffer();
		if(s_vulner!=null){
			if(StringUtil.isNotEmpty(s_vulner.getS_value())){
				if("编号".equals(s_vulner.getS_method())){
                    sb.append(" where vul_id like '%"+s_vulner.getS_value()+"%'");
				}
				else if("名称".equals(s_vulner.getS_method())){
                    sb.append(" where vul_name like '%"+s_vulner.getS_value()+"%'");
				}
				else if("类型".equals(s_vulner.getS_method())){
                    sb.append(" where vul_type like '%"+s_vulner.getS_value()+"%'");
				}
				else if("时间".equals(s_vulner.getS_method())){
                    sb.append(" where vul_time like '%"+s_vulner.getS_value()+"%'");
				}
				else if("产品".equals(s_vulner.getS_method())){
                    sb.append(" where affect_product like '%"+s_vulner.getS_value()+"%'");
				}
                else if("等级".equals(s_vulner.getS_method())){
                    sb.append(" where risklevel like '%"+s_vulner.getS_value()+"%'");
                }
                else if("危害".equals(s_vulner.getS_method())){
                    sb.append(" where dangers like '%"+s_vulner.getS_value()+"%'");
                }
				else if("模糊".equals(s_vulner.getS_method()) || "请选择...".equals(s_vulner.getS_method())){
                    sb.append(" where vul_id like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_name like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_type like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_time like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or affect_product like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or risklevel like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or dangers like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or description like '%"+s_vulner.getS_value()+"%'");
				}
			}
		}

        if(pageBean!=null){
            sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
        }
        String SQL_extend=sb.toString();
        List<Vulner> vulnerList=vulnerDao.getVulnerList(SQL_extend);
		return vulnerList;
	}

	public int count(Vulner s_vulner) throws Exception {

        StringBuffer sb=new StringBuffer();
		if(s_vulner!=null){
			if(StringUtil.isNotEmpty(s_vulner.getS_value())){
                if("编号".equals(s_vulner.getS_method())){
                    sb.append(" where vul_id like '%"+s_vulner.getS_value()+"%'");
                }
                else if("名称".equals(s_vulner.getS_method())){
                    sb.append(" where vul_name like '%"+s_vulner.getS_value()+"%'");
                }
                else if("类型".equals(s_vulner.getS_method())){
                    sb.append(" where vul_type like '%"+s_vulner.getS_value()+"%'");
                }
                else if("时间".equals(s_vulner.getS_method())){
                    sb.append(" where vul_time like '%"+s_vulner.getS_value()+"%'");
                }
                else if("产品".equals(s_vulner.getS_method())){
                    sb.append(" where affect_product like '%"+s_vulner.getS_value()+"%'");
                }
                else if("等级".equals(s_vulner.getS_method())){
                    sb.append(" where risklevel like '%"+s_vulner.getS_value()+"%'");
                }
                else if("危害".equals(s_vulner.getS_method())){
                    sb.append(" where dangers like '%"+s_vulner.getS_value()+"%'");
                }
                else if("模糊".equals(s_vulner.getS_method()) || "请选择...".equals(s_vulner.getS_method())){
                    sb.append(" where vul_id like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_name like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_type like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or vul_time like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or affect_product like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or risklevel like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or dangers like '%"+s_vulner.getS_value()+"%'");
                    sb.append(" or description like '%"+s_vulner.getS_value()+"%'");
                }
			}
		}
        String SQL_extend=sb.toString();
        int count = vulnerDao.getVulnerCount(SQL_extend);
		return count;
	}

	public Boolean delete(String vul_id) {

		try {
			vulnerDao.deleteVulner(vul_id);

            String user_home = System.getProperty("user.home");
            String path=user_home+System.getProperty("file.separator")+".vulner_poc";
            File dir = new File(path);
            if (dir.exists()) {
                String[] files = dir.list();
                for (String file : files) {
                    if (file.equals(vul_id.replace("-", "_")+".py")) {
                        File f = new File(path+System.getProperty("file.separator")+file);
                        f.delete();
                    }
                }
            }
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public JSONObject add(Vulner vulner) {

	    JSONObject result=new JSONObject();
        result.put("status", -1);
        result.put("message", "更新失败！操作发生异常，请重试或联系维护人员！");

		try {
		    if (vulnerDao.getVulnerbyvulid(vulner.getVul_id())!=null) {
                result.put("status", -1);
                result.put("message", "添加失败！漏洞编号已使用！");
            } else {
                vulnerDao.insertVulner(vulner);
                result.put("status", 1);
                result.put("message", "漏洞信息添加成功！");
            }
        } catch (Exception e) {
            System.out.println(e);} finally {
		    return result;
        }
	}

	public JSONObject update(Vulner vulner) {

        JSONObject result=new JSONObject();
        result.put("status", -1);
        result.put("message", "更新失败！操作发生异常，请重试或联系维护人员！");

        if (!vulner.getVul_id().equals(vulner.getS_value())) {
            String user_home = System.getProperty("user.home");
            String path=user_home+System.getProperty("file.separator")+".vulner_poc";
            File dir = new File(path);
            if (dir.exists()) {
                String[] files = dir.list();
                for (String file : files) {
                    if (file.equals(vulner.getS_value().replace("-", "_")+".py")) {
                        File f = new File(path+System.getProperty("file.separator")+file);
                        String poc_filepath=path+System.getProperty("file.separator")+vulner.getVul_id().replace("-", "_")+".py";
                        f.renameTo(new File(poc_filepath));
                        vulner.setPoc_filepath(poc_filepath);

                        try {
                            InputStream ins = new FileInputStream(poc_filepath);
                            byte[] contentByte = new byte[ins.available()];
                            ins.read(contentByte);
                            String poc_content = new String(contentByte);
                            ins.close();
                            String content = poc_content.replace(vulner.getS_value().replace("-", "_"), vulner.getVul_id().replace("-", "_"));

                            FileWriter fw = new FileWriter(new File(poc_filepath));
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write(content);
                            bw.close();

                        } catch (Exception e) {
                            return result;
                        }
                    }
                }
            }
            else {
                return result;
            }
        }
        try {
            if (vulnerDao.getVulnerbyvulid(vulner.getVul_id())!=null && !vulner.getVul_id().equals(vulner.getS_value())) {
                result.put("status", -1);
                result.put("message", "更新失败！漏洞编号已使用！");
            } else {
                vulnerDao.updateVulner(vulner);
                result.put("status", 1);
                result.put("message", "漏洞信息更新成功！");
            }
        } catch (Exception e) {
            System.out.println(e);} finally {
            return result;
        }
	}

	public String poc_code(String poc_filepath) {

	    String poc_content="";
        try {
            InputStream ins = new FileInputStream(poc_filepath);
            byte[] contentByte = new byte[ins.available()];
            ins.read(contentByte);
            poc_content = new String(contentByte);
            ins.close();
        } catch (Exception e) {}
        finally {
            return poc_content;
        }
    }
    public JSONObject poc_file_process(String vul_id, MultipartFile Filename) {

        JSONObject result_json=new JSONObject();
        JSONObject vali_json=new JSONObject();
        JSONObject poc_json=new JSONObject();
        JSONArray poc_message = new JSONArray();
        result_json.put("status", -1);

        if (Filename.isEmpty()) {
            poc_message.add("未上传验证程序脚本文件！");
        } else {
            //取得上传文件的名称
            String Ori_Name = Filename.getOriginalFilename();
            //如果名称不为“”,说明该文件存在，否则说明该文件不存在
            if (!Ori_Name.trim().equals(vul_id.replace("-", "_")+".py")) {
                poc_message.add("验证程序脚本文件必须是以漏洞编号命名加\".py\"结尾的文件，请检查文件名！");
            }
            else {
                List<String> str_line=new ArrayList<String>();
                String str = null;
                try {
                    InputStream ins = Filename.getInputStream();
                    InputStreamReader reader = new InputStreamReader(ins,"UTF-8");
                    BufferedReader br = new BufferedReader(new InputStreamReader(ins,"UTF-8"));
                    while((str = br.readLine()) != null) {
                        str_line.add(str);
                    }
                    ins.close();
                    reader.close();
                    br.close();
                } catch (Exception e) {}

                //调用接口开始验证poc
                vali_json = Poc_Validate.poc_validate(str_line);
                poc_json=Poc_Validate.poc_status_message(vali_json, vul_id);
                int poc_status= (int) poc_json.get("status");
                poc_message = (JSONArray) poc_json.get("message");
                if (poc_status==1) {
                    String user_home = System.getProperty("user.home");
                    String uploadUrl=user_home+System.getProperty("file.separator")+".vulner_poc";
                    File dir = new File(uploadUrl);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String fname = vul_id.replace("-", "_")+".py";
                    File targetFile = new File(uploadUrl +System.getProperty("file.separator")+ fname);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    try {
                        targetFile.createNewFile();
                        Filename.transferTo(targetFile);
                        result_json.put("status", 1);
                        result_json.put("filepath", uploadUrl +System.getProperty("file.separator")+ fname);
                    } catch (IOException e) {
                        result_json.put("status", -1);
                    }
                } else {
                    result_json.put("status", -1);
                }

            }
        }

        result_json.put("message",poc_message);
        return result_json;
    }

	public Vulner loadByVulid(String vul_id) throws Exception{

        return vulnerDao.getVulnerbyvulid(vul_id);
	}

}
