FROM eclipse/ubuntu_jdk8:latest

ADD target/pms.jar pms.jar

EXPOSE 443

ENTRYPOINT ["java", "-Xms256m", "-Xmx256m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "pms.jar", "--spring.config.location=/data/application.yml"]