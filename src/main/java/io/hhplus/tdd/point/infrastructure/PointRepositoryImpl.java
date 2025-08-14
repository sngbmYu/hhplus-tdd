package io.hhplus.tdd.point.infrastructure;

import io.hhplus.tdd.point.domain.UserPoint;
import org.springframework.stereotype.Repository;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Override
    public UserPoint findById(long id) {
        return null;
    }
}
