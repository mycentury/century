package cn.himma.hessian.ui.domain;

import java.util.List;

public class HserviceDomain {
	private String url;
	private String name;
	private String shortname;
	private List<HmethodDomain> hmethods;
	private boolean isused = false;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public List<HmethodDomain> getHmethods() {
		return this.hmethods;
	}

	public void setHmethods(List<HmethodDomain> hmethods) {
		this.hmethods = hmethods;
	}

	public boolean isIsused() {
		return this.isused;
	}

	public void setIsused(boolean isused) {
		this.isused = isused;
	}
}