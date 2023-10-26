package pro.boto.flink.process;

import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;
import pro.boto.flink.domain.ControlMessage;
import pro.boto.flink.domain.Sensor;

public class AmplifierFunction extends RichCoFlatMapFunction<Sensor<Double>, ControlMessage, Sensor<Double>> {
  private final ValueStateDescriptor<Double> stateDesc = new ValueStateDescriptor<>("amplitude", Double.class);

  @Override
  public void flatMap1(Sensor<Double> sensor, Collector<Sensor<Double>> collector) throws Exception {
    Double amplitude = getRuntimeContext().getState(stateDesc).value();
    if (amplitude == null) {
      amplitude = 0.0;
      getRuntimeContext().getState(stateDesc).update(amplitude);
    }
    collector.collect(sensor.withValue(sensor.value * amplitude));
  }

  @Override
  public void flatMap2(ControlMessage msg, Collector<Sensor<Double>> collector) throws Exception {
    getRuntimeContext().getState(stateDesc).update(msg.amplitude);
  }
}
