package pro.boto.flink.adapters.sensors;

import pro.boto.flink.domain.Sensor;

public class TempSensor {

    public static Sensor<Double> map(Sensor<Double> sensor) throws Exception {
        return sensor.withName("temp");
    }
}
