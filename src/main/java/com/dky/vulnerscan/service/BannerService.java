package com.dky.vulnerscan.service;

import com.dky.vulnerscan.dao.ProbeConfigDao;
import com.dky.vulnerscan.dao.UserBannerDao;
import com.dky.vulnerscan.entity.ProbeConfig;
import com.dky.vulnerscan.entityview.Banner;
import com.dky.vulnerscan.entityview.PageNation;
import com.dky.vulnerscan.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bo on 2017/4/7.
 */
@Service
public class BannerService extends BaseService {
    @Autowired
    private UserBannerDao userBannerDao;
    @Autowired
    private ProbeConfigDao probeConfigDao;

    //指纹探测包文件的路径
    private final String bannerPath = "/root/CyberPecker/DeviceRecognition/req/";

    //获取分页信息
    public PageNation getPageNation(int page, int perPage) {
        int allNum = userBannerDao.countBanner();
        return super.getPageNation(allNum, page, perPage);
    }

    //添加指纹
    public Map<String, Object> addBanner(MultipartFile bannerFile, Banner banner) {
        Map<String, Object> modelMap = new HashMap<>();
        String fileName;
        if (banner.getProtocol() != null) {
            fileName = banner.getProtocol() + "-req";
            banner.setFileName(fileName);
        } else {
            modelMap.put("bizNo", -1);
            modelMap.put("bizMsg", "探测协议不能为空");
            return modelMap;
        }
        if (banner.getPort() != 0) {
            int count = probeConfigDao.countSectionNum(banner.getPort() + "-" + banner.getProtocol());
            if (count > 0) {
                modelMap.put("bizNo", -1);
                modelMap.put("bizMsg", "探测协议已经存在");
                return modelMap;
            }
            //上传指纹探测包到指定的目录下
            if (!bannerFile.isEmpty()) {
                try {
                    bannerFile.transferTo(new File(bannerPath + fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                    modelMap.put("bizNo", -1);
                    modelMap.put("bizMsg", "添加指纹失败");
                    return modelMap;
                }
            } else {
                modelMap.put("bizNo", -1);
                modelMap.put("bizMsg", "指纹探测包不能为空");
                return modelMap;
            }
            Timestamp addTime = new Timestamp(System.currentTimeMillis());
            banner.setAddTime(addTime);
            //添加到数据库中
            int addBannerFlag = userBannerDao.addBanner(banner);

            ProbeConfig probeConfig = new ProbeConfig();
            probeConfig.setSection(banner.getPort() + "-" + banner.getProtocol());
            probeConfig.setType(banner.getType());
            probeConfig.setPort(banner.getPort());
            probeConfig.setReq(fileName);
            probeConfig.setEncoding(banner.getEncoding());
            probeConfig.setZgrabDefault("");
            int addConfigFlag = probeConfigDao.addProbeConfig(probeConfig);

            if (addBannerFlag < 0 || addConfigFlag < 0) {
                modelMap.put("bizNo", -1);
                modelMap.put("bizMsg", "添加指纹失败");
                return modelMap;
            }
        } else {
            modelMap.put("bizNo", -1);
            modelMap.put("bizMsg", "探测端口不能为空");
            return modelMap;
        }
        modelMap.put("bizNo", 1);
        modelMap.put("bizMsg", "添加指纹成功");
        return modelMap;
    }

    //列出所有的指纹
    public List<Banner> getPageBanner(int page, int perPage) {
        List<Banner> bannerList = new ArrayList<>();
        int begin = (page - 1) * page;
        int offset = perPage;
        List<Banner> list = userBannerDao.getPageBanner(begin, offset);
        if (list != null) {
            bannerList = list;
        }
        return bannerList;
    }

    //删除指纹
    public int deleteBannerById(int id) {
        Banner banner = userBannerDao.getBannerById(id);
        if (banner == null) {
            return Constant.FAIL;
        }
        int delConfigFlag = -1, delBannerFlag = -1;
        delBannerFlag = userBannerDao.deleteBannerById(id);
        if (banner.getPort() != 0 && banner.getProtocol() != null) {
            delConfigFlag = probeConfigDao.deleteProbeConfig(banner.getPort() + "-" + banner.getProtocol());
        }
        if (delBannerFlag < 0 || delConfigFlag < 0) {
            return Constant.FAIL;
        }
        //删除文件
        File file = new File(bannerPath + banner.getProtocol() + "-req");
        if (file.exists()) {
            file.delete();
        }
        return Constant.SUCCESS;
    }

    //更新bannner
    public int updateBanner(MultipartFile bannerFile, Banner banner) {
        int updateFlag = -1;
        if (banner.getProtocol() == null) {
            return Constant.FAIL;
        }
        Banner tmpBanner = userBannerDao.getBannerById(banner.getId());
        if (tmpBanner == null) {
            return Constant.FAIL;
        }
        String fileName = banner.getProtocol() + "-req";
        banner.setFileName(fileName);
        if (bannerFile != null) {
            if (!bannerFile.isEmpty()) {
                try {
                    bannerFile.transferTo(new File(bannerPath + fileName));
                } catch (IOException e) {
                    return Constant.FAIL;
                }
            }
        }
        updateFlag = userBannerDao.updateBanner(banner);
        if (updateFlag < 0) {
            return Constant.FAIL;
        }
        return Constant.SUCCESS;
    }

}
