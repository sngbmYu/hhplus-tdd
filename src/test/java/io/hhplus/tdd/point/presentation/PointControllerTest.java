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

	// 테스트 작성 이유: 메서드 수준 Bean Validation(@PositiveOrZero)이 정상적으로 동작하는지 검증하기 위해
	@Test
	@DisplayName("회원 아이디가 음수로 주어졌을 때 포인트 내역을 조회하면 ConstraintViolationException이 발생한다.")
	void givenNegativeId_whenFindPointHistory_thenThrowConstraintViolationException() {
		// given
		long zeroUserId = 0L;
		long negativeUserId = -1L;

		// when
		ThrowableAssert.ThrowingCallable zeroCase = () -> pointController.history(zeroUserId);
		ThrowableAssert.ThrowingCallable negativeCase = () -> pointController.history(negativeUserId);

		// then
		assertThatCode(zeroCase).doesNotThrowAnyException();
		assertThatThrownBy(negativeCase).isInstanceOf(ConstraintViolationException.class);
	}

    // 테스트 작성 이유: 메서드 수준 Bean Validation(@PositiveOrZero)이 정상적으로 동작하는지 검증하기 위해
	@Test
	@DisplayName("회원 아이디가 음수로 주어졌을 때 포인트를 충전하면 ConstraintViolationException이 발생한다.")
	void givenNegativeId_whenChargePoint_thenThrowConstraintViolationException() {
		// given
		long zeroUserId = 0L;
		long negativeUserId = -1L;
        long amount = 100L;

		// when
		ThrowableAssert.ThrowingCallable zeroCase = () -> pointController.charge(zeroUserId, amount);
		ThrowableAssert.ThrowingCallable negativeCase = () -> pointController.charge(negativeUserId, amount);

		// then
		assertThatCode(zeroCase).doesNotThrowAnyException();
		assertThatThrownBy(negativeCase).isInstanceOf(ConstraintViolationException.class);
	}

    // 테스트 작성 이유: 메서드 수준 Bean Validation(@PositiveOrZero)이 정상적으로 동작하는지 검증하기 위해
	@Test
	@DisplayName("충전 금액으로 0 이하의 값이 주어졌을 때 포인트를 충전하면 ConstraintViolationException이 발생한다.")
	void givenLeZeroAmount_whenChargePoint_thenThrowConstraintViolationException() {
		// given
		long userId = 0L;
        long normalAmount = 100L;
		long zeroAmount = 0L;
        long negativeAmount = -1L;

		// when
        ThrowableAssert.ThrowingCallable normalCase = () -> pointController.charge(userId, normalAmount);
		ThrowableAssert.ThrowingCallable zeroCase = () -> pointController.charge(userId, zeroAmount);
		ThrowableAssert.ThrowingCallable negativeCase = () -> pointController.charge(userId, negativeAmount);

		// then
        assertThatCode(normalCase).doesNotThrowAnyException();
        assertThatThrownBy(zeroCase).isInstanceOf(ConstraintViolationException.class);
		assertThatThrownBy(negativeCase).isInstanceOf(ConstraintViolationException.class);
	}
}
