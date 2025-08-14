package io.hhplus.tdd.point.infrastructure;

import io.hhplus.tdd.point.domain.UserPoint;

public interface PointRepository {

    UserPoint findById(long id);
}
