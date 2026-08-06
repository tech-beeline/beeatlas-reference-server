# Database Documentation

This document describes the PostgreSQL database structure for the Open Source Reference Service, including schemas, tables, relationships, constraints, and indexes.

## Database Overview

The database uses three schemas to organize related tables:
- **users** - User management and authentication
- **products** - Product information and Structurizr integration
- **capability** - Business and technical capabilities with hierarchical relationships

## Schemas

### users Schema

Contains user authentication and authorization data.

### products Schema

Contains product information including Structurizr integration credentials.

### capability Schema

Contains business capabilities, technical capabilities, and their relationships.

## Tables

### users.user

Stores user accounts for authentication and authorization.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-incrementing user identifier |
| `login` | VARCHAR(255) | UNIQUE, NOT NULL | User login name (must be unique) |
| `password` | VARCHAR(255) | NOT NULL | SHA-256 hashed password (64 characters) |
| `admin` | BOOLEAN | DEFAULT FALSE | Administrator flag |

**Constraints:**
- Primary Key: `id`
- Unique Constraint: `login`
- Not Null: `login`, `password`

**Indexes:**
- Primary key index on `id`
- Unique index on `login`

**Default Data:**
- On first migration, a default administrator user is created:
  - Login: `admin`
  - Password: `admin` (SHA-256 hash: `8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918`)
  - Admin: `true`

---

### products.product

Stores product information and Structurizr integration credentials.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-incrementing product identifier |
| `name` | TEXT | UNIQUE, NOT NULL | Product name (must be unique) |
| `alias` | TEXT | NOT NULL | Product alias/identifier |
| `description` | TEXT | NULL | Product description |
| `created_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Creation timestamp |
| `last_modified_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Last modification timestamp |
| `deleted_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Soft delete timestamp (NULL if not deleted) |
| `structurizr_api_key` | TEXT | NULL | Structurizr API key for integration |
| `structurizr_api_secret` | TEXT | NULL | Structurizr API secret for integration |
| `structurizr_url` | TEXT | NULL | Structurizr service URL |

**Constraints:**
- Primary Key: `id`
- Unique Constraint: `name`
- Not Null: `name`, `alias`

**Indexes:**
- Primary key index on `id`
- Unique index on `name`

**Relationships:**
- Referenced by: `capability.tech_capability.responsibility_product_id` (Foreign Key)

---

### capability.business_capability

Stores business capabilities with hierarchical structure support.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-incrementing capability identifier |
| `code` | TEXT | UNIQUE, NOT NULL | Unique capability code (e.g., "BC.00001", "DMN.001") |
| `name` | TEXT | NOT NULL | Capability name |
| `description` | TEXT | NULL | Capability description |
| `created_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Creation timestamp |
| `last_modified_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Last modification timestamp |
| `deleted_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Soft delete timestamp (NULL if not deleted) |
| `status` | TEXT | NULL | Capability status (e.g., "Proposed", "Active") |
| `parent_id` | INTEGER | FOREIGN KEY | Reference to parent business capability (self-reference) |
| `is_domain` | BOOLEAN | NULL | Flag indicating if this is a domain capability |

**Constraints:**
- Primary Key: `id`
- Unique Constraint: `code`
- Foreign Key: `parent_id` REFERENCES `capability.business_capability(id)`
- Not Null: `code`, `name`

**Indexes:**
- Primary key index on `id`
- Unique index on `code`
- Foreign key index on `parent_id`

**Relationships:**
- Self-reference: `parent_id` → `capability.business_capability.id` (Many-to-One)
- Referenced by: `capability.tech_capability_relations.parent_id` (Foreign Key)

**Hierarchical Structure:**
The table supports a tree structure where business capabilities can have parent capabilities. This allows for:
- Group capabilities (e.g., "GRP.000", "GRP.001")
- Domain capabilities (e.g., "DMN.001", "DMN.002") with `is_domain = true`
- Business capabilities (e.g., "BC.00001", "BC.00002") with `is_domain = false`

---

### capability.tech_capability

Stores technical capabilities with product responsibility assignment.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-incrementing technical capability identifier |
| `code` | TEXT | UNIQUE, NOT NULL | Unique technical capability code |
| `name` | TEXT | NOT NULL | Technical capability name |
| `description` | TEXT | NULL | Technical capability description |
| `created_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Creation timestamp |
| `last_modified_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Last modification timestamp |
| `deleted_date` | TIMESTAMP WITHOUT TIME ZONE | NULL | Soft delete timestamp (NULL if not deleted) |
| `status` | TEXT | NULL | Capability status (e.g., "Proposed", "Active") |
| `responsibility_product_id` | INTEGER | FOREIGN KEY | Reference to responsible product |

