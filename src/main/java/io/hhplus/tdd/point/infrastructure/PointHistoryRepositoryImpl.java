package io.hhplus.tdd.point.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.hhplus.tdd.point.domain.PointHistory;

@Repository
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

	@Override
	public List<PointHistory> findAllByUserId(long id) {
		return null;
	}
}
