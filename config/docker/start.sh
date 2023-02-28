#!/bin/bash
cd /usr/local
echo 4
java -jar \
  -Dspring.datasource-primary.url=jdbc:postgresql://pg-secondary-postgresql:5432/dw \
  -Dspring.datasource-secondary.url=jdbc:postgresql://pg-primary-postgresql:5432/examplesys \
  sys-golf-etl-0.0.1.jar
