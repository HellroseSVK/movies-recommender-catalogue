const {faker} = require('@faker-js/faker');
const axios = require('axios').default;

const numberOfFilmPerson = process.argv[2] || 100;

const { biographies } = require('./seed/biography.json');


(async function seedFilmPersons() {
    const bearer = process.env.BEARER_TOKEN;

    const header = {
        headers: {
            'Authorization': `Bearer ${bearer}`,
        }
    }

    function generateFilmPerson() {
        const sex = faker.person.sexType();
        return {
            name: faker.person.fullName({sex}),
            image: faker.image.personPortrait({sex}),
            dateOfBirth: faker.date.birthdate().toISOString().split('T')[0],
            biography: faker.helpers.arrayElement(biographies),
        };
    }

    async function createFilmPerson() {
        const filmPerson = generateFilmPerson();
        try {
            const response = await axios.post("http://localhost:8082/api/v1/filmPerson", filmPerson, header);
            console.log("Film Person created:", response.data);
        } catch (error) {
            console.error("Error creating film person:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfFilmPerson; i++) {
        await createFilmPerson();
    }
})();