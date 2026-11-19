FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

WORKDIR /app

ENV TZ="Europe/Oslo"

COPY build/libs/pensjon-opptjening-filadapter.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "-XX:+AggressiveHeap", "-XX:InitialHeapSize=500m" , "-XX:MaxHeapSize=1700m", "/app/app.jar"]