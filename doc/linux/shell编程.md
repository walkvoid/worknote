# shell编程
## 1.字符串使用

## 2. 参数输入
```text
1.参数的个数：$#

2.第几个参数
参数的格式为$n，n代表一个数字：$1 表示第一个参数，$2 表示第二个参数，依此类推。

3.脚本当前的进程id： 当前进程ID号

4.遍历参数，$@代表所有的参数，$*表示所有参数的字符串形式
for i in "$@"; do
    echo $i
done
```

## 2.数组的使用
```
#创建数组1
projectArray[0]="所有项目"
projectArray[1]="lls-workflow"
#创建数组2
projectArray=("所有项目","lls-workflow")

# 获取所有的下标()
echo ${!projectArray[@]}
# 获取所有的值
echo ${projectArray[@]}
# 获取数组的长度
echo ${#projectArray[@]}

#获取某个下标的值
projectArray[1]

```


## 3.map的使用
```
#声明赋值1
declare -A projectMap
projectMap["0"]="所有项目"
projectMap["1"]="项目1"

#声明赋值2
declare -A projectMap=(["0"]="所有项目" ["1"]="项目1")

#注意 map结构只有sh版本大于4.1.2才支持,所以一般用数组而不用map

```

参考
-[shell echo 显示颜色](https://zhuanlan.zhihu.com/p/181609730)
