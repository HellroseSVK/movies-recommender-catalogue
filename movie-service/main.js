const {faker} = require('@faker-js/faker');
const axios = require('axios').default;

const numberOfUsers = process.argv[2] || 100;

function fetchGenres() {
    return axios.get("http://localhost:8081/api/v1/genre")
        .then(response => response.data)
        .catch(() => {
            console.error("Error fetching genres, assuming empty list");
            return [];
        });
}

function fetchFilmPersons() {
    return axios.get("http://localhost:8082/api/v1/filmPerson")
        .then(response => response.data)
        .catch(() => {
            console.error("Error fetching film persons, assuming empty list")
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

    let genres = await fetchGenres();
    console.log("Fetched genres:", genres);
    let filmPersons = await fetchFilmPersons();
    console.log("Fetched film persons:", filmPersons);

    const genresDescriptions = require('./seed/genre.json').genres;

    // Genres

    function generateGenre() {
        return {
            name: faker.helpers.arrayElement(genresDescriptions).name,
        };
    }

    async function createGenre() {
        const genre = generateGenre();
        try {
            const response = await axios.post("http://localhost:8081/api/v1/genre", genre, header);
            console.log("Genre created:", response.data);
            genres.push(response.data);
        } catch (error) {
            console.error("Error creating genre:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfUsers; i++) {
        await createGenre();
    }

    const availableGenres = genresDescriptions
        .filter(genre => genres.some(g => g.name === genre.name))
        .map(genre => {
            let g = genres.find(g => g.name === genre.name);
            return {
                id: g.id,
                name: genre.name,
                descriptions: genre.descriptions
            };
        });

    // Movies

    function generateMovie() {
        const selectedGenres = Array.from({
            length: faker.helpers.rangeToNumber({
                min: genres.length === 0 ? 0 : 1,
                max: genres.length > 3 ? 3 : genres.length
            })
        }, () => faker.helpers.arrayElement(availableGenres));
        const availableDescriptions = selectedGenres.flatMap(genre => genre.descriptions);

        const numberOfFilmPersons = filmPersons.length === 0
            ? 0
            : faker.helpers.rangeToNumber({
                min: 1,
                max: filmPersons.length > 10 ? 10 : filmPersons.length
            });

        const uniqueFilmPersons = faker.helpers.arrayElements(filmPersons, numberOfFilmPersons)

        return {
            title: faker.book.title(),
            description: faker.helpers.arrayElement(availableDescriptions),
            images: Array.from({
                length: faker.helpers.rangeToNumber({
                    min: 1,
                    max: 10
                })
            }, () => faker.image.urlPicsumPhotos()),
            genres: selectedGenres.map(genre => genre.id),
            filmPersonRoles: uniqueFilmPersons.map(person => {
                return {
                    filmPersonId: person.id,
                    role: faker.helpers.arrayElement([
                        "ACTOR",
                        "DIRECTOR",
                        "PRODUCER",
                        "SCREENWRITER",
                        "CINEMATOGRAPHER",
                        "COMPOSER",
                        "SOURCE_MATERIAL_AUTHOR"
                    ])
                };
            }),
        };
    }

    async function createMovie() {
        const movie = generateMovie();
        try {
            const response = await axios.post("http://localhost:8081/api/v1/movie", movie, header);
            console.log("Movie created:", response.data);
        } catch (error) {
            console.error("Error creating movie:", error.response ? error.response.data : error.message);
        }
    }

    for (let i = 0; i < numberOfUsers; i++) {
        await createMovie();
    }
})();


