package com.dky.vulnerscan.util;

import com.dky.vulnerscan.entity.*;
import com.dky.vulnerscan.entityview.ReportVulInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * FileName VulUtil.java
 * 辅助生成报表数据的工具类
 * Created by liushaoshuai on 2016/12/15.
 */
public class VulUtil {
    private IpInfo ipInfo;
    public VulUtil(IpInfo ipInfo){
        this.ipInfo=ipInfo;
    }
    //统计设备漏洞信息
    public ArrayList<ReportVulInfo> getVulInfoLists(){
        ArrayList<ReportVulInfo> vulList = new ArrayList<ReportVulInfo>();
        if(getLocalVulInfo()!=null && getLocalVulInfo().size()>0){
            ArrayList<ReportVulInfo> reportVulInfos=getLocalVulInfo();
            for(int i=0;i<reportVulInfos.size();i++){
                vulList.add(reportVulInfos.get(i));
            }
        }
        if(getExploitVulInfo()!=null && getExploitVulInfo().size()>0){
            ArrayList<ReportVulInfo> reportVulInfos=getExploitVulInfo();
            for(int i=0;i<reportVulInfos.size();i++){
                vulList.add(reportVulInfos.get(i));
            }
        }
        if(getOpenvasVulInfo()!=null && getOpenvasVulInfo().size()>0){
            ArrayList<ReportVulInfo> reportVulInfos=getOpenvasVulInfo();
            for(int i=0;i<reportVulInfos.size();i++){
                vulList.add(reportVulInfos.get(i));
            }
        }
        return  vulList;
    }

    //统计本地漏洞库信息
    public ArrayList<ReportVulInfo> getLocalVulInfo(){
        ArrayList<ReportVulInfo> vulLocList = new ArrayList<ReportVulInfo>();
        if (ipInfo.getOnvif() != null && ipInfo.getOnvif().getPort() != 0) { //统计onvif中漏洞
            String port = ipInfo.getOnvif().getPort() + "";
            ReportVulInfo reportVulInfo=new ReportVulInfo();
            reportVulInfo.setService("onvif");
            reportVulInfo.setType("tcp");
            reportVulInfo.setPort(port);
            if(ipInfo.getOnvif().getVulList()!=null){
                reportVulInfo.setVulInfos(ipInfo.getOnvif().getVulList());
            }
            vulLocList.add(reportVulInfo);
        }
        if (ipInfo.getPortList() != null) {  //统计端口的漏洞
            ArrayList<PortInfo> portInfoList = ipInfo.getPortList();
            for (int i = 0; i < portInfoList.size(); i++) {
                PortInfo portInfo = portInfoList.get(i);
                String service = "", type = "",port="";
                if(portInfo.getService()!=null && portInfo.getService().getType()!=null && portInfo.getService().getType().length()>0){
                    service=portInfo.getService().getType();
                }
                if(portInfo.getType()!=null && portInfo.getType().length()>0){
                    type=portInfo.getType();
                }
                if( portInfo.getPort()>0){
                    port=portInfo.getPort()+"";
                }
                ReportVulInfo reportVulInfo=new ReportVulInfo();
                reportVulInfo.setService(service);
                reportVulInfo.setType(type);
                reportVulInfo.setPort(port);
                if (portInfo.getVulList() != null) {
                    reportVulInfo.setVulInfos(portInfo.getVulList());
                }
                vulLocList.add(reportVulInfo);
            }
        }
        return vulLocList;
    }

