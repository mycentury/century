package cn.himma.redis;

import java.util.List;

public interface IRedisListDao<E> {

	Boolean leftPush(final E e);

	Boolean rightPush(final E e);

	E leftPop();

	E rightPop();

	@Deprecated
	Boolean flushDb();

	Boolean deleteTable();

	List<E> leftQuery(long start, long end);

	Long leftCount();
}
