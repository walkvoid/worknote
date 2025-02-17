# JVM 监控工具：`jstat` 使用指南

## 1. 概述
`jstat` 是 Java 虚拟机（JVM）提供的一个命令行工具，用于监控 JVM 的各种运行时状态，包括类加载、垃圾回收、JIT 编译等。它是一个轻量级的工具，适合在性能调优和问题排查时使用。

---

## 2. 基本用法
### 2.1 命令格式
```bash
jstat [options] <vmid> [interval] [count]
```

- **`options`**: 指定要监控的选项（见下文）。
- **`vmid`**: 目标 JVM 的进程 ID（PID）或 JVM 实例的唯一标识符。
- **`interval`**: 监控的时间间隔（单位：毫秒）。
- **`count`**: 监控的次数。

---

### 2.2 常用选项
| 选项 | 描述 |
|------|------|
| `-class` | 监控类加载和卸载情况。 |
| `-gc` | 监控堆内存和垃圾回收情况。 |
| `-gccapacity` | 监控堆内存各区域的使用容量。 |
| `-gcutil` | 监控垃圾回收的统计信息（百分比形式）。 |
| `-gccause` | 显示最近一次垃圾回收的原因。 |
| `-gcnew` | 监控新生代的垃圾回收情况。 |
| `-gcold` | 监控老年代的垃圾回收情况。 |
| `-printcompilation` | 显示 JIT 编译的方法信息。 |

---

## 3. 示例
### 3.1 `-class`：监控类加载和卸载情况
```bash
jstat -class <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次类加载信息，共输出 5 次。

**输出示例：**
```
Loaded  Bytes  Unloaded  Bytes     Time
  5000  8000      100    200      1.23
```

- **Loaded**: 已加载的类数量。
- **Bytes**: 已加载类的字节数。
- **Unloaded**: 已卸载的类数量。
- **Bytes**: 已卸载类的字节数。
- **Time**: 类加载和卸载所花费的时间。

---

### 3.2 `-gc`：监控堆内存和垃圾回收情况
```bash
jstat -gc <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次垃圾回收信息，共输出 5 次。

**输出示例：**
```
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
1024.0 1024.0  0.0    0.0    8192.0   1024.0    20480.0     0.0     4480.0  776.0  384.0   76.0    0       0.0     0      0.0     0.0
```

- **S0C/S1C**: Survivor 0 和 Survivor 1 区的容量（KB）。
- **S0U/S1U**: Survivor 0 和 Survivor 1 区的使用量（KB）。
- **EC/EU**: Eden 区的容量和使用量（KB）。
- **OC/OU**: 老年代的容量和使用量（KB）。
- **MC/MU**: 元数据区的容量和使用量（KB）。
- **CCSC/CCSU**: 压缩类空间的容量和使用量（KB）。
- **YGC/YGCT**: 年轻代 GC 的次数和总耗时。
- **FGC/FGCT**: Full GC 的次数和总耗时。
- **GCT**: 所有 GC 的总耗时。

---

### 3.3 `-gccapacity`：监控堆内存各区域的使用容量
```bash
jstat -gccapacity <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次堆内存各区域的容量信息，共输出 5 次。

**输出示例：**
```
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 10240.0  20480.0  10240.0 1024.0 1024.0  8192.0    20480.0    40960.0    20480.0    20480.0    0.0    1056768.0  4480.0      0.0  1048576.0   384.0     0     0
```

- **NGCMN/NGCMX**: 新生代的最小和最大容量（KB）。
- **NGC**: 当前新生代容量（KB）。
- **S0C/S1C**: Survivor 0 和 Survivor 1 区的容量（KB）。
- **EC**: Eden 区的容量（KB）。
- **OGCMN/OGCMX**: 老年代的最小和最大容量（KB）。
- **OGC**: 当前老年代容量（KB）。
- **OC**: 当前老年代使用容量（KB）。
- **MCMN/MCMX**: 元数据区的最小和最大容量（KB）。
- **MC**: 当前元数据区容量（KB）。
- **CCSMN/CCSMX**: 压缩类空间的最小和最大容量（KB）。
- **CCSC**: 当前压缩类空间容量（KB）。
- **YGC/FGC**: 年轻代和 Full GC 的次数。

---

### 3.4 `-gcutil`：监控垃圾回收的统计信息（百分比形式）
```bash
jstat -gcutil <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次垃圾回收统计信息，共输出 5 次。

