FROM eclipse/ubuntu_jdk8:latest

ADD target/pms.jar pms.jar

EXPOSE 443

ENTRYPOINT ["java", "-Xms1024m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "pms.jar"]