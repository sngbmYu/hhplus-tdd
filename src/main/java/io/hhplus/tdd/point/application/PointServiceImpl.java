package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastructure.PointRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

	private final PointRepository pointRepository;

	@Override
	public UserPoint findUserPointById(long id) {
		return pointRepository.findById(id);
	}
}
