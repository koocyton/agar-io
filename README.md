# 自制细胞大作战

服务器有三, 一个是 Spring MVC (未完成) 一个是 Spring BOOT (未完成) 一个是 Reactor(完成)，服务端逻辑简单

这也是一个 Gradle 多模块的例子，开发中，便于不同的处理

```shell script
# 三个不同服务器端的打包
gradle :reactor-server:build
gradle :spring-server:build
gradle :boot-server:build
```

```shell script
#/bin/sh
java -jar -Dfile.encoding=utf-8 -Djava.awt.headless=true -Duser.timezone=GMT+08 reactor-server-1.0.jar app.properties &
```

### git hook for deploy

```shell script
#!/bin/sh

while read oldrev newrev refname
do
  branch=$(git rev-parse --symbolic --abbrev-ref $refname)
  # echo ">> $branch" >> /tmp/auth_commit.log
  if [ "$branch" = "master" ]; then
    /home/phd/cell_war_git_master_hook.sh > /dev/null 2>&1 &
    exec git update-server-info
  else
    echo "[ no match branch ]" >> /tmp/auth_commit.log
  fi
done
```

```shell script
#!/bin/sh

unset GIT_DIR
cd /home/phd

# Clone 项目
rm -rf /home/phd/cell-war-server
git clone -b master --depth=1 ssh://git@phabricator.doopp-inc.com/diffusion/26/agar-io-github.git cell-war-server
cd /home/phd/cell-war-server

# 更新 web 项目
sudo rm -rf /data/www/cell-war.doopp.com/web-public
sudo mv web-public /data/www/cell-war.doopp.com/

# 更新 api service
gradle release
sudo cp -rf reactor-server/build/libs/reactor-server-1.0.jar /data/www/cell-war.doopp.com/
sudo cp -rf app.properties /data/www/cell-war.doopp.com/

cd /data/www/cell-war.doopp.com/

ps axf | grep "cell-war.doopp.com" | grep -v "grep" | awk '{print $1}' | sudo xargs kill

sudo java -jar -Dfile.encoding=utf-8 -Djava.awt.headless=true -Duser.timezone=GMT+08 "/data/www/cell-war.doopp.com/reactor-server-1.0.jar" "/data/www/cell-war.doopp.com/app.properties" &

rm -rf /home/phd/cell-war-server
sudo chown www-data:www-data /data/www/cell-war.doopp.com/
```
