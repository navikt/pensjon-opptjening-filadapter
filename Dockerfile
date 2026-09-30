FROM gcr.io/distroless/java25-debian13:nonroot

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY build/libs/pensjon-opptjening-filadapter.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "-XX:+AggressiveHeap", "-XX:InitialHeapSize=500m" , "-XX:MaxHeapSize=1700m", "/app/app.jar"]