package zsc.edu.cn.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Abouerp
 */
public class CatBean implements Serializable {
	/**
	 * 1私聊 0上下线更新 -1下线请求 2请求发送文件 3.确定接收文件
	 */
	private int type;
	/**
	 * 存放选中的用户
	 */
	private HashSet<String> clients;
	/**
	 * 文件目的地
	 */
	private HashSet<String> to;
	/**
	 * 在线的人
	 */
	public HashMap<String, ClientBean> onlines;
	/**
	 * 发送的信息
	 */
	private String info;
	/**
	 * 时间
	 */
	private String timer;
	/**
	 * 记录的文件名称
	 */
	private String name;

	private String fileName;
	/**
	 * 文件大小
	 */
	private int size;
	/**
	 * 接收者ip
	 */
	private String ip;
	/**
	 * 端口
	 */
	private int port;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashSet<String> getTo() {
		return to;
	}

	public void setTo(HashSet<String> to) {
		this.to = to;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashSet<String> getClients() {
		return clients;
	}

	public void setClients(HashSet<String> clients) {
		this.clients = clients;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public HashMap<String, ClientBean> getOnlines() {
		return onlines;
	}

	public void setOnlines(HashMap<String, ClientBean> onlines) {
		this.onlines = onlines;
	}

	
	
}
