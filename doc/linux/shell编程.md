#### 1.字符串使用


#### 2.数组的使用
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


#### 3.map的使用
```
#声明赋值1
declare -A projectMap
projectMap["0"]="所有项目"
projectMap["1"]="项目1"

#声明赋值2
declare -A projectMap=(["0"]="所有项目" ["1"]="项目1")

#注意 map结构只有sh版本大于4.1.2才支持,所以一般用数组而不用map

```
