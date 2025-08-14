package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.UserPoint;

public interface PointService {

    UserPoint findUserPointById(long id);
}
