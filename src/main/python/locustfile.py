from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 1)
    @task
    def index_page(self):
        self.client.get("/api/498817/forecast?duration=P3D")

    # @task
    # def put(self):
    #     self.client.put("/", json={"locationId": -1, "timestamp": "2020-07-19T17:12:56+03:00"}, headers={"Secret": "secret"})