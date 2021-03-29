CREATE TABLE IF NOT EXISTS project (
    id uuid NOT NULL,
    created timestamp NOT NULL,
    updated timestamp NOT NULL,
    version int NOT NULL,
    name varchar NOT NULL,
    start_date date NOT NULL,
    end_date date,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS project_entry (
    id uuid NOT NULL,
    created timestamp NOT NULL,
    updated timestamp NOT NULL,
    version int NOT NULL,
    project_id uuid NOT NULL,
    entry_date date NOT NULL,
    time_spent  numeric(2,2) NOT NULL,
    description varchar,
    PRIMARY KEY (id),
    FOREIGN KEY (project_id) REFERENCES project (id)
);