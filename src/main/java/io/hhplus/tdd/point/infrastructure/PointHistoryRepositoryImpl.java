package io.hhplus.tdd.point.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

	private final PointHistoryTable pointHistoryTable;

	@Override
	public List<PointHistory> findAllByUserId(long userId) {
		return pointHistoryTable.selectAllByUserId(userId);
	}

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return null;
    }
}
