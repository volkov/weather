![](https://github.com/volkov/weather/workflows/Tests/badge.svg)
## Weather
Gets data from [openweathermap](https://openweathermap.org/api/one-call-api)

```bash
./gradlew build
docker build . -t weather
docker run --network host -e OPENWEATHER_TOKEN=<APITOKEN> --rm weather
```

Try to [get forecasts](http://localhost:8080/498817) or [forecast diffs](http://localhost:8080/498817/diffs)

## Some commands for testing jdbc
```bash
sudo lsof -P -i -n | grep java

sudo iptables -I INPUT -s 10.128.0.20 -j DROP
sudo iptables -L -nv --line-numbers
sudo iptables -D INPUT 1
``` 

## Upload data
```bash
http PUT localhost:8080/api locationId=1 timestamp='2023-07-27T16:45:09.237759722+02:00' temperature=1 rain=1 secret:secret
```

## Kafka commands
```bash
kafka-topics --list --bootstrap-server localhost:9092
```