**输出示例：**
```
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  0.00  50.00  25.00  10.00  95.00  90.00    10     0.250     2     0.500    0.750
```

- **S0/S1**: Survivor 0 和 Survivor 1 区的使用百分比。
- **E**: Eden 区的使用百分比。
- **O**: 老年代的使用百分比。
- **M**: 元数据区的使用百分比。
- **CCS**: 压缩类空间的使用百分比。
- **YGC/YGCT**: 年轻代 GC 的次数和总耗时。
- **FGC/FGCT**: Full GC 的次数和总耗时。
- **GCT**: 所有 GC 的总耗时。

---

### 3.5 `-gccause`：显示最近一次垃圾回收的原因
```bash
jstat -gccause <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次垃圾回收原因，共输出 5 次。

**输出示例：**
```
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT    LGCC                 GCC
  0.00  50.00  25.00  10.00  95.00  90.00    10     0.250     2     0.500    0.750 Allocation Failure   No GC
```

- **LGCC**: 最近一次垃圾回收的原因（如 `Allocation Failure`）。
- **GCC**: 当前垃圾回收的原因（如 `No GC`）。

---

#### 3.6 `-gcnew`：监控新生代的垃圾回收情况
```bash
jstat -gcnew <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次新生代垃圾回收信息，共输出 5 次。

**输出示例：**
```
 S0C    S1C    S0U    S1U   TT MTT  DSS      EC       EU     YGC     YGCT
1024.0 1024.0  0.0    0.0  15  15  5120.0  8192.0   1024.0     10     0.250
```

- **TT**: 对象在 Survivor 区的存活次数阈值。
- **MTT**: 对象在 Survivor 区的最大存活次数阈值。
- **DSS**: 期望的 Survivor 区大小（KB）。

---

### 3.7 `-gcold`：监控老年代的垃圾回收情况
```bash
jstat -gcold <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次老年代垃圾回收信息，共输出 5 次。

**输出示例：**
```
   MC       MU      CCSC     CCSU       OC          OU       YGC    FGC    FGCT     GCT
 4480.0    776.0    384.0     76.0    20480.0        0.0     10      2     0.500    0.750
```

- **MC/MU**: 元数据区的容量和使用量（KB）。
- **CCSC/CCSU**: 压缩类空间的容量和使用量（KB）。
- **OC/OU**: 老年代的容量和使用量（KB）。

---

### 3.8 `-printcompilation`：显示 JIT 编译的方法信息
```bash
jstat -printcompilation <vmid> 1000 5
```
- 每 1 秒（1000 毫秒）输出一次 JIT 编译信息，共输出 5 次。

**输出示例：**
```
Compiled  Size  Type Method
    100     50     1  java/lang/String::hashCode
```

- **Compiled**: 已编译的方法数量。
- **Size**: 编译后的代码大小（字节）。
- **Type**: 编译类型（如 1 表示普通方法）。
- **Method**: 被编译的方法名称。

---

## 4. 使用场景
- **性能调优**：通过监控堆内存和垃圾回收情况，优化 JVM 参数。
- **问题排查**：分析内存泄漏、频繁 Full GC 等问题。
- **实时监控**：实时查看 JVM 的运行状态。

---

## 5. 注意事项
1. **权限问题**：`jstat` 需要访问目标 JVM 的进程，因此需要足够的权限。
2. **性能影响**：`jstat` 本身对 JVM 的性能影响较小，但频繁调用可能会增加系统负载。
3. **版本兼容性**：不同版本的 JDK 可能支持不同的选项，建议使用与目标 JVM 匹配的 JDK 版本。

---

## 6. 参考文档
- [Oracle 官方文档：jstat](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html)
- [Java Performance Tuning Guide](https://www.oracle.com/java/technologies/javase/tuning-guide.html)

---


