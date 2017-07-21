/**
 * 
 */
package cn.himma.redis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
public abstract class AbsRedisListDao<E> implements IRedisListDao<E> {

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
	public Boolean leftPush(final E e) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				String jsonString = GsonUtil.toJsonString(e);
				try {
					Long index = redisconnection.rPush(getTableName()
							.toString().getBytes(), jsonString.getBytes());
					System.out.println(index);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
	}

	@Override
	public Boolean rightPush(final E e) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				String jsonString = GsonUtil.toJsonString(e);
				try {
					Long index = redisconnection.rPush(getTableName()
							.toString().getBytes(), jsonString.getBytes());
					System.out.println(index);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
	}

	@Override
	public E leftPop() {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<E>() {
			@Override
			public E doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				E result = null;
				try {
					byte[] rPop = redisconnection.rPop(getTableName()
							.getBytes());
					if (rPop == null) {
						return result;
					}
					String jsonString = new String(rPop);
					result = (E) GsonUtil.parseJson(jsonString, classE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public E rightPop() {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<E>() {
			@Override
			public E doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				E result = null;
				try {
					byte[] rPop = redisconnection.rPop(getTableName()
							.getBytes());
					if (rPop == null) {
						return result;
					}
					String jsonString = new String(rPop);
					Object parseJson = GsonUtil.parseJson(jsonString, classE);
					result = (E) parseJson;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
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
	public Boolean deleteTable() {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Boolean result = false;
				try {
					redisconnection.del(getTableName().getBytes());
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public List<E> leftQuery(final long start, final long end) {
		if (classE == null) {
			init();
		}
		return redisTemplate.execute(new RedisCallback<List<E>>() {
			@Override
			public List<E> doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				List<E> result = new ArrayList<E>();
				try {
					List<byte[]> list = redisconnection.lRange(getTableName()
							.getBytes(), start, end);
					for (byte[] bytes : list) {
						Object parseJson = GsonUtil.parseJson(
								new String(bytes), classE);
						result.add((E) parseJson);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	@Override
	public Long leftCount() {
		return redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection redisconnection)
					throws DataAccessException {
				Long size = null;
				try {
					size = redisconnection.lLen(getTableName().getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return size;
			}
		});
	}
}