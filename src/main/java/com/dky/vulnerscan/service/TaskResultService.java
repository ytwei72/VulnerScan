package com.dky.vulnerscan.service;

import java.util.*;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dky.vulnerscan.entity.*;
import com.dky.vulnerscan.entityview.*;
import com.dky.vulnerscan.module.ExploitScanModule;
import com.dky.vulnerscan.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dky.vulnerscan.dao.TaskResultDao;


@Service
public class TaskResultService extends BaseService {
    @Autowired
    private TaskResultDao taskResultDao;
    @Autowired
    private VulService vulService;
    private PageNation pageNation;
    @Autowired
    private ExploitScanModule vulDeepScanModule;

    public PageNation getPageNation(int taskID, int projectID, int page, int perPage) {
        return pageNation;
    }

    // 删除指定项目对应的所有任务结果
    public void deleteProjectTaskResult(int projectID) {
        taskResultDao.deleteProjectTaskResult(projectID);
    }

    // 根据控制子软件的发送的任务执行结果，保存到数据库
    public void saveTaskResult(int taskid, int projectid, String process, JSONObject ipJson) {
        try {
            String ip = ipJson.getString("ip");
            String ipInfo = addField(process, ipJson);//添加web后台所需的私有字段
            taskResultDao.saveTaskResult(taskid, projectid, ip, ipInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //在ipJson中添加漏洞是否修复字段fixed(初始值为-1)以及漏洞的信息字段
    private String addField(String process, JSONObject ipInfoJson) throws JSONException {
        if (process.equals("VS")) { // 漏洞验证结果中添加漏洞信息
            if (ipInfoJson.get("onvif") != null) {
                JSONArray onvifVulArray = ipInfoJson.getJSONObject("onvif").getJSONArray("vul_list");
                addVulInfo(onvifVulArray);
            }
            if (ipInfoJson.get("port_list") != null) {
                JSONArray portInfoArray = ipInfoJson.getJSONArray("port_list");
                for (int i = 0; i < portInfoArray.size(); i++) {
                    JSONArray portVulArray = portInfoArray.getJSONObject(i).getJSONArray("vul_list");
                    addVulInfo(portVulArray);
                }
            }
        }
        return ipInfoJson.toString();
    }

    // 添加漏洞信息字段
    private void addVulInfo(JSONArray vulArray) throws JSONException {
        HashMap<String, VulInfo> systemVulInfoMap = vulService.getSystemVulInfo();//从系统漏洞库中加载漏洞信息
        HashMap<String, VulInfo> userVulInfoMap = vulService.getUserVulInfo(); //从用户漏洞库中加载漏洞信息
        for (int i = 0; i < vulArray.size(); i++) {
            JSONObject vulJsonObj = vulArray.getJSONObject(i);
            VulInfo vulInfo = getVulInfo(vulJsonObj.getString("id"), systemVulInfoMap, userVulInfoMap);
            vulJsonObj.put("priv_fixed", "-1");
            vulJsonObj.put("priv_vul_tech_type", vulInfo.getVulType());
            vulJsonObj.put("priv_vul_title", vulInfo.getVulTitle());
            vulJsonObj.put("priv_vul_affect", vulInfo.getVulAffect());
            vulJsonObj.put("priv_vul_level", vulInfo.getVulLevel());
            vulJsonObj.put("priv_vul_solution", vulInfo.getVulSolution());
            if(vulJsonObj.getIntValue("checked") == 1){
                vulJsonObj.put("priv_verify_state", "已验证");
            }else{
                vulJsonObj.put("priv_verify_state", "未验证");
            }
            vulJsonObj.put("priv_vul_source", "本地漏洞库");
        }
    }

    //根据加载的漏洞库，获取漏洞信息，如果用户库与系统库中都有这个漏洞，用户定义的漏洞信息优先
    private VulInfo getVulInfo(String vulID, HashMap<String, VulInfo> systemVulInfoMap, HashMap<String, VulInfo> userVulInfoMap) {
        if (userVulInfoMap.containsKey(vulID)) {
            return super.changeNullToEmptyString(userVulInfoMap.get(vulID));
        }
        if (systemVulInfoMap.containsKey(vulID)) {
            return super.changeNullToEmptyString(systemVulInfoMap.get(vulID));
        }
        VulInfo vulInfo = new VulInfo();
        vulInfo.setVulAffect("");
        vulInfo.setVulLevel("");
        vulInfo.setVulSolution("");
        vulInfo.setVulTitle("");
        vulInfo.setVulType("");
        return vulInfo;
    }

    // 获取每个任务的资产数
    public int getTaskPropertyNum(int taskID, int projectID) {
        return taskResultDao.getTaskPropertyNum(taskID, projectID);
    }

    // 统计每次任务的结果的漏洞信息
    public VulCount getTaskResultVulCount(int taskID, int projectID) {
        VulCount taskVulCount = new VulCount();
        // 查询结果完成序列化
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskID, projectID);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        for (String ipAddr : taskResultMap.keySet()) { // 遍历结果的每个设备
            IpInfo ipInfo = taskResultMap.get(ipAddr);
            if (ipInfo.getOnvif() != null && ipInfo.getPortList() != null) {
                VulCount devVulCount = super.countDevVul(ipInfo.getOnvif().getVulList(), ipInfo.getPortList());
                taskVulCount.setAllNum(taskVulCount.getAllNum() + devVulCount.getAllNum());
                taskVulCount.setExtremeHigh(taskVulCount.getExtremeHigh() + devVulCount.getExtremeHigh());
                taskVulCount.setHigh(taskVulCount.getHigh() + devVulCount.getHigh());
                taskVulCount.setMid(taskVulCount.getMid() + devVulCount.getMid());
                taskVulCount.setLow(taskVulCount.getLow() + devVulCount.getLow());
            }
        }
        return taskVulCount;
    }

    // 对任务结果的统计
    public TaskResultCount getTaskResultCount(TaskOverView taskOverView, int taskid, int projectid) {
        TaskResultCount resultCount = new TaskResultCount();
        HashMap<String, Integer> deviceTypeMap = new HashMap<>();
        HashMap<String, Integer> osTypeMap = new HashMap<>();
        HashMap<String, Integer> brandMap = new HashMap<>();
        HashMap<String, Integer> serviceTypeMap = new HashMap<>();
        HashMap<String, Integer> vulLevelMap = new HashMap<>();
        HashMap<String, Integer> vulTypeMap = new HashMap<>();
        HashMap<String, Integer> vulAffectMap = new HashMap<>();
        HashMap<String, Integer> vulDeviceMap = new HashMap<>();
        ArrayList<Param> brandList = new ArrayList<>();
        ArrayList<Param> serviceTypeList = new ArrayList<>();
        ArrayList<Param> vulTypeList = new ArrayList<>();
        ArrayList<Param> vulAffectList = new ArrayList<>();
        ArrayList<Param> osTypeList = new ArrayList<>();
        int noVulDevNum = 0, hasVulDevNum = 0;
        // 从数据库查询此次任务的结果到内存并完成序列化
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskid, projectid);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        for (String ipAddr : taskResultMap.keySet()) {
            boolean hasVulFlag = false;
            IpInfo ipInfo = taskResultMap.get(ipAddr);
            String os = "";
            if (ipInfo.getOs() == null || os.equals("")) { //操作系统的统计
                os = "未识别";
            }
            countNum(osTypeMap, os);
            if (ipInfo.getDeviceInfoSummary() != null) {// 设备类型、品牌的统计
                String devType = super.getDeviceType(ipInfo.getDeviceInfoSummary().getSecondDeviceType());
                if (devType.equals("摄像机") || devType.equals("NVR/DVR")) {
                }
                countNum(deviceTypeMap, devType);//设备类型、品牌统计
                countNum(brandMap, super.getMaxSimilarityDevType(ipInfo.getDeviceInfoSummary().getDeviceInfoList()).getBrand());
            } else {
                countNum(deviceTypeMap, "其它");
            }
            if (ipInfo.getOnvif() != null) { //统计onvif中的漏洞信息及服务信息
                if (ipInfo.getOnvif().getPort() != 0) {
                    countNum(serviceTypeMap, "onvif");
                    if (ipInfo.getOnvif().getVulList().size() > 0) {
                        hasVulFlag = true;
                    }
                    super.countTaskResultVulInfo(ipInfo.getOnvif().getVulList(), vulTypeMap, vulLevelMap, vulAffectMap);
                }
            }
            if (ipInfo.getPortList() != null) { // 统计端口中的漏洞及服务信息
                for (int i = 0; i < ipInfo.getPortList().size(); i++) {
                    PortInfo portInfo = ipInfo.getPortList().get(i);
                    if (portInfo.getService() != null) {
                        String serviceType = portInfo.getService().getType();
                        if (!serviceType.equals("") && !serviceType.equals("unknown")) {
                            countNum(serviceTypeMap, serviceType);
                        }
                    }
                    if (portInfo.getVulList() != null && portInfo.getVulList().size() > 0) {
                        hasVulFlag = true;
                        super.countTaskResultVulInfo(portInfo.getVulList(), vulTypeMap, vulLevelMap, vulAffectMap);
                    }
                }
            }
            if (hasVulFlag) {
                hasVulDevNum++;
            } else {
                noVulDevNum++;
            }
        }
        int videoNum = 0, vulNum = 0;
        if (deviceTypeMap.get("摄像机") != null) {
            videoNum += deviceTypeMap.get("摄像机");
        }
        if (deviceTypeMap.get("NVR/DVR") != null) {
            videoNum += deviceTypeMap.get("NVR/DVR");
        }
        for (String key : vulLevelMap.keySet()) {
            vulNum += vulLevelMap.get(key);
        }
        taskOverView.setVulNum(vulNum);
        taskOverView.setVideoNum(videoNum);
        vulDeviceMap.put("正常设备", noVulDevNum);
        vulDeviceMap.put("漏洞设备", hasVulDevNum);
        serviceTypeList = produceParam(serviceTypeMap);
        osTypeList = produceParam(osTypeMap);
        brandList = produceParam(brandMap);
        vulTypeList = produceParam(vulTypeMap);
        vulAffectList = produceParam(vulAffectMap);

        resultCount.setOsTypeList(osTypeList);
        resultCount.setServiceList(serviceTypeList);
        resultCount.setBrandList(brandList);
        resultCount.setDeviceTypeMap(deviceTypeMap);
        resultCount.setVulLevelMap(vulLevelMap);
        resultCount.setVulStyleList(vulTypeList);
        resultCount.setVulDangerList(vulAffectList);
        resultCount.setVulDeviceMap(vulDeviceMap);
        return resultCount;
    }

    // 构造参数
    private ArrayList<Param> produceParam(HashMap<String, Integer> map) {
        ArrayList<Param> numList = new ArrayList<>();
        // 将map.entrySet()转换成list
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            // 降序排序
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int count = 1;
        for (Map.Entry<String, Integer> mapping : list) {
            if (mapping.getKey().equals("unknown") || mapping.getKey().equals("")) {
                continue;
            }
            if (count > 12) {
                break;
            }
            Param param = new Param();
            param.setName(mapping.getKey());
            param.setValue(mapping.getValue());
            numList.add(param);
            count++;
        }
        return numList;
    }

    //排序函数
    private void sortResultCollection(ArrayList<DeviceDetaiInfo> deviceList, String sortKw, String sortStyle) {
        if (sortKw.equalsIgnoreCase("ip")) {
            if (sortStyle.equalsIgnoreCase("desc")) {
                Collections.sort(deviceList, ipComparator);
            } else {
                Collections.sort(deviceList);
            }
        } else {
            if (sortStyle.equalsIgnoreCase("desc")) {
                Collections.sort(deviceList, vulComparatorDesc);
            } else {
                Collections.sort(deviceList, vulComparatorAsc);
            }
        }
    }

    //搜索结果
    private boolean hasMatchKw(DeviceDetaiInfo devDetailInfo, String searchKw) {
        return (devDetailInfo.getIp().toLowerCase().contains(searchKw)
                || devDetailInfo.getStyle().toLowerCase().contains(searchKw)
                || devDetailInfo.getBrand().toLowerCase().contains(searchKw));
    }

    // 获取项目对应的任务的执行结果中设备信息
    public ArrayList<DeviceDetaiInfo> getDeviceList(int taskID, int projectID, int page, int perPage, String searchKw,
                                                    String sortKw, String sortStyle) {
        ArrayList<DeviceDetaiInfo> deviceList = new ArrayList<>();
        ArrayList<DeviceDetaiInfo> searchDeviceList = new ArrayList<>();
        // 从数据库查询此次任务的结果到内存并完成序列化
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskID, projectID);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        for (String ip : taskResultMap.keySet()) {
            String deviceType = "", brand = "";
            DeviceDetaiInfo deviceOverView = new DeviceDetaiInfo();
            VulCount devVulCount = new VulCount();
            int serviceCount = 0;
            IpInfo ipInfo = taskResultMap.get(ip);
            deviceOverView.setIp(ipInfo.getIp());
            if (ipInfo.getDeviceInfoSummary() != null) {
                deviceType = super.getDeviceType(ipInfo.getDeviceInfoSummary().getSecondDeviceType());
                brand = super.getMaxSimilarityDevType(ipInfo.getDeviceInfoSummary().getDeviceInfoList()).getBrand();
            }
            if (ipInfo.getOnvif() != null && ipInfo.getPortList() != null) {
                devVulCount = super.countDevVul(ipInfo.getOnvif().getVulList(), ipInfo.getPortList());
                serviceCount = super.countNumService(ipInfo.getOnvif(), ipInfo.getPortList());
            }
            deviceOverView.setStyle(deviceType);
            deviceOverView.setBrand(brand);
            deviceOverView.setVulCount(devVulCount);
            deviceOverView.setServiceCount(serviceCount);
            deviceList.add(deviceOverView);
        }
        sortResultCollection(deviceList, sortKw, sortStyle);//对结果集进行排序
        searchKw = searchKw.toLowerCase();
        if (searchKw.equals("")) {
            return page(deviceList,page,perPage);
        }
        for (DeviceDetaiInfo deviceDetailInfo : deviceList) {
            if (hasMatchKw(deviceDetailInfo, searchKw)) {
                searchDeviceList.add(deviceDetailInfo);
            }
        }
        return page(searchDeviceList,page,perPage);
    }

