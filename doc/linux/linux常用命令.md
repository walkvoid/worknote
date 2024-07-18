# Linux常用命令
## 文件读取
### more命令
more会将整个文件读取到内存，然后一页页展示，使用Page Up 和Page Down翻页，也可以使用b和空格翻页  
```shell
more 文件名
```

### less命令
和more不同，less并不会一次性将文件读取到内存中，适合大文件。
less也提供了翻页查看功能，使用Page Up 和Page Down翻页，也可以使用b和空格翻页。
```shell
less +/关键字 文件名
less +/关键字1.*关键字2 文件名
```

##文件写入
### echo写入内容
``` 
#将aaa写入文件，会覆盖原来文件的内容
echo 'aaa' > tmp.txt
#将bbb追加写入文末尾
echo 'bbb' >> tmp.txt
```
### sed修改内容
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
