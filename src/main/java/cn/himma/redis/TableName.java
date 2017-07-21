/**
 * 
 */
package cn.himma.redis;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月19日
 * @ClassName TableName
 */
public enum TableName {
	LOG("LOG"), APP("APP"), WEB("WEB");
	private String value;

	private TableName(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
