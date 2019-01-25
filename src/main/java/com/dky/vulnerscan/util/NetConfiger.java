package com.dky.vulnerscan.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//本模块仅适合ubuntu14.04, 支持设备具有多个以太网口
//本模块不更新/etc/network/interface，因此要求在该配置文件中不要配置以太网口信息
public class NetConfiger {
	public String device;
	public String IP;
	public String netmask;
	public String gateway;
	public String DNS1;
	public String DNS2;
	public Integer dhcpFlag; // 1:采用dhcp；0:采用静态地址
	public Integer bandwidth;

	public NetConfiger(){
		device = "";
		IP = "";
		netmask = "";
		gateway = "";
		DNS1 = "";
		DNS2 = "";
		dhcpFlag = 0;
		bandwidth = 0;
	}

	public NetConfiger(String device){
		this();
		this.device = device;
	}

	public NetConfiger(String device, String IP, String netmask, String gateway, String DNS1, String DNS2, Integer dhcpFlag,Integer bandwidth){
		this();
		this.device = device;
		this.IP = IP;
		this.netmask = netmask;
		this.gateway = gateway;
		this.DNS1 = DNS1;
		this.DNS2 = DNS2;
		this.dhcpFlag = dhcpFlag;
		this.bandwidth = bandwidth;
	}

	private void loadDns(){
		this.DNS1 = "";
		this.DNS2 = "";
		String res = runCmd("cat /etc/resolv.conf");
		String lines[] = res.split("\n");
		String DNS1Line, DNS2Line;
		DNS1Line = DNS2Line = "";
		for (String line : lines) {
			if (line.indexOf("nameserver") >= 0) {
				if (DNS1Line.equals(""))
					DNS1Line = line;
				else
					DNS2Line = line;
			}
		}
		int i = DNS1Line.indexOf("nameserver");
		if (i >= 0 && DNS1Line.indexOf(" ", i) >= 0 && (DNS1Line.indexOf(" ", i) + 1 < DNS1Line.length())) {
			this.DNS1 = DNS1Line.substring(DNS1Line.indexOf(" ", i) + 1);
		}

		i = DNS2Line.indexOf("nameserver");
		if (i >= 0 && DNS2Line.indexOf(" ", i) >= 0 && (DNS2Line.indexOf(" ", i) + 1 < DNS2Line.length())) {
			this.DNS2 = DNS2Line.substring(DNS2Line.indexOf(" ", i) + 1);
		}
	}

	//获得dhclient的进程号
	//root     16885  0.0  0.0  10228  4344 ?        Ss   20:22   0:00 dhclient eth0 -nw
	//root     21259  0.0  0.0  11132  2776 pts/2    S    16:42   0:00 /bin/sh -c ps -aux | grep 'dhclient eth0'
	//root     21261  0.0  0.0  10468  2192 pts/2    S    16:42   0:00 grep dhclient eth0
	private int getDhclient(){
		int procNo = -1;
		String res = runCmd("ps -aux | grep \'dhclient "+device+"\'");
		String lines[] = res.split("\n");
		for(String line : lines){
			if(line.contains("/bin/sh -c ps -aux | grep \'dhclient "+device+"\'")==false &&
					line.contains("grep dhclient "+device)==false){
				String words[] = line.trim().split(" ");
				if(words.length>1){//提取进程号
					for(int i=1; i<words.length; i++){
						if (words[i].length()!=0){
							try{
								procNo = Integer.parseInt(words[i]);
							}catch (Exception e){
								System.out.println("[NetConfiger getDhclient]:"+e);
							}
							break;
						}
					}
				}
				break;
			}
		}//for(String line : lines)
		return procNo;
	}

	private void loadIp(){
		//获得是否DHCP
		if(getDhclient()>0)
			dhcpFlag=1;
		else
			dhcpFlag = 0;

		//获得IP
		this.IP = "";
		this.netmask = "";
		String res = runCmd("ifconfig " + this.device);
		int i = res.indexOf("inet addr:");
		if (i < 0)
			i = res.indexOf("inet 地址:");
		if (i >= 0) {
			this.IP = res.substring(res.indexOf(":", i + 7) + 1, res.indexOf(" ", i + 7));
		}
		i = res.indexOf("Mask:", i + 7);
		if (i < 0)
			i = res.indexOf("掩码:", i + 7);
		if (i >= 0) {
			this.netmask = res.substring(res.indexOf(":", i + 2) + 1, res.indexOf("\n", i + 2));
		}
	}

