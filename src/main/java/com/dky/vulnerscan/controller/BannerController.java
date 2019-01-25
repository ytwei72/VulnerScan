package com.dky.vulnerscan.controller;

import com.dky.vulnerscan.entityview.Banner;
import com.dky.vulnerscan.entityview.PageNation;
import com.dky.vulnerscan.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bo on 2017/4/7.
 */
@Controller
public class BannerController {
    @Autowired
    private BannerService bannerService;

    //加载Banner页面
    @RequestMapping(value = "/banner")
    public String bannerPage() {
        return "/banner/banner";
    }

    @RequestMapping(value = "/addBanner", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addBanner(@RequestParam(value = "bannerFile") MultipartFile bannerFile, Banner banner) {
        return bannerService.addBanner(bannerFile, banner);
    }

    @RequestMapping(value = "/bannerList")
    @ResponseBody
    public Map<String, Object> listBanner(@RequestParam("page") int page, @RequestParam("perPage") int perPage) {
        Map<String, Object> modelMap = new HashMap<>();
        PageNation pageNation = bannerService.getPageNation(page, perPage);
        List<Banner> bannerList = bannerService.getPageBanner(page, perPage);
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "");
        modelMap.put("pagenation", pageNation);
        modelMap.put("bannerList", bannerList);
        return modelMap;
    }

    @RequestMapping(value = "/deleteBanner")
    @ResponseBody
    public Map<String, Object> deleteBanner(@RequestParam("id") int id) {
        Map<String, Object> modelMap = new HashMap<>();
        int flag = bannerService.deleteBannerById(id);
        if (flag > 0) {
            modelMap.put("bizNo", 1);
            modelMap.put("bizMsg", "删除成功");
        } else {
            modelMap.put("bizNo", -1);
            modelMap.put("bizMsg", "删除失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/updateBanner")
    @ResponseBody
    public Map<String, Object> updateBanner(@RequestParam(value = "bannerFile",required = false) MultipartFile bannerFile, Banner banner) {
        Map<String, Object> modelMap = new HashMap<>();
        int flag = bannerService.updateBanner(bannerFile, banner);
        if (flag > 0) {
            modelMap.put("bizNo", 1);
            modelMap.put("bizMsg", "更新成功");
        } else {
            modelMap.put("bizNo", -1);
            modelMap.put("bizMsg", "更新失败");
        }
        return modelMap;
    }

}
