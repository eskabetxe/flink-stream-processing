package pro.boto.flink.adapters;

import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import pro.boto.flink.adapters.sensors.DoorSensor;
import pro.boto.flink.adapters.sensors.PressureSensor;
import pro.boto.flink.adapters.sensors.SawtoothSensor;
import pro.boto.flink.adapters.sensors.TempSensor;
import pro.boto.flink.adapters.sensors.TimeSensor;
import pro.boto.flink.domain.Sensor;

public class SensorSource extends RichSourceFunction<Sensor<Double>> implements CheckpointedFunction {
  private volatile boolean running = true;

  private final TimeSensor timeSensor;
  private final SawtoothSensor sawtoothSensor;

  public SensorSource( int slowdownFactor, int numSteps){
    this.timeSensor = new TimeSensor(100, slowdownFactor);
    this.sawtoothSensor = new SawtoothSensor(numSteps);
  }

  @Override
  public void run(SourceContext<Sensor<Double>> ctx) throws Exception {
    while (running) {
      synchronized (ctx.getCheckpointLock()) {
        var timeValue = timeSensor.map();
        var sawtoothValue = sawtoothSensor.map(timeValue);

        var tempSensor = TempSensor.map(sawtoothValue);
        ctx.collectWithTimestamp(tempSensor, timeValue);

        var pressureSensor = PressureSensor.map(sawtoothValue);
        ctx.collectWithTimestamp(pressureSensor, timeValue);

        var doorSensor = DoorSensor.map(sawtoothValue);
        ctx.collectWithTimestamp(doorSensor, timeValue);

        ctx.emitWatermark(new Watermark(timeValue));
      }
      timeSensor.timeSync();
    }
  }

  @Override
  public void cancel() {
    running = false;
  }

  @Override
  public void snapshotState(FunctionSnapshotContext context) throws Exception {
    timeSensor.snapshotState(context);
    sawtoothSensor.snapshotState(context);
  }

  @Override
  public void initializeState(FunctionInitializationContext context) throws Exception {
    timeSensor.initializeState(context);
    sawtoothSensor.initializeState(context);
  }

}