    //分页函数
    private ArrayList<DeviceDetaiInfo> page(ArrayList<DeviceDetaiInfo> tmpDeviceList,int page,int perPage){
        ArrayList<DeviceDetaiInfo> pageDeviceList = new ArrayList<>();
        int begin = (page - 1) * perPage;
        int offset = perPage;
        int allNum = tmpDeviceList.size();
        int count = 0;
        pageNation = super.getPageNation(allNum, page, perPage);
        while (count < offset && allNum > begin) {
            pageDeviceList.add(tmpDeviceList.get(begin++));
            count++;
        }
        return pageDeviceList;
    }

    //获取设备漏洞详细信息
    private void getDeviceVulDetail(ArrayList<VulInfo> vulInfoList, ArrayList<VulDetail> vulDetailList, String serviceType, String port) {
        for (int i = 0; i < vulInfoList.size(); i++) {
            VulDetail vulDetail = new VulDetail();
            VulInfo vulInfo = vulInfoList.get(i);
            vulDetail.setVulID(vulInfo.getId());
            vulDetail.setService(serviceType);
            vulDetail.setPort(port);
            vulDetail.setName(vulInfo.getVulTitle());
            vulDetail.setGrade(vulInfo.getVulLevel());
            vulDetail.setStyle(vulInfo.getVulType());
            vulDetail.setDanger(vulInfo.getVulAffect());
            vulDetail.setCheckedState(vulInfo.getVerifyState());
            vulDetail.setHasCheckedScript("0");
            vulDetail.setSource(vulInfo.getVulSource());
            String solution = vulInfo.getVulSolution();
            if (solution != null && !solution.equals("")) {
                String[] strSolution = solution.split("@");
                String temp = "";
                if (!strSolution[1].equals("无")) {
                    temp = strSolution[1] + "," + strSolution[0];
                } else {
                    temp = "暂无";
                }
                solution = temp;
            }
            vulDetail.setSolution(solution);
            vulDetailList.add(vulDetail);
        }
    }

