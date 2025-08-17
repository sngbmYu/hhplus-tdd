package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.UserPointRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

	private final UserPointRepository userPointRepository;

	@Override
	public UserPoint findUserPointById(long id) {
		return userPointRepository.findById(id);
	}
}