	private void loadGw(){
		//获得默认网关
		this.gateway = "";
		String res = runCmd("route -n");
		String lines[] = res.split("\n");
		for (String line : lines) {
			if (line.indexOf("0.0.0.0") == 0 && line.indexOf(this.device) > 0) {
				this.gateway = line.substring(16, line.indexOf(" ", 16));
				break;
			}
		}
	}
	//加载设备当前的配置
	public void load(){
		synchronized(NetConfiger.class) {
			loadDns();
			if (this.device == "") {
				return;
			}
			loadIp();
			loadGw();
		}//synchronized
	}

	private void dhcpSet(){
		runCmd("ifconfig "+device+" down");
		runCmd("ifconfig "+device+" 0.0.0.0");
		runCmd("dhclient "+device+" -nw");
		//runCmd(new String[]{"dhclient", device, "-v", "-nw"});
		//runCmd(new String[]{"/bin/sh", "/root/dhclientEth0.sh"});

		NetConfiger tmp = new NetConfiger(device);
		for(int i=0; i<20; i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tmp.load();
			if(tmp.IP.equals("")==false){
				break;
			}
		}
	}

	private void staticSet(){
		//设置IP
		runCmd("ifconfig "+device+" "+IP+" netmask "+netmask);

		//设置DNS
		if(DNS1!=null && DNS1.length()!=0) {
			runCmd("echo nameserver " + DNS1 + " > /etc/resolv.conf");
			runCmd("echo nameserver "+DNS1+" > /etc/resolvconf/resolv.conf.d/base");

			if(DNS2!=null && DNS2.length()!=0) {
				runCmd("echo nameserver " + DNS2 + " >> /etc/resolv.conf");
				runCmd("echo nameserver " + DNS2 + " >> /etc/resolvconf/resolv.conf.d/base");
			}

		}else if(DNS2!=null && DNS2.length()!=0) {
			runCmd("echo nameserver " + DNS2 + " > /etc/resolv.conf");
			runCmd("echo nameserver " + DNS2 + " > /etc/resolvconf/resolv.conf.d/base");
		}

		//设置默认网关
		runCmd("route del default");
		//如果要求设置网关
		if(gateway!=null && gateway.equals("")==false)
			runCmd("route add default gw "+gateway);
	}

	//成功设置网络信息返回 true
	//设置不成功返回false
	public boolean trySet(){
		synchronized (NetConfiger.class) {
			NetConfiger tmp = set();
			if(tmp==null){
				return false;
			}

			if (dhcpFlag == 1) {// 采用dhcp
				if (tmp.IP.startsWith("169.254") || tmp.IP.equals("")) {
					return false;
				}
				return true;
			} else {
				if ( device.equals(tmp.device) && IP.equals(tmp.IP)
						&& gateway.equals(tmp.gateway) && netmask.equals(tmp.netmask) ) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private NetConfiger set(){
		if(device.equals(""))
			return null;

		//杀死之前的dhclient
		int procNo;
		while( (procNo=getDhclient())>0 ){
			runCmd("kill "+procNo);
		}
		runCmd("dhclient "+device+" -x");
		if(dhcpFlag==1){// 采用dhcp
			dhcpSet();
		}else{// 采用静态IP
			staticSet();
		}

		NetConfiger res = new NetConfiger(device);
		res.load();
		return res;
	}

	private String runCmd(String cmd){
		Runtime rt = Runtime.getRuntime();
		String[] cmdA = { "/bin/sh", "-c", cmd };
		String res="";
		try {
			//Process process = rt.exec(cmdA);
			Process process = rt.exec(cmdA);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));

			//System.err.println("[NetConfigure runCmd()] cmd --> "+cmd);
			String line = null;
			while ((line = br.readLine()) != null) {
				//System.err.println("[NetConfigure runCmd()] --> "+line);
				res = res+line+'\n';
			}
		} catch (IOException e) {
			System.err.println("[NetConfigure runCmd()] --> "+e);
		}
		return res;
	}

	public void print(){
		System.out.println("eth0: \t["+device+"]");
		System.out.println("DHCP: \t["+dhcpFlag+"]");
		System.out.println("IP: \t["+IP+"]");
		System.out.println("Netmask:["+netmask+"]");
		System.out.println("Gateway:["+gateway+"]");
		System.out.println("DNS1: \t["+DNS1+"]");
		System.out.println("DNS2: \t["+DNS2+"]");
	}
}