**Constraints:**
- Primary Key: `id`
- Unique Constraint: `code`
- Foreign Key: `responsibility_product_id` REFERENCES `products.product(id)`
- Not Null: `code`, `name`

**Indexes:**
- Primary key index on `id`
- Unique index on `code`
- Foreign key index on `responsibility_product_id`

**Relationships:**
- Many-to-One: `responsibility_product_id` → `products.product.id`
- Referenced by: `capability.tech_capability_relations.child_id` (Foreign Key)

---

### capability.tech_capability_relations

Junction table linking business capabilities to technical capabilities (many-to-many relationship).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | SERIAL | PRIMARY KEY | Auto-incrementing relation identifier |
| `parent_id` | INTEGER | FOREIGN KEY, NOT NULL | Reference to business capability (parent) |
| `child_id` | INTEGER | FOREIGN KEY, NOT NULL | Reference to technical capability (child) |

**Constraints:**
- Primary Key: `id`
- Foreign Key: `parent_id` REFERENCES `capability.business_capability(id)`
- Foreign Key: `child_id` REFERENCES `capability.tech_capability(id)`
- Not Null: `parent_id`, `child_id`

**Indexes:**
- Primary key index on `id`
- Foreign key index on `parent_id`
- Foreign key index on `child_id`

**Relationships:**
- Many-to-One: `parent_id` → `capability.business_capability.id`
- Many-to-One: `child_id` → `capability.tech_capability.id`

**Purpose:**
This table establishes a many-to-many relationship between business capabilities and technical capabilities, allowing:
- One business capability to be supported by multiple technical capabilities
- One technical capability to support multiple business capabilities

---

## Entity Relationships Diagram

```
┌─────────────────────┐
│   users.user        │
│─────────────────────│
│ id (PK)             │
│ login (UNIQUE)      │
│ password            │
│ admin               │
└─────────────────────┘

┌────────────────────────┐
│ products.product       │
│────────────────────────│
│ id (PK)                │
│ name (UNIQUE)          │
│ alias                  │
│ description            │
│ created_date           │
│ last_modified_date     │
│ deleted_date           │
│ structurizr_api_key    │
│ structurizr_api_secret │
│ structurizr_url        │
└────────────────────────┘
         ▲
         │
         │ (responsibility_product_id)
         │
┌─────────────────────┐
│capability.          │
│tech_capability      │
│─────────────────────│
│ id (PK)             │
│ code (UNIQUE)       │
│ name                │
│ description         │
│ created_date        │
│ last_modified_date  │
│ deleted_date        │
│ status              │
│ responsibility_     │
│   product_id (FK)   │
└─────────────────────┘
         ▲
         │
         │ (child_id)
         │
┌─────────────────────┐
│capability.          │
│tech_capability_     │
│relations            │
│─────────────────────│
│ id (PK)            │
│ parent_id (FK)      │───┐
│ child_id (FK)       │   │
└─────────────────────┘   │
                          │
                          │ (parent_id)
                          │
┌─────────────────────┐   │
│capability.          │   │
│business_capability  │◄──┘
│─────────────────────│
│ id (PK)             │
│ code (UNIQUE)       │
│ name                │
│ description         │
│ created_date        │
│ last_modified_date  │
│ deleted_date        │
│ status              │
│ parent_id (FK)      │──┐(self-reference)
│ is_domain           │  │
└─────────────────────┘  │
                         │
                         │
                         └─── (hierarchical structure)
```

## Sequences

The following sequences are automatically created by PostgreSQL for SERIAL columns:

- `users.user_id_seq` - For `users.user.id`
- `products.product_id_seq` - For `products.product.id`
- `capability.business_capability_id_seq` - For `capability.business_capability.id`
- `capability.tech_capability_id_seq` - For `capability.tech_capability.id`
- `capability.tech_capability_relations_id_seq` - For `capability.tech_capability_relations.id`

## Data Integrity Rules

1. **User Authentication**: Passwords are stored as SHA-256 hashes (64 hexadecimal characters)
2. **Soft Deletes**: Tables use `deleted_date` for soft deletion instead of physical deletion
3. **Hierarchical Capabilities**: Business capabilities can form a tree structure via `parent_id`
4. **Many-to-Many Relations**: Technical capabilities can be linked to multiple business capabilities through the junction table
5. **Product Responsibility**: Each technical capability can be assigned to one product via `responsibility_product_id`

## Migration Files

The database structure is managed by Flyway migrations located in `src/main/resources/db/migration/`. Migrations are executed in numerical order and are idempotent (safe to run multiple times).

### V0001__create_schema_users.sql

