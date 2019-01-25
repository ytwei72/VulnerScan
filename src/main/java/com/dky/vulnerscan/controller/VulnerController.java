package com.dky.vulnerscan.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dky.vulnerscan.service.VulnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.dky.vulnerscan.entity.PageBean;
import com.dky.vulnerscan.entity.Vulner;
import com.dky.vulnerscan.util.PageUtil;
import com.dky.vulnerscan.util.ResponseUtil;
import com.dky.vulnerscan.util.StringUtil;

@Controller
@RequestMapping("/vulner_manage/vulner")
public class VulnerController {

	@Autowired
	private VulnerService vulnerService;
	
	private ModelAndView returnview(String pageCode, String actionName, Vulner vulner, List<Vulner> vulnerList, String viewName) {
		ModelAndView mav=new ModelAndView();
		mav.addObject("pageCode", pageCode);
		mav.addObject("actionName",actionName);
		mav.addObject("vulner",vulner);
		mav.addObject("vulnerList", vulnerList);
		mav.setViewName(viewName);
		return mav;
	}

	@RequestMapping("/list")
	public ModelAndView list(@RequestParam(value="page",required=false)String page,Vulner s_vulner,HttpServletRequest request) {
		
		HttpSession session=request.getSession();
		if(StringUtil.isEmpty(page)){
			page="1";
			session.setAttribute("s_vulner", s_vulner);
		}else{
			s_vulner=(Vulner) session.getAttribute("s_vulner");
		}
		PageBean pageBean=new PageBean(Integer.parseInt(page),15);
		int total=0;
		List<Vulner> vulnerList=null;
		try {
			vulnerList=vulnerService.find(pageBean, s_vulner);
		} catch (Exception e){
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
		try {
			total=vulnerService.count(s_vulner);
			String pageCode=PageUtil.getPagation(request.getContextPath()+"/vulner_manage/vulner/list", total, Integer.parseInt(page), 15);
			return returnview(pageCode, "漏洞列表", null, vulnerList, "/vulner_manage/vulner/list");
		} catch (Exception e){
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
		
	}
	
	@RequestMapping("/detail")
	public ModelAndView detail(@RequestParam(value="vul_id",required=false)String vul_id){
		try {
            Vulner vulner = vulnerService.loadByVulid(vul_id);
            if (!"No".equals(vulner.getPoc_filepath())) {
                String poc_content=vulnerService.poc_code(vulner.getPoc_filepath());
                vulner.setPoc_content(poc_content);
            }
			return returnview(null, "漏洞详细信息", vulner,null, "/vulner_manage/vulner/detail");
		} catch (Exception e) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
	}
	
	@RequestMapping("/delete")
	public ModelAndView delete(@RequestParam(value="vul_id")String vul_id,HttpServletResponse response){
		JSONObject result=new JSONObject();
		Boolean state=vulnerService.delete(vul_id);
		result.put("state", state);
		if (state) {
			result.put("info", "删除成功！");
		} else {
			result.put("info", "删除失败！");
		}
		try {
			ResponseUtil.write(result, response);
			return null;
		} catch (Exception e) {
			return returnview(null, null, null, null,  "/vulner_manage/error/error");
		}
	}
	
	@RequestMapping("/preSave")
	public ModelAndView preSave(@RequestParam(value="vul_id", required=false)String vul_id){

		Vulner vulner=new Vulner();
		try {
            if (StringUtil.isNotEmpty(vul_id)) {
                vulner = vulnerService.loadByVulid(vul_id);
                vulner.setVul_time(vulner.getVul_time().substring(0, vulner.getVul_time().length()-2));
                vulner.setS_method("修改");//设置该值判断添加行为
                vulner.setS_value(vul_id);//用于修改漏洞信息，Dao层更新原漏洞ID的条件
                if (!"No".equals(vulner.getPoc_filepath())) {
                    String poc_content=vulnerService.poc_code(vulner.getPoc_filepath());
                    vulner.setPoc_content(poc_content);
                }
                return returnview(null, "漏洞修改", vulner, null, "/vulner_manage/vulner/save");
            } else {
                vulner.setS_method("添加");//设置该值判断添加行为
                return returnview(null, "漏洞添加", vulner, null, "/vulner_manage/vulner/save");
            }
        } catch (Exception e) {
            return returnview(null, null, null, null, "/vulner_manage/error/error");
        }
	}

	public void switch_vulner(Vulner vulner){
	    String first_type=vulner.getFirst_type();
        if ("视频监控设备".equals(first_type)) {
            vulner.setFirst_type("Monitor");
        } else if ("办公自动化设备".equals(first_type)) {
            vulner.setFirst_type("OA");
        } else if ("工业控制设备".equals(first_type)) {
            vulner.setFirst_type("ICS");
        } else if ("楼宇自动化系统".equals(first_type)) {
            vulner.setFirst_type("BAS");
        } else if ("智能家居系统".equals(first_type)) {
            vulner.setFirst_type("HA");
        } else if ("语音视频系统".equals(first_type)) {
            vulner.setFirst_type("Voice and Video");
        } else if ("网络设备".equals(first_type)) {
            vulner.setFirst_type("Network Equipment");
        } else if ("网络安全设备".equals(first_type)) {
            vulner.setFirst_type("Security");
        } else if ("机房布线设备".equals(first_type)) {
            vulner.setFirst_type("Room Wiring");
        }
    }

	@RequestMapping("/save")
	public ModelAndView save(@RequestParam(value="Filename") MultipartFile Filename, Vulner vulner, HttpServletResponse response){

        switch_vulner(vulner);
        JSONObject result=new JSONObject();
        JSONObject vul_status_json=new JSONObject();
		int vul_status=0;
		String vul_status_message=new String();

        if ("".equals(vulner.getVul_id()) || "".equals(vulner.getVul_name()) || "".equals(vulner.getVul_time()) ||
                "".equals(vulner.getDescription()) || "".equals(vulner.getFirst_type()) || "".equals(vulner.getAffect_brand())) {
            vul_status=-1;
            vul_status_message="请按要求编辑表单！必填字段值不能为空！";
        } else {
            //漏洞信息保存结果
            if ("添加".equals(vulner.getS_method())) {
                vul_status_json = vulnerService.add(vulner);
            } else if ("修改".equals(vulner.getS_method())) {
                vul_status_json = vulnerService.update(vulner);
            }

            if (vul_status_json!=null) {
                vul_status = (int) vul_status_json.get("status");
                vul_status_message = (String) vul_status_json.get("message");
            }
        }
        if (vul_status==1) {

            Boolean need=false;
            if ("添加".equals(vulner.getS_method())) {
                need=true;
            } else if ("修改".equals(vulner.getS_method())) {
                if (Filename.getOriginalFilename().equals("") || Filename.isEmpty()) {
                    need=false;
                } else {
                    need=true;
                }
            }
            if (need==true) {
                //poc脚本的验证结果
                JSONObject poc_status_json = vulnerService.poc_file_process(vulner.getVul_id(), Filename);
                int poc_status = (int) poc_status_json.get("status");
                JSONArray poc_status_message = (JSONArray) poc_status_json.get("message");
                if (poc_status == 1) {
                    String poc_filepath = (String) poc_status_json.get("filepath");
                    vulner.setPoc_filepath(poc_filepath);

                    vulner.setS_value(vulner.getVul_id());
                    vul_status_json = vulnerService.update(vulner);
                } else {
                    vulner.setPoc_filepath("No");
                }

                //构造返回信息
                if (poc_status==1) {
                    result.put("state", 1);
                } else {
                    result.put("state", -1);
                }
                result.put("poc_message", poc_status_message);
            } else {
                result.put("state", 1);
            }


        } else {
            result.put("state", -1);
        }

        result.put("vul_message", vul_status_message);

		try {
			ResponseUtil.write(result, response);
			return null;
		} catch (Exception e) {
			return returnview(null, null, null, null, "/vulner_manage/error/error");
		}
	}
}
