# ==============================
# STEP 1: Base image (Java 17)
# ==============================
FROM eclipse-temurin:17-jdk

# ==============================
# STEP 2: Working directory
# ==============================
WORKDIR /app

# ==============================
# STEP 3: Copy jar file
# ==============================
# target/*.jar = Spring Boot build output
COPY target/fraud-detection-service-0.0.1-SNAPSHOT.jar app.jar

# ==============================
# STEP 4: Expose springboot port
# ==============================

EXPOSE 8080

# ==============================
# STEP 5: Run application
# ==============================
ENTRYPOINT ["java","-jar","app.jar"]
