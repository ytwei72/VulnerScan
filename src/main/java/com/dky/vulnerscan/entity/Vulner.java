package com.dky.vulnerscan.entity;

public class Vulner {

	private String vul_id;
	private String vul_name;
	private String vul_type;
	private String vul_time;
	private String first_type;
	private String affect_brand;
	private String affect_product;
	private String service;
	private String risklevel;
	private String dangers;
	private String description;
	private String poc_filepath;

    //用于查询的字段：查询方式和查询关键字
	private String s_method;
	private String s_value;

    //显示脚本内容
    private String poc_content;

    public String getVul_id() {
        return vul_id;
    }

    public void setVul_id(String vul_id) {
        this.vul_id = vul_id;
    }

    public String getVul_name() {

        return vul_name;
    }

    public void setVul_name(String vul_name) {
        this.vul_name = vul_name;
    }

    public String getVul_type() {
        return vul_type;
    }

    public void setVul_type(String vul_type) {
        this.vul_type = vul_type;
    }

    public String getVul_time() {
        return vul_time;
    }

    public void setVul_time(String vul_time) {
        this.vul_time = vul_time;
    }

    public String getFirst_type() {
        return first_type;
    }

    public void setFirst_type(String first_type) {
        this.first_type = first_type;
    }

    public String getAffect_brand() {
        return affect_brand;
    }

    public void setAffect_brand(String affect_brand) {
        this.affect_brand = affect_brand;
    }

    public String getAffect_product() {
        return affect_product;
    }

    public void setAffect_product(String affect_product) {
        this.affect_product = affect_product;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRisklevel() {
        return risklevel;
    }

    public void setRisklevel(String risklevel) {
        this.risklevel = risklevel;
    }

    public String getDangers() {
        return dangers;
    }

    public void setDangers(String dangers) {
        this.dangers = dangers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoc_filepath() {
        return poc_filepath;
    }

    public void setPoc_filepath(String poc_filepath) {
        this.poc_filepath = poc_filepath;
    }

    public String getPoc_content() {
        return poc_content;
    }

    public void setPoc_content(String poc_content) {
        this.poc_content = poc_content;
    }

    public String getS_method() {
		return s_method;
	}

	public void setS_method(String s_method) {
		this.s_method = s_method;
	}

	public String getS_value() {
		return s_value;
	}

	public void setS_value(String s_value) {
		this.s_value = s_value;
	}
	
}
