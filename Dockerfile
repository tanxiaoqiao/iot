FROM 10.78.115.234/access/openjdk:8-jre-alpine
ADD target/fire-foxconn-1.0.0.jar  /
RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories
RUN apk --update add curl bash ttf-dejavu && \
      rm -rf /var/cache/apk/*
EXPOSE 7079
ENTRYPOINT ["java","-jar","-Duser.timezone=GMT+08","fire-foxconn-1.0.0.jar"]