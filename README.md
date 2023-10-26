# Stream Processing with Apache Flink

## Setup

For this demo a local Grafana and InfluxDB instance is needed.

These can be setup via:
```cd docker && docker-compose up -d```Y

It will take about one minute until the datasource and dashboard is added to Grafana. 

Then you need to config the Grafana executing the:
```cd docker && ./setup.sh```

Grafana is served on `localhost:3000`. 

In addition, a local Flink cluster is needed (not included in above docker setup).
You can download this [here](https://www.apache.org/dyn/closer.lua/flink/flink-1.17.1/flink-1.17.1-bin-scala_2.12.tgz)

You will need to change on file `conf/flink-conf.yaml`, these configurations:
```
state.checkpoints.dir: file:///tmp/flink-checkpoints
state.savepoints.dir: file:///tmp/flink-savepoints
```

## Disclaimer
Apache®, Apache Flink™, Flink™, and the Apache feather logo are trademarks of [The Apache Software Foundation](http://apache.org).
