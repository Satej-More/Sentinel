# Docker Deployment Guide - SENTINEL

This guide outlines how to deploy the entire SENTINEL platform — including the Spring Boot application, MySQL, Kafka, and Redis — in a local production-style environment using Docker and Docker Compose.

---

## 🚀 Container Startup Guide

You can launch the complete ecosystem with a single command. 

### 1. Build and Start Services
Run the following command in the root of the backend folder (where `docker-compose.yml` resides):
```bash
docker compose up --build -d
```
*   `--build`: Automatically builds/rebuilds the Spring Boot application's local Docker image from the source code.
*   `-d`: Runs the containers in detached (background) mode, freeing up your terminal.

### 2. Monitor Container Status
Verify that all services are running and healthy:
```bash
docker compose ps
```
The health checks will cause the Spring Boot container `sentinel-backend` to wait until `sentinel-mysql`, `sentinel-redis`, and `sentinel-kafka` are in a `healthy` state before starting.

### 3. View Logs
View real-time logs for all containers:
```bash
docker compose logs -f
```
Or view logs for only the Spring Boot application:
```bash
docker compose logs -f app
```

### 4. Stop Services
Shut down and stop all containers without losing persistent data:
```bash
docker compose down
```
If you want to stop the containers **and** delete all persistent volumes (e.g. wiping the database completely):
```bash
docker compose down -v
```

---

## 🔌 Networking & Service Discovery

Inside the [docker-compose.yml](file:///c:/Users/SATEJ%20MORE/Downloads/sentinel/sentinel/docker-compose.yml), a custom bridge network called `sentinel-network` is defined:

*   **Bridge Network**: A private internal network isolated from your host system where containers can seamlessly connect.
*   **Service Discovery**: Each container's hostname is automatically registered in Docker's internal DNS based on the service name specified in the compose file. E.g.:
    *   The `app` container connects to MySQL using the URL `jdbc:mysql://mysql:3306/...` where `mysql` is the container host.
    *   The `app` connects to Redis via the hostname `redis`.
    *   The `app` connects to Kafka via the bootstrap server `kafka:9092`.
*   **Port Mapping**: Port mappings are written as `HOST_PORT:CONTAINER_PORT`. For instance, the app exposes `8080:8080`, allowing you to make requests to the application via `http://localhost:8080` from your browser or cURL on the host.

### ⚙️ Simplified Kafka Listener Configuration
To ensure a highly reliable startup and eliminate complexity, Kafka is configured with a **single listener** setup:
*   **PLAINTEXT Listener (`kafka:9092`)**: Listens on port `9092` inside the Docker network. Both the Spring Boot `app` container and any other service inside `sentinel-network` connect to `kafka:9092`.
*   **Port Forwarding**: Port `9092` is mapped to the host (`9092:9092`), meaning you can interact with Kafka from your host machine as well. If you run the Spring Boot application locally on your host (outside Docker) for development, configure it to connect to `localhost:9092`, but make sure to add `127.0.0.1 kafka` to your host OS `hosts` file to resolve Kafka's advertised internal hostname properly.

---

## 💾 Data Persistence and Volumes

By default, files inside a running Docker container are ephemeral; if the container is deleted, all stored data is lost. 

To solve this, we use a **Docker Named Volume**:
```yaml
volumes:
  mysql_data:
```
*   This creates a persistent storage directory managed by the Docker daemon on the host.
*   In the `mysql` service definition, this volume is mounted:
    ```yaml
    volumes:
      - mysql_data:/var/lib/mysql
    ```
*   This mounts the persistent volume to MySQL's internal storage path (`/var/lib/mysql`).
*   **Rebuild Safety**: Running `docker compose down` and `docker compose up` will rebuild and restart containers, but **your MySQL database state (including users and transaction records) will be safely preserved**!

---

## 🛠️ Troubleshooting Notes

### 1. Port Already in Use (e.g., `Port 3306 or 8080 is already occupied`)
*   **Cause**: You have a local installation of MySQL, Redis, or an existing process running on ports `3306`, `6379`, or `8080`.
*   **Solution**: Stop your local services, or edit the active [.env](file:///c:/Users/SATEJ%20MORE/Downloads/sentinel/sentinel/.env) file to bind the services to different host ports:
    ```ini
    SERVER_PORT=8090
    DB_PORT=3307
    REDIS_PORT=6380
    ```

### 2. Spring Boot App Fails to Start due to Database/Kafka/Redis Connection Issues
*   **Cause**: Docker Compose is spinning up services, but the databases are not ready to accept connections yet when the Spring Boot app attempts to boot.
*   **Solution**: We've mitigated this by using container `healthcheck` configurations combined with `depends_on` in `docker-compose.yml`. Spring Boot will not launch until:
    *   MySQL successfully handles `mysqladmin ping`.
    *   Redis answers `redis-cli ping`.
    *   Kafka successfully executes a topic query command.
    *   If a service still crashes, try restarting the container: `docker compose restart app`.

### 3. Running App Locally on Host (Outside Docker) with Kafka inside Docker
*   **Cause**: If you run the Spring Boot application on your host machine but keep Kafka running inside Docker, the host application will resolve Kafka's advertised address (`kafka:9092`) and fail to connect if `kafka` cannot be resolved on the host.
*   **Solution**: Simply map the hostname `kafka` to your local machine inside your host operating system's `hosts` file:
    *   **Windows**: Open `C:\Windows\System32\drivers\etc\hosts` as Administrator and add: `127.0.0.1 kafka`.
    *   **Mac/Linux**: Open `/etc/hosts` as root and add: `127.0.0.1 kafka`.
    *   This maps host-level calls to `kafka:9092` straight into the container successfully!

### 4. Windows Line Endings (`CRLF` issue)
*   **Cause**: Executables like the Maven wrapper `./mvnw` fail with "command not found" or "no such file" inside Alpine/Ubuntu Linux containers because of Windows carriage return characters (`\r`).
*   **Solution**: The `Dockerfile` includes an explicit utility line that automatically strips `\r` endings (`tr -d '\r'`) before execution, making this entirely Windows-friendly out-of-the-box!