    //统计exploit漏洞信息
    private void getDeviceExploitVulDetail(ArrayList<ExploitVul> exploitVulList, ArrayList<VulDetail> vulList) {
        for (int i = 0; i < exploitVulList.size(); i++) {
            VulDetail vulDetail = new VulDetail();
            vulDetail.setVulID("");
            vulDetail.setService("");
            vulDetail.setName(exploitVulList.get(i).getScriptName());
            vulDetail.setGrade(exploitVulList.get(i).getVulLevel());
            vulDetail.setStyle("");
            vulDetail.setDanger("");
            vulDetail.setHasCheckedScript("1");
            vulDetail.setSolution(exploitVulList.get(i).getVulSolution());
            vulDetail.setSource(exploitVulList.get(i).getVulSource());
            vulDetail.setCheckedState(exploitVulList.get(i).getVerifyState());
            vulList.add(vulDetail);
        }
    }

    //统计openVas漏洞信息
    private void getDeviceOpenVasVulDetail(ArrayList<Openvas> openvasVulArrayList, ArrayList<VulDetail> vulList) {
        for (int i = 0; i < openvasVulArrayList.size(); i++) {
            VulDetail vulDetail = new VulDetail();
            vulDetail.setVulID("");
            vulDetail.setService(openvasVulArrayList.get(i).getName());
            vulDetail.setName(openvasVulArrayList.get(i).getNvt());
            vulDetail.setGrade(openvasVulArrayList.get(i).getThreat());
            vulDetail.setStyle("");
            vulDetail.setSolution("暂无");
            vulDetail.setPort(openvasVulArrayList.get(i).getPort());
            vulDetail.setDanger("");
            vulDetail.setHasCheckedScript("0");
            vulDetail.setCheckedState(openvasVulArrayList.get(i).getVerifyState());
            vulDetail.setSource(openvasVulArrayList.get(i).getVulSource());
            vulList.add(vulDetail);
        }
    }

