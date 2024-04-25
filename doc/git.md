##### 回滚某个提交
```text
# 1. 将本地分之重置到某一次提交， 使用--soft 选项会讲变更的记录保存到本地暂存区
git reset --soft {commitId}

# 2. 本地提交后重新强制push，保证远端和本地一样
```
##### 远程仓库修改
```text
#获取远程仓库地址
git remote get-url origin
#更改远程仓库地址
git remote set-url origin git@github.com:aaa/bbb.git
```

##### 修改历史提交记录的坐着和邮箱信息
```text
#执行下面的脚本,根据需要需改OLD_EMAIL,CORRECT_NAME,CORRECT_EMAIL三个变量的值即可。
# 执行完脚本后，需要强制将代码推送到远端

git filter-branch --env-filter '

OLD_EMAIL="old-email@email.com"   
CORRECT_NAME="new-name"        
CORRECT_EMAIL="new-email@email.com"       

if [ "$GIT_COMMITTER_EMAIL" = "$OLD_EMAIL" ]

then

    export GIT_COMMITTER_NAME="$CORRECT_NAME"
    export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"

fi

if [ "$GIT_AUTHOR_EMAIL" = "$OLD_EMAIL" ]

then

    export GIT_AUTHOR_NAME="$CORRECT_NAME"
    export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"

fi

' --tag-name-filter cat -- --branches --tags

```
#### 基于远程分支创建新分支并切换到新分支
```text
git checkout -b 需要创建的新分支名 origin/已存在的远程分支名
#切换后,当前分支的追踪分支还是旧的分支,并且只是在本地创建的新分支
```
#### 推送当前分支到远端并更新追踪分支
```text
git push -u origin 分支名
```
#### 更新追踪分支
```text
git branch --set-upstream-to=origin/远程分支名 本地分支名
#如果是更新本地当前分支,“本地分支名”可以省略
```

#### 使用git-filter-repo更新提交记录
```
#使用pip 安装git-filter-repo
pip3 install git-filter-repo

#看安装成功的提示,将git-filter-repo安装的目录添加到系统环境变量中

#在项目下创建一个名称mymailmap文件,文件名任意取,内容为
newName <newMail@mail.com>  <oldMail1@mail.com>
newName <newMail@mail.com>  <oldMail2@mail.com>
newName <newMail@mail.com>  <oldMail3@mail.com>

#执行
 git filter-repo -f --mailmap .\mymailmap

#成功后,本地项目会丢失远程仓库的地址,需要重新设置远程地址和追踪分支

#强制提交


```
