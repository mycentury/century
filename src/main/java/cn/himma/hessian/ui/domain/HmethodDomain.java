package cn.himma.hessian.ui.domain;

import java.util.List;

public class HmethodDomain {
	private String name;
	private String rname;
	private String srname;
	private List<HparamDomain> hparams;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRname() {
		return this.rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getSrname() {
		return this.srname;
	}

	public void setSrname(String srname) {
		this.srname = srname;
	}

	public List<HparamDomain> getHparams() {
		return this.hparams;
	}

	public void setHparams(List<HparamDomain> hparams) {
		this.hparams = hparams;
	}
}
