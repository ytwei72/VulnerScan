package com.dky.vulnerscan.util;

//import com.dky.vulnerscan.entity.VulInfo;
//import com.dky.vulnerscan.entityview.ReportDeviceInfo;
//import com.dky.vulnerscan.entityview.ReportResultCount;
//import com.dky.vulnerscan.entityview.ReportVulDetailInfo;
//import com.dky.vulnerscan.entityview.ReportVulInfo;
//import com.dky.vulnerscan.service.ReportService;
import com.dky.vulnerscan.entity.VulInfo;
import com.dky.vulnerscan.entityview.ReportDeviceInfo;
import com.dky.vulnerscan.entityview.ReportResultCount;
import com.dky.vulnerscan.entityview.ReportVulDetailInfo;
import com.dky.vulnerscan.entityview.ReportVulInfo;
import com.dky.vulnerscan.service.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

//import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

//import static com.itextpdf.text.pdf.PdfName.DEST;

/**ReportUtil.java
 * 生成报表的工具类
 * Created by liushaoshuai on 2016/12/16.
 */
//@Component
public class ReportUtil {

    private ReportService reportService;

    private  String font ;
    private  String mediumFont;
    private BaseFont cnFontBf;
    private BaseFont cnBoldFontBf;
    private ReportResultCount reportResultCount;
    private ArrayList<ReportDeviceInfo> reportDeviceInfos;
    private String imgUrl1;
    private String imgUrl2;
    private String imgUrl3;
    private ArrayList<ReportVulDetailInfo> locVulInfos;
    private ArrayList<ReportVulDetailInfo> exploitVulInfos;
    private ArrayList<ReportVulDetailInfo> openvasVulInfos;
    public ReportUtil(int projectId, int taskId) {
        try {
        this.font=this.getClass().getResource("/").getPath()+"/"+"reportFont/SourceHanSansSC-Normal.otf";
        this.mediumFont=this.getClass().getResource("/").getPath()+"/"+"reportFont/SourceHanSansSC-Medium.otf";
        this.imgUrl1=this.getClass().getResource("/").getPath()+"/reportFont/jfreechart1.jpeg";
        this.imgUrl2=this.getClass().getResource("/").getPath()+"/reportFont/jfreechart2.jpeg";
        this.imgUrl3=this.getClass().getResource("/").getPath()+"/reportFont/jfreechart3.jpeg";
        this.cnFontBf = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        this.cnBoldFontBf = BaseFont.createFont(mediumFont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/spring-mybatis.xml");
        reportService=(ReportService) ctx.getBean("reportService");
        this.reportResultCount = reportService.getReportResultCount(projectId,taskId);
        this.reportDeviceInfos = reportService.getReportDevInfos(projectId,taskId);
        this.locVulInfos=reportService.getVulDetailInfo(reportDeviceInfos,"本地漏洞库");
       this.exploitVulInfos=reportService.getVulDetailInfo(reportDeviceInfos,"Exploit");
       this.openvasVulInfos=reportService.getVulDetailInfo(reportDeviceInfos,"openVAS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean manipulatePdf(String dest) {
        boolean flag = false;
        try {
            File file = new File(dest);
            file.getParentFile().mkdirs();
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(dest));
            doc.open();
            Font f = new Font(cnBoldFontBf, 20);
            Paragraph coverTitle = new Paragraph("\n\n\n" + "视频监控系统" + "\n" + "信息安全检查报告", f);
            coverTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(coverTitle);
            f = new Font(cnBoldFontBf, 14);
            Paragraph coverProName = new Paragraph("\n\n\n\n\n\n\n\n\n" + "项目名：" + reportResultCount.getProjectName(), f);
            Paragraph coverTaskID = new Paragraph("任务编号：" + reportResultCount.getTaskNo(), f);
            Paragraph coverdate = new Paragraph("检查日期：" + reportResultCount.getCheckerDate(), f);
            Paragraph coverchecker = new Paragraph("检查人员：" + reportResultCount.getCherker(), f);
            coverProName.setIndentationLeft(180f);
            coverTaskID.setIndentationLeft(180f);
            coverdate.setIndentationLeft(180f);
            coverchecker.setIndentationLeft(180f);
            doc.add(coverProName);
            doc.add(coverTaskID);
            doc.add(coverdate);
            doc.add(coverchecker);
            f = new Font(cnFontBf, 10);
            Paragraph coverPage = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + "技术支持中国科学院信息工程研究所", f);
            coverPage.setAlignment(Element.ALIGN_CENTER);
            doc.add(coverPage);
            doc.newPage();
            f = new Font(cnBoldFontBf, 20);
            Paragraph paragraph = new Paragraph("1.综合分析", f);
            paragraph.setSpacingAfter(10);
            doc.add(paragraph);
            f = new Font(cnBoldFontBf, 16);
            Paragraph paragraph1 = getParagraph1();
            paragraph1.setFirstLineIndent(24);
            paragraph1.setFont(f);
            paragraph1.setSpacingAfter(20);
            doc.add(paragraph1);
            JFreeChartUtil.getPicUrl("图1 工控设备品牌分布", "品牌名称", "数量", reportResultCount.getInControlDeviceList(), imgUrl1);
            Image img1 = Image.getInstance(imgUrl1);
            img1.setAlignment(Element.ALIGN_CENTER);
            img1.scaleToFit(320, 480);
            doc.add(img1);
            Paragraph paragraph2 = getParagraph2();
            paragraph2.setFirstLineIndent(24);
            paragraph2.setFont(f);
            paragraph2.setSpacingAfter(20);
            doc.add(paragraph2);
           JFreeChartUtil.getPicUrl("图2 漏洞类型分布", "漏洞类型", "数量", reportResultCount.getVulTypeList(), imgUrl2);
            Image img2 = Image.getInstance(imgUrl2);
            img2.setAlignment(Element.ALIGN_CENTER);
            img2.scaleToFit(320, 480);
            doc.add(img2);
            Paragraph paragraph3 = getParagraph3();
            paragraph3.setFirstLineIndent(24);
            paragraph3.setFont(f);
            paragraph3.setSpacingAfter(20);
            doc.add(paragraph3);
           JFreeChartUtil.getPicUrl("图3 工控服务分布", "服务类型", "数量", reportResultCount.getServiceList(), imgUrl3);
            Image img3 = Image.getInstance(imgUrl3);
            img3.setAlignment(Element.ALIGN_CENTER);
            img3.scaleToFit(320, 480);
            doc.add(img3);
            Rectangle rt=doc.getPageSize();  //横向显示
            doc.setPageSize(new Rectangle(rt.getHeight(),rt.getWidth()));
            doc.newPage();
            f = new Font(cnBoldFontBf, 24);
            Paragraph pDeviceInfo = new Paragraph("2.设备详情", f);
            pDeviceInfo.setSpacingAfter(10);
            doc.add(pDeviceInfo);
            f = new Font(cnFontBf, 14);
            Paragraph pDeviceInfoTableTitle = new Paragraph("表1 设备详情表", f);
            pDeviceInfoTableTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(pDeviceInfoTableTitle);
            doc.add(getTable1());
            doc.setPageSize(rt); //纵向显示
            doc.newPage();
            f = new Font(cnBoldFontBf, 24);
            Paragraph pVulInfo = new Paragraph("3.漏洞详情", f);
            pVulInfo.setSpacingAfter(10);
            doc.add(pVulInfo);
            f = new Font(cnFontBf, 14);
            Paragraph pVulInfoTableTitle = new Paragraph("表2 漏洞详情表", f);
            pVulInfoTableTitle.setAlignment(Element.ALIGN_CENTER);
            doc.add(pVulInfoTableTitle);
            doc.add(getTable2());
            doc.close();
            flag = true;
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return flag;
        }
    }

    private Paragraph getParagraph1() {
        Font f = new Font(cnFontBf, 12);
        Paragraph p = new Paragraph();
        p.setFont(f);
        p.setLeading(20);
        p.add("本次检查共发现" + reportResultCount.getAliveIpNum() + "台存活主机。经深度检测分析，" +
                reportResultCount.getAliveIpNum() + "个存活主机中工控设备" +
                reportResultCount.getInControlDeviceNum() + "台，其他设备" +
                reportResultCount.getOtherDeviceNum() + "台。工控设备中，");
        String str = "";
        ArrayList<HashMap<String, String>> inControlDeviceList = reportResultCount.getInControlDeviceList();
        if(inControlDeviceList!=null){
            for (int i = 0; i < inControlDeviceList.size(); i++) {
                String name = inControlDeviceList.get(i).get("name");
                String num = inControlDeviceList.get(i).get("num");
                str += name + num + "台，";
            }
        }

        p.add(str + "具体分布如图1。");
        return p;
    }

    private Paragraph getParagraph2() {
        Font ff = new Font(cnFontBf, 12);
        Paragraph p = new Paragraph();
        p.setFont(ff);
        p.setLeading(20);
        String rate1 = getDecimal(reportResultCount.getVulIpNum(), reportResultCount.getAliveIpNum());
        String rate2 = getDecimal(reportResultCount.getInControlDeviceVulNum(), reportResultCount.getInControlDeviceNum());
        String str = "";
        ArrayList<HashMap<String, String>> vulTypeList = reportResultCount.getVulTypeList();
        if(vulTypeList!=null){
            for (int i = 0; i < vulTypeList.size(); i++) {
                String name = vulTypeList.get(i).get("name");
                String num = vulTypeList.get(i).get("num");
                str += name + num + "个，";
            }
        }
        int vulnum = reportResultCount.getVulNum();
        int checkedvulnum = reportResultCount.getCheckedVulNum();
        int num = vulnum - checkedvulnum;
        p.add("本次检查共发现漏洞" + vulnum + "个，其中"
                + checkedvulnum + "个已验证，" + num + "个未验证，本地漏洞库扫描出"
                + reportResultCount.getLocalVulNum() + "个，exploit扫描出" + reportResultCount.getExploitVulNum() + "个，" +
                "openVAS扫描出" + reportResultCount.getOpenVasVulNum() + "个。存在漏洞的主机" +
                reportResultCount.getVulIpNum() + "台，占比" + rate1 + "。存在漏洞的工控设备" +
                reportResultCount.getInControlDeviceVulNum() + "台，占比" + rate2 + "。所有漏洞中，" +
                str + "具体分布如图2。");
        return p;
    }

    private String getDecimal(int num1, int num2) {
        String rate = "0";
        if (num2 > 0) {
            DecimalFormat percentFormat = new DecimalFormat();
            percentFormat.applyPattern("#0.00%");
            rate = percentFormat.format((double) num1 / num2);
        }
        return rate;
    }

    private Paragraph getParagraph3() {
        Font ff = new Font(cnFontBf, 12);
        Paragraph p = new Paragraph();
        p.setFont(ff);
        p.setLeading(20);

        ArrayList<HashMap<String, String>> servList = reportResultCount.getServiceList();
        String str = "";
        int znum = 0;
        if(servList!=null){
            for (int i = 0; i < servList.size(); i++) {
                String name = servList.get(i).get("name");
                String num = servList.get(i).get("num");
                znum += Integer.parseInt(num);
                str += name + num + "个，";
            }
            p.add("本次检查共识别出工控服务" + znum + "个，其中" + str.substring(0, str.length() - 1) + "。");
            return p;
        }
        p.add("本次检查共识别出工控服务" + znum + "个。" );
        return p;
    }

    private PdfPTable getTable1() {
        PdfPTable table = new PdfPTable(20);
        table.setSpacingBefore(10);
        table.addCell(createHCell("编号", 2));
        table.addCell(createHCell("IP", 2));
        table.addCell(createHCell("品牌", 2));
        table.addCell(createHCell("操作系统", 2));
        table.addCell(createHCell("协议服务", 2));
        table.addCell(createHCell("漏洞名称", 4));
        table.addCell(createHCell("漏洞等级", 2));
        table.addCell(createHCell("是否验证", 2));
        table.addCell(createHCell("来源", 2));

            for (int i = 0; i < reportDeviceInfos.size(); i++) {
                ReportDeviceInfo reportDeviceInfo = reportDeviceInfos.get(i);
                handleEmpty(reportDeviceInfo);
                if (getIpRowspan(reportDeviceInfo) == null) {
                    continue;
                }
                int[] ipRowspans = getIpRowspan(reportDeviceInfo);
                int ipRowspan = 0;
                for (int num : ipRowspans) {
                    ipRowspan += num;
                }
                table.addCell(createCell(i + 1 + "", ipRowspan, 2));
                table.addCell(createCell(reportDeviceInfo.getIp(), ipRowspan, 2));
                table.addCell(createCell(reportDeviceInfo.getBrand(), ipRowspan, 2));
                table.addCell(createCell(reportDeviceInfo.getOs(), ipRowspan, 2));

                ArrayList<ReportVulInfo> reportVulInfos = reportDeviceInfo.getReportVulInfos();
                for (int j = 0; j < reportVulInfos.size(); j++) {
                    ReportVulInfo reportVulInfo = reportVulInfos.get(j);
                    int serviceRowspan = ipRowspans[j];
                    table.addCell(createCell(reportVulInfo.getStp(), serviceRowspan, 2));
                    for (int k = 0; k < serviceRowspan; k++) {
                        VulInfo vulInfo=new VulInfo();
                        if(reportVulInfo.getVulInfos()!=null && reportVulInfo.getVulInfos().size()>0){
                            vulInfo = (VulInfo)reportVulInfo.getVulInfos().get(k);
                        }
                        handleEmptyVulInfo(vulInfo);
                        table.addCell(createCell(vulInfo.getVulTitle(), 1, 4));
                        table.addCell(createCell(vulInfo.getVulLevel(), 1, 2));
                        table.addCell(createCell(vulInfo.getVerifyState(), 1, 2));
                        table.addCell(createCell(vulInfo.getVulSource(), 1, 2));
                    }
                }
            }
            return table;
        }


    private PdfPTable getTable2() {
        PdfPTable table = new PdfPTable(12);
        table.setSpacingBefore(10);
        table.addCell(createHCell("漏洞来源", 2));
        table.addCell(createHCell("漏洞名称", 4));
        table.addCell(createHCell("等级", 2));
        table.addCell(createHCell("设备IP", 2));
        table.addCell(createHCell("是否验证", 2));

        if(locVulInfos!=null && locVulInfos.size()>0){
           int[] rowspan= getVulRowspan(locVulInfos);
           int vulrows=0;
            for (int num : rowspan) {
                vulrows += num;
            }
            table.addCell(createCell("本地漏洞库",vulrows,2));
            for(int j=0;j<locVulInfos.size();j++){
                ReportVulDetailInfo reportVulDetailInfo=locVulInfos.get(j);
                int serowspan = rowspan[j];
                table.addCell(createCell(reportVulDetailInfo.getVulname(), serowspan, 4));
                table.addCell(createCell(reportVulDetailInfo.getVullevel(), serowspan, 2));
                ArrayList<HashMap<String,String>> iips=reportVulDetailInfo.getIps();
                for(int k=0;k<serowspan;k++){
                    HashMap<String,String> map=iips.get(k);
                    table.addCell(createCell(map.get("ip"),1,2));
                    table.addCell(createCell(map.get("vulVerifyState"),1,2));
                }
            }

        }
        if(exploitVulInfos!=null && exploitVulInfos.size()>0){
            int[] rowspan= getVulRowspan(exploitVulInfos);
            int vulrows=0;
            for (int num : rowspan) {
                vulrows += num;
            }
            table.addCell(createCell("Exploit",vulrows,2));
            for(int j=0;j<exploitVulInfos.size();j++){
                ReportVulDetailInfo reportVulDetailInfo=exploitVulInfos.get(j);
                int serowspan = rowspan[j];
                table.addCell(createCell(reportVulDetailInfo.getVulname(), serowspan, 4));
                table.addCell(createCell(reportVulDetailInfo.getVullevel(), serowspan, 2));
                ArrayList<HashMap<String,String>> iips=reportVulDetailInfo.getIps();
                for(int k=0;k<serowspan;k++){
                    HashMap<String,String> map=iips.get(k);
                    table.addCell(createCell(map.get("ip"),1,2));
                    table.addCell(createCell(map.get("vulVerifyState"),1,2));
                }
            }

        }
        if(openvasVulInfos!=null && openvasVulInfos.size()>0){
            int[] rowspan= getVulRowspan(openvasVulInfos);
            int vulrows=0;
            for (int num : rowspan) {
                vulrows += num;
            }
            table.addCell(createCell("openVAS",vulrows,2));
            for(int j=0;j<openvasVulInfos.size();j++){
                ReportVulDetailInfo reportVulDetailInfo=openvasVulInfos.get(j);
                int serowspan = rowspan[j];
                table.addCell(createCell(reportVulDetailInfo.getVulname(), serowspan, 4));
                table.addCell(createCell(reportVulDetailInfo.getVullevel(), serowspan, 2));
                ArrayList<HashMap<String,String>> iips=reportVulDetailInfo.getIps();
                for(int k=0;k<serowspan;k++){
                    HashMap<String,String> map=iips.get(k);
                    table.addCell(createCell(map.get("ip"),1,2));
                    table.addCell(createCell(map.get("vulVerifyState"),1,2));
                }
            }

        }
        return table;
    }

    private PdfPCell createHCell(String content, int colspan) {
        Font f = new Font(cnBoldFontBf, 10);
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell createCell(String content, int rowspan, int colspan) {
        Font f = new Font(cnFontBf, 8);
        PdfPCell cell = new PdfPCell(new Paragraph(content, f));
        cell.setRowspan(rowspan);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private void handleEmpty(ReportDeviceInfo reportDeviceInfo) {
        if(reportDeviceInfo.getBrand()==null){
            reportDeviceInfo.setBrand("无");
        }
        if(reportDeviceInfo.getOs()==null){
            reportDeviceInfo.setOs("无");
        }
        if (reportDeviceInfo.getBrand()!=null && reportDeviceInfo.getBrand().length() < 0) {
            reportDeviceInfo.setBrand("无");
        }
        if (reportDeviceInfo.getOs()!=null && reportDeviceInfo.getOs().length() < 0) {
            reportDeviceInfo.setOs("无");
        }

    }
    private void handleEmptyVulInfo(VulInfo vulInfo) {
        if(vulInfo.getVulTitle()==null){
            vulInfo.setVulTitle("无");
        }
        if(vulInfo.getVulLevel()==null){
            vulInfo.setVulLevel("N/A");
        }
        if(vulInfo.getVerifyState()==null){
            vulInfo.setVerifyState("N/A");
        }
        if(vulInfo.getVulSource()==null){
            vulInfo.setVulSource("N/A");
        }
        if (vulInfo.getVulTitle()!=null && vulInfo.getVulTitle().length() <=0) {
            vulInfo.setVulTitle("无");
        }
        if(vulInfo.getVulLevel()!=null && vulInfo.getVulLevel().length()<=0){
            vulInfo.setVulLevel("N/A");
        }
        if(vulInfo.getVerifyState()!=null && vulInfo.getVerifyState().length()<=0){
            vulInfo.setVerifyState("N/A");
        }
        if(vulInfo.getVulSource()!=null && vulInfo.getVulSource().length()<=0){
            vulInfo.setVulSource("N/A");
        }
    }

    private int[] getIpRowspan(ReportDeviceInfo reportDeviceInfo) {
        if (reportDeviceInfo.getReportVulInfos() != null && reportDeviceInfo.getReportVulInfos().size() > 0) {
            int rowspan=reportDeviceInfo.getReportVulInfos().size();
            int[] rowsPanArray=new int[rowspan];
            for(int i=0;i<reportDeviceInfo.getReportVulInfos().size();i++){
                ReportVulInfo reportVulInfo=reportDeviceInfo.getReportVulInfos().get(i);
                if(reportVulInfo.getVulInfos()!=null && reportVulInfo.getVulInfos().size()>0) {
                    rowsPanArray[i]=reportDeviceInfo.getReportVulInfos().get(i).getVulInfos().size();
                }else {
                    rowsPanArray[i]=1;
                }
            }
            return rowsPanArray;
        }
       return null;
    }
    private int[] getVulRowspan(ArrayList<ReportVulDetailInfo> reportVulDetailInfos) {
            int rows=reportVulDetailInfos.size();
            int[] rowsarray=new int[rows];
            for(int i=0;i<reportVulDetailInfos.size();i++){
                ReportVulDetailInfo reportVulDetailInfo=reportVulDetailInfos.get(i);
                if(reportVulDetailInfo.getIps()!=null && reportVulDetailInfo.getIps().size()>0){
                    rowsarray[i]=reportVulDetailInfo.getIps().size();
                }else{
                    rowsarray[i]=1;
                }
            }
        return rowsarray;
    }

    }

