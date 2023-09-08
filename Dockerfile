# Используем базовый образ Temurin 17 с Alpine Linux
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /rivstas/

# Копируем собранный JAR-файл внутрь контейнера
COPY ./target/JAN-Problem-0.0.1-SNAPSHOT.jar ./problem-service.jar

# Устанавливаем переменные окружения
ENV EMAIL_ADDRESS='stasrivkin.il@gmail.com'
ENV EMAIL_PASSWORD='srbckjeonektkgyo'
ENV JWT_SECRET='hqUerWWqiExZNqIANXb4Xe8upVEaLtBvOJZkza1+desHl5kyrHSFCnDgI0OGCsT1kGJhWxCfxKvWmj3EAH0wTA=='
ENV KAFKA_CONFIG="org.apache.kafka.common.security.scram.ScramLoginModule required username='ssppshpm' password='uipBeio0BdwVedfCCqy6vhtBDcbeuUpR'"
ENV KAFKA_KEY='ssppshpm'
ENV MOGODB_URI='mongodb+srv://DevTeam:TelranJava47@jan-problem.biv87zb.mongodb.net/telran?retryWrites=true&w=majority'

# Запускаем Java-приложение при старте контейнера
CMD ["java", "-jar", "/rivstas/problem-service.jar", "--server.port=8081", "--server.address=0.0.0.0"]
