FROM postgres:latest

# Set the environment variables
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD example
ENV POSTGRES_DB pet-service

# Copy the SQL script to initialize the database
COPY init.sql /docker-entrypoint-initdb.d/

# Expose the PostgreSQL port
EXPOSE 5432