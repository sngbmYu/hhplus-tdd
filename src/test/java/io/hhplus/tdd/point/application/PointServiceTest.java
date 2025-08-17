package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.PointHistoryRepository;
import io.hhplus.tdd.point.infrastructure.UserPointRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@Mock
	private UserPointRepository userPointRepository;
	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@InjectMocks
	private PointServiceImpl pointService;

	// 테스트 작성 이유: 정상 시나리오에서 서비스가 올바르게 동작하는지 검증하기 위해
	@Test
	@DisplayName("회원 아이디로 포인트를 조회하면 해당하는 UserPoint 객체가 반환된다.")
	void givenId_whenFindPoint_thenReturnUserPoint() {
		// given
		long id = 0L;
		when(userPointRepository.findById(id))
			.thenReturn(new UserPoint(id, 100L, System.currentTimeMillis()));

		// when
		UserPoint userPoint = pointService.findUserPointById(id);

		// then
		assertThat(userPoint.point()).isEqualTo(100L);
	}

	// 테스트 작성 이유: DB에 존재하지 않는 아이디가 주어졌을 때, 서비스가 예외 시나리오를 정상적으로 처리하는지 검증하기 위해
	@Test
	@DisplayName("존재하지 않는 회원 아이디로 포인트를 조회하면 포인트가 0으로 초기화된 UserPoint 객체가 반환된다.")
	void givenNotExistId_whenFindPoint_thenReturnZeroPoint() {
		// given
		long id = 1L;
		when(userPointRepository.findById(id))
			.thenReturn(UserPoint.empty(id));

		// when
		UserPoint userPoint = pointService.findUserPointById(id);

		// then
		assertThat(userPoint.point()).isEqualTo(0L);
	}

	// 테스트 작성 이유: 정상 시나리오에서 서비스가 올바르게 동작하는지 검증하기 위해
	@Test
	@DisplayName("회원 아이디로 포인트 내역을 조회하면 해당하는 PointHistory의 리스트가 반환된다.")
	void givenId_whenFindPointHistory_thenReturnPointHistoryList() {
		// given
		long userId = 0L;
		PointHistory sampleHistory = new PointHistory(0L, userId, 1000L, TransactionType.CHARGE,
			System.currentTimeMillis());
		when(pointHistoryRepository.findAllByUserId(userId))
			.thenReturn(List.of(sampleHistory));

		// when
		List<PointHistory> pointHistoryList = pointService.findPointHistoriesByUserId(userId);

		// then
		assertThat(pointHistoryList).isNotEmpty();
		assertThat(pointHistoryList.get(0)).isEqualTo(sampleHistory);
	}

	// 테스트 작성 이유: DB에 존재하지 않는 아이디가 주어졌을 때, 서비스가 예외 시나리오를 정상적으로 처리하는지 검증하기 위해
	@Test
	@DisplayName("존재하지 않는 회원 아이디로 포인트 내역을 조회하면 빈 PointHistory 리스트가 반환된다.")
	void givenNotExistId_whenFindPointHistory_thenReturnEmptyPointHistoryList() {
		// given
		long userId = 1L;
		when(pointHistoryRepository.findAllByUserId(userId))
			.thenReturn(List.of());

		// when
		List<PointHistory> pointHistoryList = pointService.findPointHistoriesByUserId(userId);

		// then
		assertThat(pointHistoryList).isEmpty();
	}
}
