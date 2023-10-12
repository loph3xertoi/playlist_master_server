[![build](https://github.com/loph3xertoi/playlist_master_server/actions/workflows/build-deploy.yml/badge.svg)](https://github.com/loph3xertoi/playlist_master_server/actions/workflows/build-deploy.yml)
[![docs](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/loph3xertoi/7193286529b83053d1fb221d3c72402a/raw/pms-docs.json)](https://www.loph.tk/playlist_master_server/docs)
[![api](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/loph3xertoi/7193286529b83053d1fb221d3c72402a/raw/pms-api.json)](https://www.loph.tk/playlist_master_server/swagger-ui)
[![loc](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/loph3xertoi/7193286529b83053d1fb221d3c72402a/raw/pms-loc.json)](https://github.com/loph3xertoi/playlist_master_server)
[![comments](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/loph3xertoi/7193286529b83053d1fb221d3c72402a/raw/pms-comments.json)](https://github.com/loph3xertoi/playlist_master_server)

# playlist_master_server

The backend of playlist master.

## Setup
- You need add secrets.yml and your ssl keystore to src/main/resources.
The secrets.yml example is as follows:
```yml
server:
  ssl:
    key-store-password: <KEYSTORE-PASSWORD>

pms:
  # Used pepper to enhance the security of user's password stored in database.
  pepper: <PEPPER>
  # Used for registration of new user.
  registration-code: <REGISTRATION-CODE>
  remote-ip: <SERVER-IP>

# Used for test.
qqmusic:
  id: <QQ-NUMBER>
  cookie: <QQMUSIC-COOKIE>

ncm:
  id: <NCM-UID>
  cookie: <NCM-COOKIE>

bilibili:
  id: <BILIBILI-UID>
  cookie: <BILIBILI-COOKIE>

spring:
  mail:
    host: <SMTP-SERVER>
    username: <SENDER-EMAIL>
    password: <SMTP-ACCESS-TOKEN>
    port: <SMTP-PORT>
  redis:
    password: <REDIS-PASSWORD>
  datasource:
    username: <MYSQL-USERNAME>
    password: <MYSQL-PASSWORD>
  security:
    oauth2:
      client:
        registration:
          github:
            # OAuth2 for GitHub App.
            client-id: <GITHUB-APP-CLIENT-ID>
            client-secret: <GITHUB-APP-CLIENT-SECRET>
          google:
            # OAuth2 for Google.
            client-id: <GOOGLE-CLIENT-ID>
            client-secret: <GOOGLE-CLIENT-SECRET>

# OSS setting for library cover user uploaded.
qiniu:
  access-key: <QINIU-ACCESS-KEY>
  secret-key: <QINIU-SECRET-KEY>
  bucket: <QINIU-BUCKET>
  domain: <OSS-DOMAIN>
```
- Put the ssl keystore file and secrets.yml at the same directory with pms.jar, and run:
```shell
java -jar -Dspring.profiles.active=prod -Dspring.config.additional-location=secrets.yml pms.jar
```
- Enjoy
