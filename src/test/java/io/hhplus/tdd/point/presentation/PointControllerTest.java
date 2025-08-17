package io.hhplus.tdd.point.presentation;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
class PointControllerTest {

	@Autowired
	private PointController pointController;

	// 테스트 작성 이유: 메서드 수준 Bean Validation(@PositiveOrZero)이 정상적으로 동작하는지 검증하기 위해
	@Test
	@DisplayName("회원 아이디가 음수로 주어졌을 때 포인트를 조회하면 ConstraintViolationException이 발생한다.")
	void givenNegativeId_whenFindPoint_thenThrowConstraintViolationException() {
		// given
		long zeroId = 0L;
		long negativeId = -1L;

		// when
		ThrowableAssert.ThrowingCallable zeroCase = () -> pointController.point(zeroId);
		ThrowableAssert.ThrowingCallable negativeCase = () -> pointController.point(negativeId);

		// then
		assertThatCode(zeroCase).doesNotThrowAnyException();
		assertThatThrownBy(negativeCase).isInstanceOf(ConstraintViolationException.class);
	}
}
