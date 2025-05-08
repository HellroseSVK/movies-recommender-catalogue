const {faker} = require('@faker-js/faker');
const axios = require('axios').default;

const numberOfFeedbacks = process.argv[2] || 100;

function generateUser() {
    return {
        id: faker.helpers.rangeToNumber({ min: 1, max:  564999}) + "@muni.cz",
    }
}

function fakeUsers() {
    let users = [];
    for (let i = 0; i < numberOfFeedbacks; i++) {
        const user = generateUser();
        users.push(user);
    }
    return users;
}

function fetchMovies() {
    return axios.get("http://localhost:8081/api/v1/movie")
        .then(response => response.data)
        .catch(() => {
            console.error("Error fetching film persons, assuming empty list")
            return [];
        });
}

function fetchRatingCategories() {
    return axios.get("http://localhost:8084/api/v1/rating/category")
        .then(response => response.data)
        .catch(() => {
            console.error("Error fetching rating categories, assuming empty list");
            return [];
        });
}


(async function seedAll() {
    const bearer = process.env.BEARER_TOKEN;

    const header = {
        headers: {
            'Authorization': `Bearer ${bearer}`,
        }
    }

    let users = await fakeUsers();
    console.log("Faked users:", users);
    let movies = await fetchMovies();
    console.log("Fetched movies:", movies);
    let ratingCategories = await fetchRatingCategories();
    console.log("Fetched rating categories:", ratingCategories);
    const { comments } = require('./seed/comment.json');

    const ratingCategoriesNames = require('./seed/ratingCategory.json').ratingCategories;

    // Rating Categories

    function generateRatingCategory() {
        return {
            name: faker.helpers.arrayElement(ratingCategoriesNames),
        }
    }

    async function createRatingCategory() {
        const ratingCategory = generateRatingCategory();
        try {
            const response = await axios.post("http://localhost:8084/api/v1/rating/category", ratingCategory, header);
            console.log("Rating Category created:", response.data);
            ratingCategories.push(response.data);
        } catch (error) {
            console.error("Error creating rating category:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfFeedbacks; i++) {
        await createRatingCategory();
    }

    // Ratings

    function generateRating() {
        const numberOfRatingCategories = ratingCategories.length === 0
            ? 0
            : faker.helpers.rangeToNumber({
                min: 1,
                max: ratingCategories.length > 10 ? 10 : ratingCategories.length
            });

        const uniqueRatingCategories =  faker.helpers.arrayElements(ratingCategories, numberOfRatingCategories)
            .map((category) => {
                return {
                    ratingCategoryId: category.id,
                    rating: faker.helpers.rangeToNumber({ min: 0, max: 100 }),
                }
            });

        return {
            userId: faker.helpers.arrayElement(users).id,
            movieId: faker.helpers.arrayElement(movies).id,
            ratingDetails: uniqueRatingCategories,
            comment : faker.helpers.arrayElement(comments),
        }
    }

    async function createRating() {
        const rating = generateRating();
        try {
            const response = await axios.post("http://localhost:8084/api/v1/rating", rating, header);
            console.log("Rating created:", response.data);
        } catch (error) {
            console.error("Error creating rating:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfFeedbacks; i++) {
        await createRating();
    }
})();


