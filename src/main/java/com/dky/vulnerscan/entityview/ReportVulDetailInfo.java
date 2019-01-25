package com.dky.vulnerscan.entityview;

import java.util.ArrayList;
import java.util.HashMap;

/**用于构造报表的漏洞详情列表
 * Created by cyberpecker on 2016/12/23.
 */
public class ReportVulDetailInfo {

    private String vulname; //漏洞名称
    private String vullevel;//漏洞等级
    private ArrayList<HashMap<String,String>> ips;//对应的ip


    public String getVulname() {
        return vulname;
    }

    public void setVulname(String vulname) {
        this.vulname = vulname;
    }

    public String getVullevel() {
        return vullevel;
    }

    public void setVullevel(String vullevel) {
        this.vullevel = vullevel;
    }

    public ArrayList<HashMap<String, String>> getIps() {
        return ips;
    }

    public void setIps(ArrayList<HashMap<String, String>> ips) {
        this.ips = ips;
    }
}
