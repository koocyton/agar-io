#### Agar IO Game

```bash
# 打包
gradle :iogame-server:build

# 运行
java -jar \
  -Dfile.encoding=utf-8 \
  -Djava.awt.headless=true \
  -Duser.timezone=GMT+08 \
  iogame-server-1.0.jar \
  iogame.dev.properties &
```

#### git post_receive hook
```bash
#!/bin/sh
while read oldrev newrev refname
do
  branch=$(git rev-parse --symbolic --abbrev-ref $refname)
  if [ "$branch" = "master" ]; then
    /home/phd/agar_hook_build.sh > /dev/null 2>&1 &
    exec git update-server-info
  else
    echo "[ no match branch ]" >> /tmp/agar_hook_build.log
  fi
done
```
#### git build hook
```bash
#!/bin/sh
sudo rm -rf /tmp/io-game
sudo git clone --depth=1 ssh://git@dev.doopp.com/diffusion/15/io-game.git /tmp/io-game
sudo gradle -p /tmp/io-game :iogame-server:build
sudo cp /tmp/io-game/iogame-server/build/libs/iogame-server-1.0.jar /data/www/agar.doopp.com
sudo ps -ef | grep iogame-server | awk -F ' ' '{print $2}' | xargs sudo kill -9
sudo java -jar /data/www/agar.doopp.com/iogame-server-1.0.jar /data/www/agar.doopp.com/iogame.dev.properties &
sudo cp -R /tmp/io-game/iogame-web /data/www/agar.doopp.com/
```

#### nginx config
```
server {
    listen 80;
    server_name agar.doopp.com;
    return 301 https://$host$request_uri;
}
server {
    listen 443 ssl;
    server_name agar.doopp.com;
    client_max_body_size 10M;

    ssl_certificate crt/doopp_com.pem;
    ssl_certificate_key crt/doopp_com.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;

    location /ws/agar-game {
        proxy_pass              http://127.0.0.1:15128;
        proxy_set_header        Host            $http_host;
        proxy_set_header        X-Real-IP       $remote_addr;
        proxy_set_header        X-Forwarded-For $remote_addr;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /api {
        proxy_pass              http://127.0.0.1:15128;
        proxy_set_header        Host            $host;
        proxy_set_header        X-Real-IP       $remote_addr;
        proxy_set_header        X-Forwarded-For $remote_addr;
        proxy_http_version      1.1;
        proxy_connect_timeout   3s;
        proxy_send_timeout      3s;
    }

    location / {
       index index.html index.htm;
       root /data/www/agar.doopp.com/iogame-web;
    }
}
```

