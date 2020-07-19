![](https://github.com/volkov/weather/workflows/Tests/badge.svg)
## Weather
Gets data from [openweathermap](https://openweathermap.org/api/one-call-api)

```bash
./gradlew build
docker build . -t weather
docker run --network host -e OPENWEATHER_TOKEN=<APITOKEN> --rm weather
```

Try [get forecasts](http://localhost:8080/498817) or [forecast diffs](http://localhost:8080/498817/diffs)

## Some commands for testing jdbc
```bash
sudo lsof -P -i -n | grep java

sudo iptables -I INPUT -s 10.128.0.20 -j DROP
sudo iptables -L -nv --line-numbers
sudo iptables -D INPUT 1
``` 