**Purpose**: Creates the initial `users` schema and the `user` table.

**Actions**:
- Creates the `users` schema if it doesn't exist
- Creates the `users.user` table with columns:
  - `id` (SERIAL PRIMARY KEY)
  - `login` (VARCHAR(255) UNIQUE NOT NULL)
  - `password` (VARCHAR(255) NOT NULL)
  - `admin` (BOOLEAN DEFAULT FALSE)

**Idempotent**: Yes - uses `IF NOT EXISTS` checks

---

### V0002__create_table_user.sql

**Purpose**: Inserts the default administrator user for initial system access.

**Actions**:
- Checks if admin user already exists
- Inserts default admin user if not present:
  - Login: `admin`
  - Password: `admin` (SHA-256 hash: `8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918`)
  - Admin flag: `true`

**Security Note**: The default password should be changed immediately after first deployment.

**Idempotent**: Yes - checks for existing admin user before insertion

---

### V0003__create_capability_schema.sql

**Purpose**: Creates the `capability` schema for storing business and technical capabilities.

**Actions**:
- Creates the `capability` schema if it doesn't exist

**Idempotent**: Yes - uses `IF NOT EXISTS` check

---

### V0004__create_business_capability_table.sql

**Purpose**: Creates the `business_capability` table for hierarchical business capability management.

**Actions**:
- Ensures `capability` schema exists
- Creates `capability.business_capability` table with columns:
  - `id` (SERIAL PRIMARY KEY)
  - `code` (TEXT UNIQUE NOT NULL) - Unique capability code identifier
  - `name` (TEXT NOT NULL) - Capability name
  - `description` (TEXT) - Optional description
  - `created_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `last_modified_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `deleted_date` (TIMESTAMP WITHOUT TIME ZONE) - For soft deletion
  - `status` (TEXT) - Capability status (e.g., "Proposed", "Active")
  - `parent_id` (INTEGER) - Self-referencing foreign key for hierarchy
  - `is_domain` (BOOLEAN) - Flag indicating domain capability
- Creates foreign key constraint: `parent_id` → `capability.business_capability.id`

**Idempotent**: Yes - uses `IF NOT EXISTS` check

---

### V0005__create_products_schema_and_table.sql

**Purpose**: Creates the `products` schema and `product` table for product management.

**Actions**:
- Creates the `products` schema if it doesn't exist
- Creates `products.product` table with columns:
  - `id` (SERIAL PRIMARY KEY)
  - `name` (TEXT UNIQUE NOT NULL) - Unique product name
  - `alias` (TEXT NOT NULL) - Product alias/identifier
  - `description` (TEXT) - Optional description
  - `created_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `last_modified_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `deleted_date` (TIMESTAMP WITHOUT TIME ZONE) - For soft deletion

**Idempotent**: Yes - uses `IF NOT EXISTS` checks

---

### V0006__create_table_tech_capability.sql

**Purpose**: Creates the `tech_capability` table for technical capability management.