    //统计Exploit漏洞库信息
    public ArrayList<ReportVulInfo> getExploitVulInfo(){
        ArrayList<ReportVulInfo> vulExploitList = new ArrayList<ReportVulInfo>();
        if (ipInfo.getExploit() != null && ipInfo.getExploit().getExploitVulList() != null){
            ArrayList<ExploitVul> exploitVulList=ipInfo.getExploit().getExploitVulList();

            ArrayList<VulInfo> vulInfos=new ArrayList<VulInfo>();
            for (int i = 0; i < exploitVulList.size(); i++) {
                ReportVulInfo revulInfo = new ReportVulInfo();
                revulInfo.setService("");
                revulInfo.setType("");
                revulInfo.setPort("");
                VulInfo vulInfo = new VulInfo();
                vulInfo.setVulTitle(exploitVulList.get(i).getScriptName());
                vulInfo.setVerifyState(exploitVulList.get(i).getVerifyState());
                vulInfo.setVulSource(exploitVulList.get(i).getVulSource());
                vulInfos.add(vulInfo);
                revulInfo.setVulInfos(vulInfos);
                vulExploitList.add(revulInfo);
            }
        }
        return vulExploitList;
    }
    //统计Openvas漏洞库信息
    public ArrayList<ReportVulInfo> getOpenvasVulInfo(){
        ArrayList<ReportVulInfo> vulOpenvasList = new ArrayList<ReportVulInfo>();
        ArrayList<Openvas> openvass=ipInfo.getOpenvasVulList();
        if(openvass!=null){
            for(int i=0;i<openvass.size();i++){
                ReportVulInfo reportVulInfo=new ReportVulInfo();
                ArrayList<VulInfo> vulInfos=new ArrayList<VulInfo>();
                reportVulInfo.setService(openvass.get(i).getName());
                String[] ports=openvass.get(i).getPort().split("/");
                if(ports.length>1){
                    reportVulInfo.setType(ports[1]);
                }else{
                    reportVulInfo.setType("");
                }
                reportVulInfo.setPort(ports[0]);
                VulInfo vulInfo=new VulInfo();
                vulInfo.setVerifyState(openvass.get(i).getVerifyState());
                vulInfo.setVulSource(openvass.get(i).getVulSource());
                vulInfos.add(vulInfo);
                reportVulInfo.setVulInfos(vulInfos);
                vulOpenvasList.add(reportVulInfo);
            }
        }
        return vulOpenvasList;
    }

    //统计设备漏洞库数量
    public static int countVulInfos(ArrayList<ReportVulInfo> reportVulInfos){
        int num=0;
        for(int i=0;i<reportVulInfos.size();i++){
            if(reportVulInfos.get(i).getVulInfos()!=null){
                num+=reportVulInfos.get(i).getVulInfos().size();
            }
        }
        return num;
    }

    //统计漏洞类型分布
    public HashMap<String,Integer> countVulTypeNum(ArrayList<ReportVulInfo> reportVulInfos){
        HashMap<String,Integer> map=new HashMap<String,Integer>();
        for(int i=0;i<reportVulInfos.size();i++){
            ReportVulInfo reportVulInfo=reportVulInfos.get(i);
            if(reportVulInfo.getVulInfos()!=null){
                for(int j=0;j<reportVulInfo.getVulInfos().size();j++){
                    VulInfo vulInfo=reportVulInfo.getVulInfos().get(j);
                    if(vulInfo.getVulType()!=null && vulInfo.getVulType().length()>0){
                        String type=vulInfo.getVulType();
                        if(map.containsKey(type)){
                            int num=map.get(type);
                            map.put(type,num+1);
                        }else{
                            map.put(type,1);
                        }
                    }
                }
            }

        }

        return map;
    }

    //统计已验证的漏洞数量
    public int countCheckedVulNum(ArrayList<ReportVulInfo> reportVulInfos){
        int num=0;
        for(ReportVulInfo reportVulInfo:reportVulInfos){
            if(reportVulInfo.getVulInfos()!=null){
                for(int i=0;i<reportVulInfo.getVulInfos().size();i++){
                    VulInfo vulInfo=reportVulInfo.getVulInfos().get(i);
                    if(vulInfo.getVerifyState()!=null && vulInfo.getVerifyState().length()>0 && vulInfo.getVerifyState().equals("已验证")){
                        num++;
                    }
                }
            }

        }
        return num;
    }

    //统计工控服务列表
    public HashMap<String,Integer> countServlist(ArrayList<ReportVulInfo> reportVulInfos){
        HashMap<String,Integer> map=new HashMap<String,Integer>();
        for(ReportVulInfo reportVulInfo:reportVulInfos){
            String service="";
            if(reportVulInfo.getService()!=null && reportVulInfo.getService().length()>0){
                service=reportVulInfo.getService();
                if(service.equals("unknown")){
                    continue;
                }
                if(map.containsKey(service)){
                    int num=map.get(service);
                    map.put(service,num+1);
                }else{
                    map.put(service,1);
                }
            }

        }
        return map;
    }
}
