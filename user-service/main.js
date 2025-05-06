const {faker} = require('@faker-js/faker');
const axios = require('axios').default;

const numberOfUsers = process.argv[2] || 100;

(async function seedUsers() {
    const bearer = process.env.BEARER_TOKEN;

    const header = {
        headers: {
            'Authorization': `Bearer ${bearer}`,
        }
    }

    function generateUser() {
        return {
            username: faker.internet.displayName(),
            password: faker.internet.password(),
            email: faker.internet.email(),
            roles: [faker.helpers.arrayElement(["ADMIN", "USER"])]
        };
    }

    async function createUser() {
        const user = generateUser();
        try {
            const response = await axios.post("http://localhost:8083/api/v1/user", user, header);
            console.log("User created:", response.data);
        } catch (error) {
            console.error("Error creating user:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfUsers; i++) {
        await createUser();
    }
})();