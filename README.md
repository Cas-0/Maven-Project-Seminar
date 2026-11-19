# Ticket Management API

A Spring Boot REST API that provides CRUD (create, read, update, delete) operations for **users** and **products** stored in PostgreSQL, plus a simple file upload endpoint.

## Tech stack

| Technology | Purpose |
|---|---|
| Java 17 | Language |
| Spring Boot 4 (Spring MVC) | Web framework and REST endpoints |
| Spring Data JPA + Hibernate 7 | Object-relational mapping and generated SQL |
| PostgreSQL | Database |
| Maven (wrapper included) | Build and dependency management |

## Getting started

### 1. Prerequisites
- JDK 17 or newer
- PostgreSQL running on `localhost:5432`

### 2. Create the database and tables
Create a database named `SeniorSeminarMavenProjectDatabease`, or change the name in `spring.datasource.url` in [`application.properties`](src/main/resources/application.properties). Then run:

```sql
CREATE TABLE users
(
userid integer NOT NULL PRIMARY KEY,
username text
);
INSERT INTO users(userid, username) VALUES
(1, 'Bob'),
(2, 'Mary'),
(3, 'David'),
(4, 'Alice'),
(5, 'Steve');

CREATE TABLE product (
PRODNR char(6) NOT NULL,
PRODNAME varchar(60) UNIQUE NOT NULL,
PRODTYPE varchar(15) DEFAULT NULL,
AVAILABLE_QUANTITY integer DEFAULT NULL,
PRIMARY KEY (PRODNR)
);
```

The app does **not** create tables. It runs with `spring.jpa.hibernate.ddl-auto=validate`, so it checks at startup that the tables match the Java entities and refuses to start if they don't.

### 3. Set the database password
The password is read from the `DB_PASSWORD` environment variable and is never stored in the repository.

- **PowerShell (current session only):** `$env:DB_PASSWORD = "your-password"`
- **Windows (permanent):** `setx DB_PASSWORD "your-password"`, then restart your terminal or IDE
- **Eclipse:** Run → Run Configurations… → *TicketManagementApplication* → **Environment** tab → add `DB_PASSWORD`

### 4. Run the app
```powershell
.\mvnw.cmd spring-boot:run
```
Or, in Eclipse, run `TicketManagementApplication` as a Java application. The API starts on `http://localhost:8080`.

## API reference

IDs used to **read or delete** a single record go in the URL path (`/product/P00001`). Values for **create and update** go in query parameters (`/product?prodnr=P00001&prodname=Hammer`).

### Users: `/user`

| Method | URL | Parameters | Result |
|---|---|---|---|
| GET | `/user` | none | All users, sorted by `userid` |
| GET | `/user/{id}` | none | One user, or 404 |
| POST | `/user` | `userid`, `username` (both required) | `{"result":"added"}`, or 409 if the ID exists |
| PUT | `/user` | `userid`, `username` (both required) | `{"result":"Updated"}`, or 404 |
| DELETE | `/user/{id}` | none | `{"result":"Deleted"}`, or 404 |

### Products: `/product`

| Method | URL | Parameters | Result |
|---|---|---|---|
| GET | `/product` | none | All products, sorted by `prodnr` |
| GET | `/product/{prodnr}` | none | One product, or 404 |
| POST | `/product` | `prodnr`, `prodname` (required); `prodtype`, `availableQuantity` (optional) | `{"result":"added"}`, or 409 if the `prodnr` or `prodname` exists |
| PUT | `/product` | `prodnr`, `prodname` (required); `prodtype`, `availableQuantity` (optional) | `{"result":"Updated"}`, 404 if missing, or 409 if the new name is taken |
| DELETE | `/product/{prodnr}` | none | `{"result":"Deleted"}`, or 404 |

**Input rules** (based on the table definition, rejected with 400 if broken):
- `prodnr`: 1 to 6 characters
- `prodname`: 1 to 60 characters, and unique across all products
- `prodtype`: at most 15 characters

PUT replaces all of a product's values. If you leave out `prodtype` or `availableQuantity`, that value is cleared to `NULL`.

### Other endpoints

| Method | URL | Purpose |
|---|---|---|
| GET | `/` | Health check; returns `{"result":"Aloha"}` |
| POST | `/file/upload` | Uploads a multipart file (form field `file`) to `C:/test/`. [`upload.html`](upload.html) is a simple form for testing it. |


## How the code is organised

