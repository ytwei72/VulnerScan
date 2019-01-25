package com.dky.vulnerscan.controller;

import java.io.*;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.dky.vulnerscan.entity.PageBean;
import com.dky.vulnerscan.entity.Backup;
import com.dky.vulnerscan.service.BackupService;
import com.dky.vulnerscan.util.PageUtil;
import com.dky.vulnerscan.util.ResponseUtil;
import com.dky.vulnerscan.util.StringUtil;

@RestController
@RequestMapping("/vulner_manage/backup")
public class BackupController {
	
	@Resource
	private BackupService backupService;
	
	private ModelAndView returnview(String pageCode, String actionName, Backup backup, List<Backup> backupList, String viewName) {
		ModelAndView mav=new ModelAndView();
		mav.addObject("pageCode", pageCode);
		mav.addObject("actionName", actionName);
		mav.addObject("backup",backup);
		mav.addObject("backupList", backupList);
		mav.setViewName(viewName);
		return mav;
	}
	
	@RequestMapping("/list")
	public ModelAndView list(@RequestParam(value="page",required=false)String page,Backup s_backup,HttpServletRequest request){
		
		HttpSession session=request.getSession();
		if(StringUtil.isEmpty(page)){
			page="1";
			session.setAttribute("s_backup", s_backup);
		}else{
			s_backup=(Backup) session.getAttribute("s_backup");
		}
		try {
			PageBean pageBean=new PageBean(Integer.parseInt(page),15);
			List<Backup> backupList=backupService.find(pageBean, s_backup);
			int total=backupService.count(s_backup);
			String pageCode=PageUtil.getPagation(request.getContextPath()+"/vulner_manage/backup/list", total, Integer.parseInt(page), 15);
			return returnview(pageCode, "备份列表", null, backupList, "/vulner_manage/backup/list");
		} catch (Exception e){
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
		
	}

	@RequestMapping("/recover")
	public ModelAndView recover(@RequestParam(value="id")String id, @RequestParam(value="time")String time, @RequestParam(value="remark")String remark, HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		Backup backup=new Backup(Integer.parseInt(id), time, remark);
		Boolean state=backupService.recover(backup);
		result.put("state", state);
		if (state) {
			result.put("info", "恢复数据成功！");
		} else {
			result.put("info", "恢复数据失败！");
		}
		try {
			ResponseUtil.write(result, response);
			return null;
		} catch (Exception e) {
			return returnview(null, null, null, null,  "/vulner_manage/error/error");
		}
	}
	
	@RequestMapping("/export")
	public ModelAndView export(@RequestParam(value="id")String id, @RequestParam(value="time")String time, @RequestParam(value="remark")String remark, HttpServletRequest request, HttpServletResponse response){

		try {
			response.setContentType("text/html;charset=utf-8");
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		String code = "UTF-8";
		String user_home = System.getProperty("user.home");
        String ctxPath=user_home+System.getProperty("file.separator")+".backdata";
		try {
			if (remark.equals(new String(remark.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
				code = "ISO-8859-1";
			}
			else if (remark.equals(new String(remark.getBytes("UTF-8"), "UTF-8"))) {
				code = "UTF-8";
			}
			else if (remark.equals(new String(remark.getBytes("GBK"), "GBK"))) {
				code = "GBK";
			}
            String filename = id+"_"+time+"_"+remark+".sql";
            String FileName = new String(filename.getBytes(code), "UTF-8");
            String filepath = ctxPath + System.getProperty("file.separator")+FileName;
			long fileLength = new File(filepath).length();
			response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(FileName.getBytes(),"ISO-8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            try {
                bis = new BufferedInputStream(new FileInputStream(filepath));
            } catch (Exception e) {
                e.printStackTrace();
            }

			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
            int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
			}
		} catch (Exception exception1) {
			return returnview(null, null, null, null,  "/vulner_manage/error/error");
		} finally {
			if (bis != null || bos != null)
				try {
					bis.close();
					bos.close();
				} catch (IOException e) {
					return null;
				}
		}

		return null;
	
	}

	@RequestMapping("/delete")
	public ModelAndView delete(@RequestParam(value="id")String id, @RequestParam(value="time")String time, @RequestParam(value="remark")String remark, HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		Backup backup=new Backup(Integer.parseInt(id), time, remark);
		Boolean state=backupService.delete(backup);
		result.put("state", state);
		if (state) {
			result.put("info", "删除备份记录成功！");
		} else {
			result.put("info", "删除备份记录失败！");
		}
		try {
			ResponseUtil.write(result, response);
			return null;
		} catch (Exception e) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
	}
	
	@RequestMapping("/preUpload")
	public ModelAndView preUpload(){
		Backup backup = backupService.getbackupByNum();
		return returnview(null, "本地恢复", backup, null, "/vulner_manage/backup/upload");
	}

	@RequestMapping("/file_upload")
	public ModelAndView file_upload(@RequestParam(value="Filename") MultipartFile Filename, Backup backup, HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		Boolean state=false;
        String user_home = System.getProperty("user.home");
		String uploadUrl=user_home+System.getProperty("file.separator")+".backdata";
		String fname = backup.getId().toString()+"_"+backup.getTime()+"_"+backup.getRemark()+".sql";
		File dir = new File(uploadUrl);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File targetFile = new File(uploadUrl +System.getProperty("file.separator")+ fname);
		if (!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				return returnview(null, null, null, null, "/vulner_manage/error/error");
			}
		}
		
		try {
			Filename.transferTo(targetFile);
			state=true;
		} catch (Exception e) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		} finally {
			result.put("state", state);
			if (state) {
				result.put("info", "上传本地备份文件成功！");
			} else {
				result.put("info", "上传本地备份文件失败！");
			}
			try {
				ResponseUtil.write(result, response);
			} catch (Exception e) {
				return returnview(null, null, null, null, "/vulner_manage/error/error");
			}
		}
		return null;
	}
	
	@RequestMapping("/pre_add_backup")
	public ModelAndView pre_add_backup(){
		Backup backup = backupService.getbackupByNum();
		return returnview(null, "新建备份", backup, null, "/vulner_manage/backup/add_backup");
	}
	
	@RequestMapping("/add_backup")
	public ModelAndView add_backup(Backup backup,HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		Boolean state=backupService.add_backup(backup);
		result.put("state", state);
		if (state) {
			result.put("info", "添加备份成功！");
		} else {
			result.put("info", "添加备份失败！");
		}
		try {
			ResponseUtil.write(result, response);
			return null;
		} catch (Exception e) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
	}
	
}
