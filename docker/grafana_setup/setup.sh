#!/usr/bin/env bash

curl -u admin:admin http://localhost:3000/api/datasources -d @./datasource.json --header "Content-Type: application/json"
curl -u admin:admin http://localhost:3000/api/dashboards/db -d @./dashboard.json --header "Content-Type: application/json"
