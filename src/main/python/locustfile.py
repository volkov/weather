from locust import HttpUser, task, between

class QuickstartUser(HttpUser):
    wait_time = between(1, 1)
    @task
    def index_page(self):
        self.client.get("/498817/diffs?timestamp=2020-06-18T21:00:00.000%2B03:00")