/**
 * 
 */
package cn.himma.redis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import cn.himma.util.service.GsonUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月11日
 * @ClassName AbstractRedisDao
 */
public abstract class AbsRedisMapDao<E> implements IRedisMapDao<E> {

	@Autowired
	protected RedisTemplate<String, E> redisTemplate;

	private Class<E> classE;

	protected abstract String getTableName();

	private void init() {
		Type t = getClass().getGenericSuperclass();
		Type[] actualTypeArguments = ((ParameterizedType) t)
				.getActualTypeArguments();
		classE = (Class<E>) actualTypeArguments[0];
		if (classE == null) {
			throw new RuntimeException("classE获取失败！");
		}
	}

	@Override
	public Boolean flushDb() {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = false;
				try {
					redisconnection.flushDb();
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Boolean hashSetNX(final String key, final E e) {
		final String jsonString = GsonUtil.toJsonString(e);
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = false;
				try {
					redisconnection.hSetNX(getTableName().getBytes(),
							key.getBytes(), jsonString.getBytes());
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Boolean hashSet(final String key, final E e) {
		final String jsonString = GsonUtil.toJsonString(e);
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = false;
				try {
					redisconnection.hSet(getTableName().getBytes(),
							key.getBytes(), jsonString.getBytes());
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public E hashGet(final String key) {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<E>() {
			@Override
			public E doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				E result = null;
				try {
					byte[] bytes = redisconnection.hGet(getTableName()
							.getBytes(), key.getBytes());
					result = (E) GsonUtil.parseJson(new String(bytes), classE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Boolean hashMultiSet(final Map<String, E> map) {
		final Map<byte[], byte[]> bytesMap = new HashMap<byte[], byte[]>();
		for (Entry<String, E> entry : map.entrySet()) {
			String jsonString = GsonUtil.toJsonString(entry.getValue());
			bytesMap.put(entry.getKey().getBytes(), jsonString.getBytes());
		}
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = false;
				try {
					redisconnection.hMSet(getTableName().getBytes(), bytesMap);
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public List<E> hashMultiGet(String[] keys) {
		if (classE == null) {
			init();
		}
		final byte[][] bytesOf2d = new byte[keys.length][];
		for (int i = 0; i < keys.length; i++) {
			bytesOf2d[i] = keys[i].getBytes();
		}
		return redisTemplate.execute(new RedisCallback<List<E>>() {
			@Override
			public List<E> doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				List<E> result = null;
				try {
					List<byte[]> list = redisconnection.hMGet(getTableName()
							.getBytes(), bytesOf2d);
					result = new ArrayList<E>();
					for (byte[] bytes : list) {
						E e = (E) GsonUtil.parseJson(new String(bytes), classE);
						result.add(e);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Map<String, E> hashGetAll() {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<Map<String, E>>() {
			@Override
			public Map<String, E> doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Map<String, E> result = null;
				try {
					Map<byte[], byte[]> map = redisconnection
							.hGetAll(getTableName().getBytes());
					result = new HashMap<String, E>();
					for (Entry<byte[], byte[]> entry : map.entrySet()) {
						E e = (E) GsonUtil.parseJson(
								new String(entry.getValue()), classE);
						result.put(new String(entry.getKey()), e);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Long hashCount() {
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Long result = null;
				try {
					result = redisconnection.hLen(getTableName().getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public List<String> hashKeys() {
		return redisTemplate.execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				List<String> result = null;
				try {
					Set<byte[]> set = redisconnection.hKeys(getTableName()
							.getBytes());
					result = new ArrayList<String>();
					for (byte[] bytes : set) {
						result.add(new String(bytes));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public List<E> hashVals() {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<List<E>>() {
			@Override
			public List<E> doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				List<E> result = null;
				try {
					Set<byte[]> set = redisconnection.hKeys(getTableName()
							.getBytes());
					result = new ArrayList<E>();
					for (byte[] bytes : set) {
						E e = (E) GsonUtil.parseJson(new String(bytes), classE);
						result.add(e);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Boolean hashExists(final String key) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = null;
				try {
					result = redisconnection.hExists(getTableName().getBytes(),
							key.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}
}