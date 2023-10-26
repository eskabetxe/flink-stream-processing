package pro.boto.flink.adapters.sensors;

import pro.boto.flink.domain.Sensor;

public class DoorSensor {

    public static Sensor<Double> map(Sensor<Double> dataPoint) throws Exception {
        double value = (dataPoint.value > 0.4) ? 1.0 :0.0;
        return dataPoint
                .withValue(value)
                .withName("door");
    }
}
