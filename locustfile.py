import jwt
import os
import random
from locust import HttpUser, task, between


class UserScenario(HttpUser):
    host = ""
    wait_time = between(1, 3)

    def on_start(self):
        self.headers = self.init_auth()
        self.recommendation_base = "http://localhost:8084"
        self.feedback_base = "http://localhost:8083"

        res = self.client.get(f"{self.feedback_base}/api/v1/rating/category", headers=self.headers)
        self.category_ids = [c["id"] for c in res.json()]

    def init_auth(self):
        token = os.getenv("BEARER_TOKEN")
        decoded = jwt.decode(token, options={"verify_signature": False})
        self.user_id = decoded.get("sub")
        return {"Authorization": f"Bearer {token}"}

    @task(3)
    def get_recommendations(self):
        category_id = random.choice(self.category_ids)

        with self.client.get(
            f"{self.recommendation_base}/api/v1/recommendation/by-categories/0?categoryIds={category_id}",
            headers=self.headers,
            catch_response=True
        ) as res:
            self.last_movie_id = random.choice(res.json())["id"]
            self.last_category_id = category_id
            res.success()

    @task(2)
    def submit_rating(self):
        payload = {
            "userId": self.user_id,
            "movieId": self.last_movie_id,
            "ratingDetails": [{
                "ratingCategoryId": self.last_category_id,
                "score": random.randint(1, 5)
            }],
            "comment": "Load test rating"
        }

        with self.client.post(
            f"{self.feedback_base}/api/v1/rating",
            json=payload,
            headers=self.headers,
            catch_response=True
        ) as res:
            res.success()


class MovieRecommendationUser(HttpUser):
    host = ""
    wait_time = between(2, 4)

    def on_start(self):
        token = os.getenv("BEARER_TOKEN")
        decoded = jwt.decode(token, options={"verify_signature": False})
        self.user_id = decoded.get("sub")
        self.headers = {"Authorization": f"Bearer {token}"}

        self.movie_service = "http://localhost:8081"
        self.recommendation_service = "http://localhost:8084"

        res = self.client.get(f"{self.movie_service}/api/v1/movie", headers=self.headers)
        self.movie_ids = [movie["id"] for movie in res.json()]

    @task(2)
    def view_movie_detail(self):
        movie_id = random.choice(self.movie_ids)
        self.last_movie_id = movie_id

        self.client.get(
            f"{self.movie_service}/api/v1/movie/{movie_id}",
            headers=self.headers
        )

    @task(1)
    def get_similar_movies(self):
        if not hasattr(self, "last_movie_id"):
            return

        self.client.get(
            f"{self.recommendation_service}/api/v1/recommendation/by-movie/{self.last_movie_id}",
            headers=self.headers
        )
