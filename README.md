![](https://github.com/volkov/weather/workflows/Tests/badge.svg)
## Weather
Gets data from [openweathermap](https://openweathermap.org/api/one-call-api)

```bash
./gradlew build
docker build . -t weather
docker run --network host -e OPENWEATHER_TOKEN=<APITOKEN> --rm weather
```

Try [get forecast](http://localhost:8080/498817)