### Project structure
```
src/main/java/com/packtpub/
├── model/
│   ├── User.java                    Entity: one object = one row of "users"
│   ├── Product.java                 Entity: one object = one row of "product"
│   └── repository/
│       ├── UserRepository.java      Data access for users
│       └── ProductRepository.java   Data access for products
├── service/
│   ├── UserService.java             Interface: which user operations exist
│   ├── UserServiceImpl.java         Implementation: how they work, with business rules
│   ├── ProductService.java          Interface: which product operations exist
│   ├── ProductServiceImpl.java      Implementation: how they work, with business rules
│   ├── FileUploadService.java
│   └── FileUploadServerImpl.java
└── restapp/
    ├── HomeController.java
    ├── UserController.java          HTTP endpoints for /user
    ├── ProductController.java       HTTP endpoints for /product
    ├── FileController.java          HTTP endpoint for /file/upload
    └── ticket_management/
        └── TicketManagementApplication.java   Entry point
```

### The layers
Each layer has one job and only talks to the layer directly below it:

```
 HTTP request
      │
      ▼
 Controller        Web layer: reads the request, checks input, builds the JSON reply
      │
      ▼
 Service           Business rules: "does it exist?", "is the name taken?" (404 / 409)
 (interface + impl)
      │
      ▼
 Repository        Data access: Spring Data generates the SQL
      │
      ▼
 Entity            A Java object that represents one table row
      │  Hibernate translates objects into SQL
      ▼
 PostgreSQL
```

The controller never touches the database, and the repository knows nothing about HTTP. This separation is why moving the user data from an in-memory list to PostgreSQL only required changes in `UserServiceImpl`: the controller depends on the `UserService` **interface**, so the implementation behind it could be replaced without the controller noticing.

### Who creates the objects
The code never calls `new ProductController()` or `new ProductServiceImpl()`. Spring creates these objects (called *beans*) at startup and connects them. This is called *dependency injection*:

1. **Scanning:** `@SpringBootApplication(scanBasePackages = "com.packtpub")` tells Spring where to look for annotated classes.
2. **Repositories:** for each repository interface (found through `@EnableJpaRepositories`), Spring **generates an implementation at runtime**, with real SQL behind methods like `findById`, `save` and `existsByProdname`.
3. **Services:** Spring creates each `@Service` class and passes the matching repository into its constructor.
4. **Controllers:** Spring creates each `@RestController` and injects the matching service into its `@Autowired` field.
5. **Schema check:** Hibernate validates every `@Entity` (found through `@EntityScan`) against its table.

Spring creates each controller, service and repository **once** and shares it across all requests. Entity objects such as `Product` are different: Hibernate creates a new one for every row it reads, and the service creates one for every new record.

### One request, start to finish
For example, `PUT /product?prodnr=0120&prodname=Hammer&availableQuantity=50`:

1. **Spring MVC** routes the request to `ProductController.updateProduct` and converts the query parameters into Java values.
2. **The controller** validates the input, then calls `productService.updateProduct(...)`.
3. **The service** calls `productRepository.findById("0120")`, and Hibernate runs a `SELECT`, turning the row into a `Product` object.
4. **The service** checks the business rules, updates the object's fields and calls `productRepository.save(product)`, and Hibernate runs an `UPDATE`.
5. **The controller** returns `{"result":"Updated"}`, which Spring converts to JSON using Jackson.

### Error handling
Errors are raised as `ResponseStatusException` and travel back up through the layers. Spring turns them into an HTTP error response. Because of `server.error.include-message=always`, the response includes the message (for example, "Product 0120 not found").

| Layer | Catches | Status |
|---|---|---|
| Controller | Invalid input: blank values, values too long | 400 |
| Service | Business rules: record missing, duplicate ID or name | 404 / 409 |
| Database | Anything that slips past both, such as two identical requests arriving at once | 500 |

## Notes
- **`char(6)` product IDs:** PostgreSQL pads shorter values with spaces. `Product.getProdnr()` trims them, so the JSON shows `"0120"` and not `"0120  "`. The `@JdbcTypeCode(SqlTypes.CHAR)` annotation on that field tells Hibernate the column is `char` and not `varchar`, which schema validation requires.
- **Column names:** Hibernate maps camelCase fields to snake_case columns, so `availableQuantity` maps to `available_quantity`. Fields that are entirely lowercase, such as `prodnr`, map unchanged.
