# petstore-data-service

[![codecov](https://codecov.io/github/coding2themax/petstore-data-service/graph/badge.svg)](https://codecov.io/github/coding2themax/petstore-data-service)![GitHub License](https://img.shields.io/github/license/coding2themax/petstore-data-service)

petstore-data-service

The petstore-data-service is a backend service that handles data management for the petstore application. It provides a set of APIs for creating, retrieving, updating, and deleting pet data.

Key Features:

- Pet CRUD Operations: The service allows users to perform CRUD (Create, Read, Update, Delete) operations on pet data. This includes adding new pets, retrieving pet details, updating pet information, and deleting pets from the system.
- Data Validation: The service ensures that the data being stored or retrieved is valid and follows the defined data schema. It performs validation checks to maintain data integrity and consistency.
- Authentication and Authorization: The service implements authentication and authorization mechanisms to ensure that only authorized users can access and modify pet data.
- Performance and Scalability: The service is designed to handle a large volume of pet data efficiently. It is optimized for performance and can scale horizontally to accommodate increased traffic and data storage requirements.

Please refer to the documentation for detailed information on how to use the petstore-data-service and its APIs.

This service will be using the swagger open api file from https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml

## Horse feeding schedules

The `/horse` routes store horses and their feeding schedules (tables `petstore.horse` and `petstore.feeding_schedule`).

| Method | Path | Description |
| --- | --- | --- |
| GET | `/horse/all` | All horses with their feeding schedules |
| GET | `/horse/{horseId}` | One horse with its feeding schedule |
| POST | `/horse` | Create a horse (feeding schedules optional) |
| PUT | `/horse/{horseId}` | Update a horse and replace its feeding schedules |
| DELETE | `/horse/{horseId}` | Delete a horse and its feeding schedules |
| GET | `/horse/{horseId}/feeding-schedule` | A horse's feedings, in time order |
| POST | `/horse/{horseId}/feeding-schedule` | Add one feeding |
| DELETE | `/horse/{horseId}/feeding-schedule/{scheduleId}` | Remove one feeding |

Example body for `POST /horse`:

```json
{
  "name": "Thunder",
  "breed": "Thoroughbred",
  "age": 7,
  "owner": "Alex",
  "feedingSchedules": [
    { "feedingTime": "07:00", "feedType": "Hay", "quantity": 5, "unit": "kg", "notes": "Morning feed" },
    { "feedingTime": "18:00", "feedType": "Oats", "quantity": 2.5, "unit": "kg" }
  ]
}
```

Unknown horses or feedings return `404`; invalid bodies (missing name, missing feeding time, non-positive quantity) return `400`.
