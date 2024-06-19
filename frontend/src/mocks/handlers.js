import { rest } from 'msw'

const authPlayer = {
    "authority": "PLAYER"
};

const userAdmin1 = {
    "id": 1,
    "username": "admin1",
    "authority": {
        "authority": "ADMIN"
    }
};

const userPlayer1 = {
    "id": 2,
    "username": "player1",
    "authority": authPlayer
};

const userPlayer2 = {
    "id": 3,
    "username": "player2",
    "authority": authPlayer
};

const userVet1 = {
    "id": 12,
    "username": "vet1",
    "authority": {
        "authority": "VET"
    }
};

const userVet2 = {
    "id": 13,
    "username": "vet2",
    "authority": {
        "authority": "VET"
    }
};

const player1 = {
    "id": 1,
    "firstName": "George",
    "lastName": "Franklin",
    "address": "110 W. Liberty St.",
    "city": "Sevilla",
    "telephone": "608555103",
    "plan": "PLATINUM",
    "user": userPlayer1
};

const player2 = {
    "id": 2,
    "firstName": "Betty",
    "lastName": "Davis",
    "address": "638 Cardinal Ave.",
    "city": "Sevilla",
    "telephone": "608555174",
    "plan": "PLATINUM",
    "user": userPlayer2
};

const pet1 = {
    "id": 1,
    "name": "Leo",
    "birthDate": "2010-09-07",
    "type": {
        "id": 1,
        "name": "cat"
    },
    "player": player1
};

const pet2 = {
    "id": 2,
    "name": "Basil",
    "birthDate": "2012-08-06",
    "type": {
        "id": 6,
        "name": "hamster"
    },
    "player": player2
};

const vet1 = {
    "id": 1,
    "firstName": "James",
    "lastName": "Carter",
    "specialties": [],
    "user": userVet1,
    "city": "Sevilla"
}

const vet2 = {
    "id": 2,
    "firstName": "Helen",
    "lastName": "Leary",
    "specialties": [
        {
            "id": 1,
            "name": "radiology"
        }
    ],
    "user": userVet2,
    "city": "Sevilla"
};

const visit1 = {
    "id": 1,
    "datetime": "2013-01-01T13:00:00",
    "description": "rabies shot",
    "pet": pet1,
    "vet": vet1,
    "city": "Badajoz",
};

const visit2 = {
    "id": 2,
    "datetime": "2013-01-02T15:30:00",
    "description": "",
    "pet": pet1,
    "vet": vet2
};

const consultation1 = {
    "id": 1,
    "title": "Mi gato no come",
    "status": "ANSWERED",
    "player": player2,
    "pet": pet1,
    "creationDate": "2023-04-11T11:20:00"
};

const consultation2 = {
    "id": 2,
    "title": "Título 2",
    "status": "PENDING",
    "player": player1,
    "pet": pet1,
    "creationDate": "2023-04-11T11:20:00"
};

const ticket1 = {
    "id": 1,
    "description": "What vaccine should my dog recieve?",
    "creationDate": "2023-01-04T17:32:00",
    "user": userPlayer1,
    "consultation": consultation1
};

const ticket2 = {
    "id": 2,
    "description": "Rabies' one.",
    "creationDate": "2023-01-04T17:36:00",
    "user": userVet1,
    "consultation": consultation1
}

export const handlers = [
    rest.delete('*/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json({
                message: "Entity deleted"
            }),
        )
    }),

    rest.get('*/api/v1/players', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                player1,
                player2,
            ]),
        )
    }),

    rest.get('*/api/v1/pets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                pet1,
                pet2,
            ]),
        )
    }),

    rest.get('*/api/v1/users', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                userAdmin1,
                userPlayer1,
            ]),
        )
    }),

    rest.get('*/api/v1/vets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                vet1,
                vet2,
            ]),
        )
    }),

    rest.get('*/api/v1/vets/specialties', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                {
                    "id": 1,
                    "name": "radiology"
                },
                {
                    "id": 2,
                    "name": "surgery"
                },
                {
                    "id": 3,
                    "name": "dentistry"
                }
            ]),
        )
    }),

    rest.get('*/api/v1/pets/:petId/visits', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                visit1,
                visit2,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                consultation1,
                consultation2,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                consultation1,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations/:id/tickets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                ticket1,
                ticket2
            ]),
        )
    }),

    rest.post('*/api/v1/consultations/:id/tickets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(
                {
                    "id": 3,
                    "description": "test ticket",
                    "creationDate": "2023-01-04T17:32:00",
                    "user": userPlayer1,
                    "consultation": consultation1
                },
            ))
    }),

    rest.put('*/api/v1/consultations/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(
                {
                    "id": 1,
                    "title": "Consulta sobre vacunas",
                    "status": "CLOSED",
                    "player": player1,
                    "pet": pet1,
                    "creationDate": "2023-01-04T17:30:00"
                }
            )
        )
    }),

]