FROM eclipse/ubuntu_jdk8:latest

ADD target/pms.jar pms.jar

EXPOSE 80 443

# ENTRYPOINT ["java", "-jar", "/root/pms.jar"]
ENTRYPOINT ["sleep", "999999"]
