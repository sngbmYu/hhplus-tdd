package io.hhplus.tdd.point.application;

import java.util.List;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.UserPointRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

	private final UserPointRepository userPointRepository;
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	public UserPoint findUserPointById(long id) {
		return userPointRepository.findById(id);
	}

	@Override
	public List<PointHistory> findPointHistoriesByUserId(long userId) {
		return pointHistoryRepository.findAllByUserId(userId);
	}

    @Override
    public UserPoint chargeUserPoint(long userId, long amount) {
        return null;
    }
}
