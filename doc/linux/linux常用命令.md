##### echo操作文件
``` 
#将aaa写入文件，会覆盖原来文件的内容
echo 'aaa' > tmp.txt
#将bbb追加写入文末尾
echo 'bbb' >> tmp.txt
```
##### sed操作文件
```text
#更改文件中的匹配项
sed -i ‘s/oldstr/newstr/g’ file
#将修改后的文件写入到new.file, 不影响原文件
sed -i ‘s/oldstr/newstr/g’ file > new.file
```
```text
#sed 修改配置
例如my.cnf文件有一行配置datadir=/var/lib/mysql，需要修改成datadir=/opt/data/mysql
则需要执行命令
sed -i -e '/^datadir=/ s/=.*/= "\/opt\/data\/mysql"/' my.cnf

```
