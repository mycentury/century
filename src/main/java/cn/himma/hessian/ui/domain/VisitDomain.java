package cn.himma.hessian.ui.domain;

import java.util.Date;

public class VisitDomain {
	private String ip;
	private String className;
	private String methodName;
	private Long totalTimes;
	private Long avgTime;
	private Date lastTime;

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Long getTotalTimes() {
		return this.totalTimes;
	}

	public void setTotalTimes(Long totalTimes) {
		this.totalTimes = totalTimes;
	}

	public Long getAvgTime() {
		return this.avgTime;
	}

	public void setAvgTime(Long avgTime) {
		this.avgTime = avgTime;
	}

	public Date getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
}