**Actions**:
- Creates `capability.tech_capability` table with columns:
  - `id` (SERIAL PRIMARY KEY)
  - `code` (TEXT UNIQUE NOT NULL) - Unique technical capability code
  - `name` (TEXT NOT NULL) - Technical capability name
  - `description` (TEXT) - Optional description
  - `created_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `last_modified_date` (TIMESTAMP WITHOUT TIME ZONE)
  - `deleted_date` (TIMESTAMP WITHOUT TIME ZONE) - For soft deletion
  - `status` (TEXT) - Capability status
  - `responsibility_product_id` (INTEGER) - Foreign key to `products.product.id`
- Creates foreign key constraint: `responsibility_product_id` → `products.product.id`

**Idempotent**: Yes - uses `IF NOT EXISTS` check

---

### V0007__create_capability_tech_capability_relations_table.sql

**Purpose**: Creates the junction table for many-to-many relationships between business and technical capabilities.

**Actions**:
- Creates `capability.tech_capability_relations` table with columns:
  - `id` (SERIAL PRIMARY KEY)
  - `parent_id` (INTEGER NOT NULL) - Foreign key to `capability.business_capability.id`
  - `child_id` (INTEGER NOT NULL) - Foreign key to `capability.tech_capability.id`
- Creates foreign key constraints:
  - `parent_id` → `capability.business_capability.id`
  - `child_id` → `capability.tech_capability.id`

**Purpose**: Establishes many-to-many relationships allowing:
- One business capability to be supported by multiple technical capabilities
- One technical capability to support multiple business capabilities

**Idempotent**: Yes - uses `IF NOT EXISTS` check

---

### V0008__add_column_to_product_table.sql

**Purpose**: Adds Structurizr integration columns to the `product` table.

**Actions**:
- Adds `structurizr_api_key` (TEXT) column to `products.product`
- Adds `structurizr_api_secret` (TEXT) column to `products.product`
- Adds `structurizr_url` (TEXT) column to `products.product`

**Purpose**: Enables integration with Structurizr for architecture diagramming and documentation.

**Idempotent**: Yes - uses `ALTER TABLE ADD COLUMN` which is safe to run multiple times (PostgreSQL will skip if column exists)

---

### V0009__insert_initial_capabilities.sql

**Purpose**: Inserts initial business capability data for the system.

**Actions**:
- Checks if any business capabilities already exist
- If table is empty, inserts a hierarchical set of business capabilities including:
  - Group capabilities (GRP.000, GRP.001, GRP.002)
  - Domain capabilities (DMN.001 through DMN.007) with `is_domain = true`
  - Business capabilities (BC.00001 through BC.00039) with `is_domain = false`
  - Establishes parent-child relationships through `parent_id` references
  - Sets initial status to "Proposed" for all capabilities
  - Sets creation timestamps

**Data Structure**: Creates a multi-level hierarchy:
- Root: "Каталог Возможностей (Capability Catalog)" (GRP.000)
- Level 1: Groups (e.g., "Нефтегазовая Разведка", "Производство и эксплуатация")
- Level 2: Domains (e.g., "Оценка возможностей", "Геологический и географический анализ")
- Level 3: Business Capabilities (e.g., "Геологическая и геофизическая идентификация")

**Idempotent**: Yes - checks if table is empty before insertion

---

### V0010__insert_test_product.sql

**Purpose**: Inserts a test product for development and testing purposes.

**Actions**:
- Inserts a test product into `products.product` table:
  - Name: "Тестовое приложение" (Test Application)
  - Alias: "test"
  - Description: "Тестовое приложение компании" (Company test application)
  - Structurizr API Key: `10476ad7-2675-49e1-861d-6bf2d7964164`
  - Structurizr API Secret: `8a02614c-17db-4f7f-bfac-c24ad069ca2q`
  - Created Date: Current timestamp (NOW())

**Purpose**: Provides a default product for testing technical capability assignments and Structurizr integration.

**Note**: This migration inserts data unconditionally. If a product with the same name already exists, it may cause a unique constraint violation. Consider adding existence checks for production use.

**Idempotent**: No - will fail if product with same name exists

## Query Examples

### Get all business capabilities with their parent
```sql
SELECT 
    bc.id,
    bc.code,
    bc.name,
    parent.code AS parent_code,
    parent.name AS parent_name
FROM capability.business_capability bc
LEFT JOIN capability.business_capability parent ON bc.parent_id = parent.id
WHERE bc.deleted_date IS NULL;
```

### Get technical capabilities with their responsible products
```sql
SELECT 
    tc.id,
    tc.code,
    tc.name,
    p.name AS product_name,
    p.alias AS product_alias
FROM capability.tech_capability tc
LEFT JOIN products.product p ON tc.responsibility_product_id = p.id
WHERE tc.deleted_date IS NULL;
```

### Get business capabilities with their related technical capabilities
```sql
SELECT 
    bc.code AS business_capability_code,
    bc.name AS business_capability_name,
    tc.code AS technical_capability_code,
    tc.name AS technical_capability_name
FROM capability.business_capability bc
JOIN capability.tech_capability_relations tcr ON bc.id = tcr.parent_id
JOIN capability.tech_capability tc ON tcr.child_id = tc.id
WHERE bc.deleted_date IS NULL 
  AND tc.deleted_date IS NULL;
```

### Get hierarchical business capability tree
```sql
WITH RECURSIVE capability_tree AS (
    -- Base case: root capabilities (no parent)
    SELECT id, code, name, parent_id, 0 AS level
    FROM capability.business_capability
    WHERE parent_id IS NULL AND deleted_date IS NULL
    
    UNION ALL
    
    -- Recursive case: child capabilities
    SELECT bc.id, bc.code, bc.name, bc.parent_id, ct.level + 1
    FROM capability.business_capability bc
    JOIN capability_tree ct ON bc.parent_id = ct.id
    WHERE bc.deleted_date IS NULL
)
SELECT * FROM capability_tree ORDER BY level, code;
```

## Notes

- All timestamps use `TIMESTAMP WITHOUT TIME ZONE` type
- Soft deletion is implemented using `deleted_date` columns (NULL = active, NOT NULL = deleted)
- The `users.user.password` column stores SHA-256 hashed passwords
- Business capabilities support hierarchical structures through self-referencing foreign keys
- The relationship between business and technical capabilities is many-to-many via the junction table


