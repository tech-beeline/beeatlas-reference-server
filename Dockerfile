# Многоступенчатая сборка
# Этап 1: Сборка приложения
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Устанавливаем Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Копируем pom.xml и загружаем зависимости (кэширование слоев)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Создаем пользователя для запуска приложения
RUN useradd -m -d /app -s /bin/bash -u 1000 appuser && \
    chown -R appuser:appuser /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/opensource-reference-service-*.jar app.jar

# Меняем владельца файла
RUN chown appuser:appuser app.jar

# Переключаемся на пользователя приложения
USER appuser

# Открываем порты
EXPOSE 8080 8090 10260

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

