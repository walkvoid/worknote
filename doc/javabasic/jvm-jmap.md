# JVM 内存分析工具：`jmap` 使用指南

## 1. 概述
`jmap` 是 Java 虚拟机（JVM）提供的一个命令行工具，用于生成堆内存快照（Heap Dump）或查看 JVM 内存使用情况。它可以帮助开发者分析内存泄漏、对象分布等问题。

---

## 2. 基本用法
### 2.1 命令格式
```bash
jmap [options] <pid>
```

- **`options`**: 指定要执行的操作（见下文）。
- **`pid`**: 目标 JVM 的进程 ID。

---

### 2.2 常用选项及示例

#### 1. `-heap`：显示堆内存摘要信息
```bash
jmap -heap <pid>
```
- 显示堆内存的配置和使用情况。

**输出示例：**
```
Attaching to process ID 12345, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.221-b11

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1048576000 (1000.0MB)
   NewSize                  = 104857600 (100.0MB)
   MaxNewSize               = 104857600 (100.0MB)
   OldSize                  = 209715200 (200.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 83886080 (80.0MB)
   used     = 16777216 (16.0MB)
   free     = 67108864 (64.0MB)
   20.0% used
From Space:
   capacity = 10485760 (10.0MB)
   used     = 0 (0.0MB)
   free     = 10485760 (10.0MB)
   0.0% used
To Space:
   capacity = 10485760 (10.0MB)
   used     = 0 (0.0MB)
   free     = 10485760 (10.0MB)
   0.0% used
PS Old Generation
   capacity = 209715200 (200.0MB)
   used     = 0 (0.0MB)
   free     = 209715200 (200.0MB)
   0.0% used
```

- **Heap Configuration**: 堆内存的配置参数。
- **Heap Usage**: 堆内存的使用情况，包括新生代（Young Generation）和老年代（Old Generation）。

---

#### 2. `-histo`：显示堆内存中对象的直方图
```bash
jmap -histo <pid>
```
- 显示堆内存中对象的数量、大小和类名。

**输出示例：**
```
 num     #instances         #bytes  class name
----------------------------------------------
   1:         10000       1000000  java.lang.String
   2:          5000        500000  java.util.HashMap$Node
   3:          2000        200000  [C
   4:          1000        100000  java.lang.Object
```

- **#instances**: 对象实例的数量。
- **#bytes**: 对象占用的总字节数。
- **class name**: 对象的类名。

---

#### 3. `-dump`：生成堆内存快照（Heap Dump）
```bash
jmap -dump:format=b,file=heapdump.hprof <pid>
```
- 生成堆内存快照并保存到文件 `heapdump.hprof`。

**参数说明：**
- **format=b**: 指定输出格式为二进制。
- **file=heapdump.hprof**: 指定输出文件名。

**使用场景：**
- 分析内存泄漏。
- 查看堆内存中对象的分布情况。

---

#### 4. `-finalizerinfo`：显示等待终结的对象
```bash
jmap -finalizerinfo <pid>
```
- 显示等待终结（Finalization）的对象信息。

**输出示例：**
```
Attaching to process ID 12345, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.221-b11

Number of objects pending for finalization: 0
```

- **Number of objects pending for finalization**: 等待终结的对象数量。

---

#### 5. `-clstats`：显示类加载器的统计信息
```bash
jmap -clstats <pid>
```
- 显示类加载器的统计信息。

**输出示例：**
```
Attaching to process ID 12345, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.221-b11

class_loader    classes  bytes   parent_loader   alive?  type  
<bootstrap>      1000    500000  null            live    <internal>
0x00000007c0060c48  200    100000  0x00000007c0060c00  live    sun/misc/Launcher$AppClassLoader@0x00000007c0060c00
```

- **class_loader**: 类加载器的地址。
- **classes**: 加载的类数量。
- **bytes**: 加载的类占用的字节数。
- **parent_loader**: 父类加载器的地址。
- **alive?**: 类加载器是否存活。
- **type**: 类加载器的类型。

---

#### 6. `-F`：强制模式
```bash
jmap -F -heap <pid>
```
- 当目标 JVM 进程无响应时，使用 `-F` 选项强制生成堆内存快照或显示信息。

---

## 3. 使用场景
- **内存泄漏分析**：通过生成堆内存快照（Heap Dump），使用工具（如 Eclipse MAT、VisualVM）分析内存泄漏。
- **对象分布分析**：通过 `-histo` 选项查看堆内存中对象的分布情况。
- **性能调优**：通过 `-heap` 选项查看堆内存配置和使用情况，优化 JVM 参数。

---

## 4. 注意事项
1. **权限问题**：`jmap` 需要访问目标 JVM 的进程，因此需要足够的权限。
2. **性能影响**：生成堆内存快照可能会导致 JVM 暂停，影响应用程序的性能。
3. **文件大小**：堆内存快照文件可能非常大，确保磁盘空间充足。

---

## 5. 参考文档
- [Oracle 官方文档：jmap](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jmap.html)
- [Java Performance Tuning Guide](https://www.oracle.com/java/technologies/javase/tuning-guide.html)

---
