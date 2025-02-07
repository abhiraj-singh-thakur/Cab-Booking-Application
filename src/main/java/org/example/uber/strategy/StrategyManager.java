package org.example.uber.strategy;


import lombok.RequiredArgsConstructor;
import org.example.uber.strategy.impl.DriverMatchingHighestRateDriverStrategy;
import org.example.uber.strategy.impl.DriverMatchingNearestDriverStrategy;
import org.example.uber.strategy.impl.RideFareDefaultFareCalculationStrategyStrategy;
import org.example.uber.strategy.impl.RideFareSurgePricingFareCalculationStrategyStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


@RequiredArgsConstructor
@Component
public class StrategyManager {

    private final DriverMatchingHighestRateDriverStrategy highestRateDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategyStrategy fareSurgePricingFareCalculationStrategyStrategy;
    private final RideFareDefaultFareCalculationStrategyStrategy fareDefaultFareCalculationStrategyStrategy;


    public DriverMatchingStrategy driverMatchingStrategy(double rideRatings) {
        if (rideRatings >= 4.5)
            return highestRateDriverStrategy;
        else
            return nearestDriverStrategy;

    }

    public RideFairCalculationStrategy rideFairCalculationStrategy() {
        LocalTime surgeStartTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);

        return isSurgeTime ? fareSurgePricingFareCalculationStrategyStrategy : fareDefaultFareCalculationStrategyStrategy;
    }
}
