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

The `/horse` routes store horses and their feeding schedules. Each horse is one item in the DynamoDB `horse` table, with its feeding schedule stored inside it.

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

## Storage: Postgres or AWS DynamoDB

One build supports both. The `petstore.storage` setting picks which repository beans are created:

| Mode | How to select | Repositories |
| --- | --- | --- |
| Postgres (default) | nothing, or `petstore.storage=postgres` | `PetR2DBCService`, `HorseR2DBCService` |
| DynamoDB | `SPRING_PROFILES_ACTIVE=dynamodb` | `PetDynamoRepository`, `HorseDynamoRepository` |

The `dynamodb` profile (`application-dynamodb.yml`) sets `petstore.storage=dynamodb` and turns off the R2DBC auto-configuration, so no Postgres connection is needed. Use the profile rather than setting the property alone. Beans for each mode are marked with `@ConditionalOnPostgresStorage` or `@ConditionalOnDynamoDbStorage`.

Services and controllers depend only on the `PetRepository` and `HorseRepository` interfaces, so adding a storage type means adding implementations of those two interfaces.

Run locally with `docker compose --profile postgres up` or `docker compose --profile dynamodb up` (build the jar first with `./mvnw package`).

### Postgres

Tables are defined in `src/main/resources/schema.sql` (local image: `postgres/init.sql`). Settings: `DATABASE_HOST` (an `r2dbc:postgresql://...` URL), `DATABASE_USERNAME`, `DATABASE_PASSWORD`.

### DynamoDB

Data is stored through the AWS SDK v2 enhanced async client.

| Table | Key | Notes |
| --- | --- | --- |
| `<prefix>pet` | `petId` (N) | Category, photo URLs and tags are stored inside the item. The `status-index` GSI on `status` serves `/pet/findByStatus`, and the `tagNames` string set is used to filter by tag. |
| `<prefix>horse` | `horseId` (N) | The feeding schedule is stored inside the item. A `version` attribute adds optimistic locking: concurrent changes are retried instead of overwriting each other. |
| `<prefix>counter` | `name` (S) | Atomic counters that issue numeric ids for horses and feedings, because DynamoDB has no auto-increment. |

#### DynamoDB settings

| Property | Environment variable | Default |
| --- | --- | --- |
| `petstore.dynamodb.region` | `AWS_REGION` | `us-east-1` |
| `petstore.dynamodb.endpoint` | `DYNAMODB_ENDPOINT` | empty (uses AWS) |
| `petstore.dynamodb.table-prefix` | `DYNAMODB_TABLE_PREFIX` | `petstore-` |
| `petstore.dynamodb.access-key-id` / `secret-access-key` | `DYNAMODB_ACCESS_KEY_ID` / `DYNAMODB_SECRET_ACCESS_KEY` | empty (uses the default AWS credentials chain) |
| `petstore.dynamodb.create-tables` | `DYNAMODB_CREATE_TABLES` | `false` |
| `petstore.dynamodb.seed-data` | `DYNAMODB_SEED_DATA` | `false` |

In AWS, create the tables with your infrastructure code using the definitions in `DynamoDbTableInitializer`, and give the service an IAM role. The role needs `GetItem`, `PutItem`, `DeleteItem`, `UpdateItem`, `Scan` and `Query` on the three tables and the `status-index` index. `create-tables` and `seed-data` are for local development and tests.

With the compose `dynamodb` profile, the service creates the tables in DynamoDB Local and loads sample pets and horses.

### Tests

Both storage types must behave the same, so the repository tests are written once, in `PetRepositoryContract` and `HorseRepositoryContract`, and run against each backend:

- Postgres: `@DataR2dbcTest` slice against a Testcontainers Postgres (`PetR2DBCServiceTest`, `HorseR2DBCServiceTest`, which also checks transaction rollback).
- DynamoDB: the `@DynamoDbTest` slice (`src/test/java/.../support`) against an `amazon/dynamodb-local` container (`PetDynamoRepositoryTest`, `HorseDynamoRepositoryTest`).

`PostgresApiEndToEndTest` and `DynamoDbApiEndToEndTest` start the whole app in each mode and call the API over HTTP. Docker is needed to run `mvn verify`.

