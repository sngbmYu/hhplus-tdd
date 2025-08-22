package io.hhplus.tdd.point.application;

import io.hhplus.tdd.common.exception.ChargePointFailureException;
import io.hhplus.tdd.common.exception.InvariantViolationException;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.UserPointRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;

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
        if (amount <= 0) {
            throw new InvariantViolationException();
        }

        final String errorMessage = "포인트 충전에 실패했습니다. 잠시 후 다시 시도해주세요.";

        UserPoint userPoint = userPointRepository.findById(userId);

        long newPoint = userPoint.point() + amount;
        long currentTime = System.currentTimeMillis();
        UserPoint newUserPoint = new UserPoint(userId, newPoint, currentTime);

        UserPoint savedUserPoint;
        try {
            savedUserPoint = userPointRepository.save(newUserPoint);
        } catch (Exception e) {
            throw new ChargePointFailureException(errorMessage, e);
        }

        long tempId = 0L; // id는 auto-increment
        long savedUserId = savedUserPoint.id();
        PointHistory pointHistory = new PointHistory(tempId, savedUserId, amount, CHARGE, currentTime);

        try {
            pointHistoryRepository.save(pointHistory);
        } catch (Exception e) {
            throw new ChargePointFailureException(errorMessage, e);
        }

        return savedUserPoint;
    }
}
