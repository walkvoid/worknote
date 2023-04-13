##### 回滚某个提交
```text
# 1. 将本地分之重置到某一次提交， 使用--soft 选项会讲变更的记录保存到本地暂存区
git reset --soft {commitId}

# 2. 本地提交后重新强制push，保证远端和本地一样
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