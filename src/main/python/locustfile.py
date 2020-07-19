from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 1)
    @task
    def index_page(self):
        self.client.get("/498817/diffs?timestamp=2020-06-18T21:00:00.000%2B03:00")

    @task
    def put(self):
        self.client.put("/", json={"locationId": -1, "timestamp": "2020-07-19T17:12:56+03:00"}, headers={"Secret": "secret"})