    // 获取设备概览信息
    public DeviceDetaiInfo getDeviceDetailInfo(int taskID, int projectID, String ip) {
        DeviceDetaiInfo deviceDetailInfo = new DeviceDetaiInfo();
        String deviceType = "", brand = "";
        ArrayList<Map<String, String>> serviceList = new ArrayList<>();
        HashMap<String, Map<String,String>> serviceMap = new HashMap<>();
        ArrayList<VulDetail> vulDetailList = new ArrayList<>();
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskID, projectID);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        IpInfo ipInfo = taskResultMap.get(ip);
        if (ipInfo.getDeviceInfoSummary() != null) {
            brand = super.getMaxSimilarityDevType(ipInfo.getDeviceInfoSummary().getDeviceInfoList()).getBrand();
            deviceType = super.getDeviceType(ipInfo.getDeviceInfoSummary().getSecondDeviceType());
        }
        if (ipInfo.getOnvif() != null && ipInfo.getOnvif().getPort() != 0) {
            Map<String,String> onvifMap = new HashMap<>();
            String port = ipInfo.getOnvif().getPort() + "";
            onvifMap.put("port",port);
            onvifMap.put("version","");
            serviceMap.put("onvif", onvifMap);
            getDeviceVulDetail(ipInfo.getOnvif().getVulList(), vulDetailList, "onvif", port);
        }
        if (ipInfo.getPortList() != null) {
            ArrayList<PortInfo> portInfoList = ipInfo.getPortList();
            for (int i = 0; i < portInfoList.size(); i++) {
                String serviceType = "", servicePort = "",serviceVersion = "";
                PortInfo portInfo = portInfoList.get(i);
                if (portInfo.getService() != null) {
                    serviceType = portInfo.getService().getType();
                    servicePort = portInfo.getPort() + "";
                    serviceVersion = portInfo.getService().getVersion();
                    Map<String,String> map = new HashMap<>();
                    map.put("port",servicePort);
                    map.put("version",serviceVersion);
                    if (!serviceType.equals("") && !serviceType.equals("unknown") && !serviceMap.containsKey(serviceType)) {
                        serviceMap.put(serviceType, map);
                    }
                }
                if (portInfo.getVulList() != null) {
                    getDeviceVulDetail(portInfo.getVulList(), vulDetailList, serviceType, servicePort);
                }
            }
        }
        for (String serviceType : serviceMap.keySet()) {
            HashMap<String, String> tempmap = new HashMap<>();
            tempmap.put("name", serviceType);
            tempmap.put("port", serviceMap.get(serviceType).get("port"));
            tempmap.put("version", serviceMap.get(serviceType).get("version"));
            serviceList.add(tempmap);
        }
        deviceDetailInfo.setIp(ipInfo.getIp());
        deviceDetailInfo.setStyle(deviceType);
        deviceDetailInfo.setBrand(brand);
        deviceDetailInfo.setService(serviceList);
        deviceDetailInfo.setVulDes(vulDetailList);
        return deviceDetailInfo;
    }

    // 删除指定任务的结果
    public void deleteTaskResult(int taskID) {
        taskResultDao.deleteTaskResult(taskID);
    }

    //按ip地址降序排序
    Comparator<DeviceDetaiInfo> ipComparator = new Comparator<DeviceDetaiInfo>() {
        @Override
        public int compare(DeviceDetaiInfo o1, DeviceDetaiInfo o2) {
            if (o1.compareTo(o2) > 0) {
                return -1;
            } else if (o1.compareTo(o2) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    //按照漏洞数量升序排序
    Comparator<DeviceDetaiInfo> vulComparatorAsc = new Comparator<DeviceDetaiInfo>() {
        @Override
        public int compare(DeviceDetaiInfo o1, DeviceDetaiInfo o2) {
            return o1.getVulCount().compareTo(o2.getVulCount());
        }
    };

    //按照漏洞数量降序排序
    Comparator<DeviceDetaiInfo> vulComparatorDesc = new Comparator<DeviceDetaiInfo>() {
        @Override
        public int compare(DeviceDetaiInfo o1, DeviceDetaiInfo o2) {
            if (o1.getVulCount().compareTo(o2.getVulCount()) > 0) {
                return -1;
            } else if (o1.getVulCount().compareTo(o2.getVulCount()) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    //获取IpInfo信息
    public String getIpInfo(int projectID, int taskID, String ip) {
        return taskResultDao.getIpInfo(projectID, taskID, ip);
    }

    //获取exploit漏洞验证的参数
    public ExploitVerifyParam getExploitVertifyParam(int projectID, int taskID, String ip, String vulName) {
        ExploitVerifyParam exploitVerifyParam = new ExploitVerifyParam();
        String ipInfo = getIpInfo(projectID, taskID, ip);
        if (ipInfo != null) {
            ArrayList<String> payLoadList = new ArrayList<>();
            ArrayList<ExploitVulParam> vulParamList = new ArrayList<>();
            JSONObject ipInfoJson = JSONObject.parseObject(ipInfo);
            JSONArray payLoadArr = ipInfoJson.getJSONObject("exploit").getJSONArray("payloads_list");
            JSONArray vulList = ipInfoJson.getJSONObject("exploit").getJSONArray("vul_list");
            for (int i = 0; i < payLoadArr.size(); i++) {
                payLoadList.add(payLoadArr.getString(i));
            }
            exploitVerifyParam.setPayload(payLoadList);
            for (int i = 0; i < vulList.size(); i++) {
                if (vulList.getJSONObject(i).getString("script_name").equals(vulName)) {
                    JSONArray paramArr = vulList.getJSONObject(i).getJSONArray("arguments_list");
                    for (int j = 0; j < paramArr.size(); j++) {
                        String param = paramArr.getJSONObject(j).toString();
                        vulParamList.add((ExploitVulParam) JsonUtil.JsonToObj(param, ExploitVulParam.class));
                    }
                    exploitVerifyParam.setParam(vulParamList);
                    break;
                }
            }
        }
        vulDeepScanModule.setExploitVerifyParam(exploitVerifyParam);
        return exploitVerifyParam;
    }

    //保存深度扫描的结果
    public void saveDeepScanResult(int taskID, int projectID, String ip, String ipInfo) {
        taskResultDao.saveDeepScanResultResult(taskID, projectID, ip, ipInfo);
    }

    //获取设备的漏洞信息
    public ArrayList<VulDetail> getDeviceVulList(int projectID, int taskID, String ip) {
        ArrayList<VulDetail> vulList = new ArrayList<>();
        List<TaskResult> taskResultList = taskResultDao.getTaskResult(taskID, projectID);
        HashMap<String, IpInfo> taskResultMap = super.serializeTaskResult(taskResultList);
        IpInfo ipInfo = taskResultMap.get(ip);
        //统计onvif中漏洞
        if (ipInfo.getOnvif() != null && ipInfo.getOnvif().getPort() != 0) {
            String port = ipInfo.getOnvif().getPort() + "";
            getDeviceVulDetail(ipInfo.getOnvif().getVulList(), vulList, "onvif", port);
        }
        //统计每个端口的漏洞
        if (ipInfo.getPortList() != null) {
            ArrayList<PortInfo> portInfoList = ipInfo.getPortList();
            for (int i = 0; i < portInfoList.size(); i++) {
                String serviceType = "", servicePort = "";
                PortInfo portInfo = portInfoList.get(i);
                if (portInfo.getVulList() != null) {
                    getDeviceVulDetail(portInfo.getVulList(), vulList, serviceType, servicePort);
                }
            }
        }
        //统计exploit漏洞信息
        if (ipInfo.getExploit() != null) {
            ArrayList<ExploitVul> exploitVulList = ipInfo.getExploit().getExploitVulList();
            if (exploitVulList != null) {
                getDeviceExploitVulDetail(exploitVulList, vulList);
            }
        }
        //统计openvas漏洞
        if (ipInfo.getOpenvasVulList() != null) {
            ArrayList<Openvas> openvasVulArrayList = ipInfo.getOpenvasVulList();
            getDeviceOpenVasVulDetail(openvasVulArrayList, vulList);
        }
        return vulList;
    }

}
