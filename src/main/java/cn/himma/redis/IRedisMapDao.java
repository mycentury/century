package cn.himma.redis;

import java.util.List;
import java.util.Map;

public interface IRedisMapDao<E> {

	@Deprecated
	Boolean flushDb();

	Boolean hashSetNX(String key, E e);

	Boolean hashSet(String key, E e);

	E hashGet(String key);

	Boolean hashMultiSet(Map<String, E> map);

	List<E> hashMultiGet(String[] keys);

	Map<String, E> hashGetAll();

	Long hashCount();

	List<String> hashKeys();

	List<E> hashVals();

	Boolean hashExists(String key);
}
