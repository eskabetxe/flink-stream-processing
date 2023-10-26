package pro.boto.flink.adapters.sensors;

import pro.boto.flink.domain.Sensor;

public class PressureSensor {

    public static Sensor<Double> map(Sensor<Double> sensor) throws Exception {
        double phase = sensor.value * 2 * Math.PI;
        return sensor
                .withValue(Math.sin(phase))
                .withName("pressure");
    }
}
