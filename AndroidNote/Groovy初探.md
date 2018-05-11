# Groovy初探

## **一、groovy简介和环境搭建**

**本机环境**：

ubuntu 14.04 64bit

JDK 1.7.67

IDE : **intellij**  idea  13.1

### **1、groovy简介**

简单说来：**Groovy是一种运行在JVM上的动态语言**，它吸取了Python,Ruby和Smalltalk等语言的优点，在Java语言的基础之上增加了许多特色功能；对于Java开发人员来说掌握Groovy是没有什么太大障碍的；相比 Java 而言，语法更易懂，更易上手，更易调试；无缝的集成了Java 的类和库；编译后的.groovy也是以class的形式出现的。

### **2、groovy下载******

网址：[http://groovy.codehaus.org/Download](http://groovy.codehaus.org/Download)

### **3、groovy环境配置和Hello World!**

#### **1)首先解压：**

unzip groovy-binary-2.3.6.zip #解压groovyunzip groovy-docs-2.3.6.zip #解压docs

#### **2) 进入到Groovy Shell命令界面：**

在Groovy Shell里不必定义class可以直接写代码，如下面进行一个for循环：

groovy:000> **for(i=0;i<10;i++){**

groovy:001> **println("i:"+i);}**

注意这里，你可以发现i是没有指定int类型的，这里也是写法上也是比较随意的。

#### **3)、将groovy加入到环境变量（可选）**

将解压后的groovy拷到/usr/local/groovy 目录下：

root@amosli-ThinkPad:/usr/local/groovy# cp -r   /home/amosli/developsoft/language/groovy/groovy-2.3.6 .

将groovy路径拷到/etc/profile里：

gedit /etc/profile  #使用gedit打开profile,也可以使用vi等工具

将下面内容拷到profile里最后位置：

```shell
export GROOVY_HOME=/usr/local/groovy/groovy-2.3.6
export PATH=$GROOVY_HOME/bin:$PATH:.
export GROOVY_HOME
export PATH
```

**更新环境变量：**

```shell
source /etc/profile
```

**验证是否成功：**

\# **groovy -version**

Groovy Version: 2.3.6 JVM: 1.7.0_67 Vendor: Oracle Corporation OS: Linux

**打开groovyConsole:**

## **二、Groovy初探**

### **1、Groovy和Java对比**

· Groovy 的松散的 Java 语法允许省略分号和修改符。

· 除非另行指定，Groovy 的所有内容都为 public。

· Groovy 允许定义简单脚本，同时无需定义正规的 class 对象。

· Groovy 在普通的常用 Java 对象上增加了一些独特的方法和快捷方式，使得它们更容易使用。

· Groovy 语法还允许省略变量类型。

· **关于闭包:**可以将*闭包* 想像为一个代码块，可以现在定义，以后再执行。可以使用这些强大的构造做许多漂亮的事，不过最著名的是简化迭代。使用 Groovy 之后，就有可能再也不需要编写 Iterator 实例了。

· **动态的 Groovy:** 从技术上讲，Groovy 可能是您最近听说过的类型最松散的动态语言之一。从这个角度讲，Groovy 与 Java 语言的区别很大，Java 语言是一种固定类型语言。在 Groovy 中，类型是可选的，所以您不必输入 String myStr = "Hello"; 来声明 String 变量。可以直接使用def进行不指定类型定义，类似于js中的var。

· **与Java互用：**用 Groovy 编写的任何内容都可以编译成标准的 Java 类文件并在 Java 代码中重用。类似地，用标准 Java 代码编写的内容也可以在 Groovy 中重用。

### **2、实例演示Java和Groovy的区别**

#### **用 Java 编写的 Hello World**

用 Java 编写的典型的 Hello World 示例如下所示：

```java
public class HelloWorld {

  public static void main(String[] args) {    

    System.out.println("Hello World!");

  }
}
```

#### **编译和运行 Java 示例**

在这个简单的 HelloWorld 类中，我省略了包，而且向控制台输出的时候没有使用任何多余的编码约定。下一步是用 javac 编译这个类，如下所示：

c:>javac HelloWorld.java

最后，运行经过编译的类：

c:>java HelloWorld

迄今为止还不错 — 很久以前就会编这么基础的代码了，所以这里只是回顾一下。下面，请看用 Groovy 编码的相同过程。

#### **用 Groovy 编写的 Hello World**

就像前面提到过的，Groovy 支持松散的 Java 语法 — 例如，不需要为打印 “Hello World!” 这样的简单操作定义类。而且，Groovy 使日常的编码活动变得更容易，例如，Groovy 允许输入 println，而无需输入 System.out.println。当您输入 println时，Groovy 会非常聪明地知道您指的是 System.out。

所以，用 Groovy 编写 Hello World 程序就如下面这样简单：

println "Hello World!"

请注意，在这段代码周围没有类结构，而且也没有方法结构！我还使用 println 代替了 System.out.println。

#### **运行 Groovy 示例**

假设我将代码保存在文件/home/amosli/developsoft/language/groovy/test/Hello.groovy 内，只要输入以下代码就能运行这个示例：

amosli@amosli-ThinkPad:~/developsoft/language/groovy/groovy-2.3.6/bin$ **./groovy**  **../../test/****Hello.groovy**

Hello World!

**如果已经配置了groovy的环境变量，那么只需要输入以下命令即可：**

root@amosli-ThinkPad:/home/amosli/developsoft/language/groovy/test#** groovy Hello.groovy **

Hello World! 

在控制台上输出 “Hello World!” 所需的工作就这么多。

#### **更快捷的方式**

amosli@amosli-ThinkPad:~/developsoft/language/groovy/groovy-2.3.6/bin$** ./groovy** **-e ****"println 'helloworld '"**

helloworld 

**如果已经配置了groovy的环境变量，那么只需要输入以下命令即可：**

root@amosli-ThinkPad:/home/amosli/developsoft/language/groovy/test#** groovy -e ****"println 'helloworld '"**

helloworld 

这会生成相同的结果，而且甚至**无需定义任何文件**！

### **3、Groovy 是没有类型的 Java 代码**

很可能将 Groovy 当成是没有规则的 Java 代码。但实际上，Groovy 只是规则少一些。这一节的重点是使用 Groovy 编写 Java 应用程序时可以不用考虑的一个 Java 编程的具体方面：类型定义。

#### **为什么要有类型定义？**

在 Java 中，如果要声明一个 String 变量，则必须输入：

```java
String value="Hello World"
```

但是，如果仔细想想，就会看出，等号右侧的字符已经表明 value 的类型是 String。所以，Groovy 允许省略 value 前面的 String 类型变量，并用 def 代替。

```groovy
def value="Hello World"
//或者
value="Hello World"
```

实际上，Groovy 会根据对象的值来判断它的类型。

#### **运行程序！**

将 HelloWorld.groovy 文件中的代码编辑成下面这样：

```groovy
String message="Hello World"
println message.class
println message
```

### **4、通过 Groovy 进行循环**

同大多数脚本语言一样，Groovy 经常被宣传为*生产力更高* 的 Java 语言替代品。您已经看到了 Groovy 缺少类型能够如何减少打字工作。在这一节，将创建并试用一个 repeat 函数。在这个过程中，将进一步探索 Groovy 提高效率的方式。

**更好、更短的循环**

下面这种方法可以更好地感受 Groovy 缺乏类型的好处：首先，用与创建 HelloWorld 相同的方式创建一个 Groovy 类，将这个类称为 MethodMadness，并删除自动生成的类体：将要定义一个独立的 repeat 函数。现在在控制台中输入以下代码： 

```groovy
def repeat(val){
 for(i = 0; i < 5; i++){
  println val
 }
}
```

起初，从 Java 的角度来看，这个小函数看起来可能有些怪（实际上，它很像 JavaScript）。但它就是 Java 代码，只不过是用 Groovy 的样式编写的。

**深入方法**

repeat 函数接受一个变量 val。请注意参数不需要 def。方法体本质上就是一个 for 循环。 调用这个函数。 会输出 “hello world” 五次。请注意，for 循环中省略了 int。没有变量类型的 for 循环要比标准的 Java 代码短些。现在看看如果在代码里加入范围会出现什么情况。

**Groovy 中的范围**

*范围* 是一系列的值。例如 “0..4” 表明*包含* 整数 0、1、2、3、4。Groovy 还支持排除范围，“0..<4” 表示 0、1、2、3。还可以创建字符范围：“a..e” 相当于 a、b、c、d、e。“a..<e” 包括小于 *e* 的所有值。

**循环范围**

范围为循环带来了很大的方便。例如，前面从 0 递增到 4 的 for 循环如下所示：

范围可以将这个 for 循环变得更简洁，更易阅读：

```groovy
def repeat(val){

 for(i in 0..5){

  println val

 }

}
```

**设置范围**

如果运行这个示例，可能会注意到一个小问题：“Hello World” 输出了六次而不是五次。这个问题有三种解决方法： 

· 将包含的范围限制到 4： 

· 从 1 而不是 0 开始： 

```groovy
def repeat(val){

  for(i in 1..5){
   println val
  }
}
```

· 将范围由包含改为排除： 

```groovy
def repeat(val){

  for(i in 0..<5){

   println val

  }

}
```

不论采用哪种方法，都会得到原来的效果 — 输出 “Hello World” 五次。

**默认参数值**

现在已经成功地使用 Groovy 的范围表达式缩短了 repeat 函数。但这个函数依然有些限制。如果想重复 “Hello World” 八次该怎么办？如果想对不同的值重复不同次数 — 比如 “Hello World” 重复八次，“Goodbye Sunshine” 重复两次，这时该怎么办？

每次调用 repeat 时都要指定需要的重复次数的做法已经过时了，特别是在已经适应了默认行为（重复五次）的时候。 

Groovy 支持*默认参数值*，可以在函数或方法的正式定义中指定参数的默认值。调用函数的程序可以选择省略参数，使用默认值。

**更加复杂的参数值**

使用前面的 repeat 函数时，如果希望调用程序能够指定重复值，可以像下面这样编码： 

```groovy
def repeat(val, repeat=5){

 for(i in 0..<repeat){

  println val

 }

}
```

像下面这样调用该函数： 

```groovy
repeat("Hello World", 2)

repeat("Goodbye sunshine", 4)

repeat("foo")
//结果会输出 “Hello World” 两次，“Goodbye sunshine” 四次，“foo” 五次（默认次数）。
```

### 5、Groovy中的集合 

#### 1)、Groovy  中的List 

```groovy
def  range=0..4
println range.class
assert range instanceof List
```

请注意，assert 命令用来证明范围是 java.util.List 的实例。接着运行这个代码，证实该范围现在是类型 List 的集合。

Groovy 的语法:

```groovy
def coll=["Groovy","java","Ruby"]

assert coll instanceof Collection
assert coll instanceof ArrayList
```

你将会注意到，coll 对象看起来很像 Java 语言中的数组。实际上，它是一个 Collection。要在普通的 Java 代码中得到集合的相同实例，必须执行以下操作：

```java
Collection<String> coll = new ArrayList<String>();

coll.add("Groovy");

coll.add("Java");

coll.add("Ruby");
```

在 Java 代码中，必须使用 add() 方法向 ArrayList 实例添加项。

**而Groovy中则提供了3种方法：**

```groovy
coll.add("Python")

coll << "Smalltalk"

coll[5] = "Perl"
```

##### **查找元素:**

如果需要从集合中得到某个特定项，可以通过像上面那样的位置参数获取项。例如，如果想得到第二个项 “Java”，可以编写下面这样的代码（请记住集合和数组都是从 0 开始）：

```groovy
assert coll[1] == "Java"
```

Groovy 还允许在集合中增加或去掉集合，如下所示：

```groovy
def numbers=[1,2,3,4]
assert numbers+5==[1,2,3,4,5]
assert numbers-[2,3]==[1,4]
```

**Groovy中的特殊方法：**

```groovy
def numbers=[1,2,3,4]
assert numbers.join(",")=="1,2,3,4"
assert [1,2,3,4,3].count(3)==2
```

**join() 和 count()** 只是在任何项List上都可以调用的众多方便方法中的两个。分布操作符（spread operator） 是个特别方便的工具，使用这个工具不用在集合上迭代，就能够调用集合的每个项上的方法。

假设有一个 String 列表，现在想将列表中的项目全部变成大写，可以编写以下代码：

```groovy
assert["JAVA","GROOVY"]==["Java","Groovy"]*.toUpperCase()
```

请注意 *. 标记。对于以上列表中的每个值，都会调用 toUpperCase()，生成的集合中每个 String 实例都是大写的。 

#### **2）Groovy中的Map**

Java 语言中的映射是名称-值对的集合。所以，要用 Java 代码创建典型的映射，必须像下面这样操作：

```java
Map<String, String>map = new HashMap<String, String>();

map.put("name", "Andy");

map.put("VPN-#","45");
for(Iterator iter = map.entrySet().iterator(); iter.hasNext();){
 Map.Entry entry = (Map.Entry)iter.next();
 System.out.println(entry.getKey() + " : " + entry.getValue());
}
```

Groovy 使得处理映射的操作像处理列表一样简单 — 例如，可以用 Groovy 将上面的 Java 映射写成

```groovy
"ITERATION".each{
 println it.toLowerCase()
}
```

请注意，Groovy 映射中的键不必是 String。在这个示例中，name 看起来像一个变量，但是在幕后，Groovy 会将它变成 String。

验证hash格式：

```groovy
assert hash.getClass() == java.util.LinkedHashMap 
```

Groovy 中Hash的Set/Get

//方法1

```groovy
hash.put("id", 23)
assert hash.get("name") == "Andy"
```

//方法2

```groovy
hash.dob = "01/29/76"

//. 符号还可以用来获取项。例如，使用以下方法可以获取 dob 的值：
assert hash.dob == "01/29/76" 
```

//方法3

```groovy
//还可以使用假的位置语法将项放入映射，或者从映射获取项目
assert hash["name"] == "Andy"

hash["gender"] = "male"
assert hash.gender == "male"
assert hash["gender"] == "male"
```

请注意，在使用 [] 语法从映射获取项时，必须将项作为 String 引用。

### **6、Groovy 中的闭包**

 Java 的 Iterator 实例，用它在集合上迭代，就像下面这样：

```java
def acoll = ["Groovy", "Java", "Ruby"]

 for(Iterator iter = acoll.iterator(); iter.hasNext();){
 	println iter.next()
 }
```

请注意，each 直接在 acoll 实例内调用，而 acoll 实例的类型是 ArrayList。在 each 调用之后，引入了一种新的语法 —{，然后是一些代码，然后是 }。由 {} 包围起来的代码块就是闭包。

```groovy
def acoll = ["Groovy", "Java", "Ruby"]   

acoll.each{

 println it

}
```

闭包中的 it 变量是一个关键字，指向被调用的外部集合的每个值 — 它是默认值，可以用传递给闭包的参数覆盖它。下面的代码执行同样的操作，但使用自己的项变量：

```groovy
def acoll = ["Groovy", "Java", "Ruby"]  

acoll.each{ value ->

 println value

}
```

在这个示例中，用 value 代替了 Groovy 的默认 it。

```groovy
def hash = [name:"Andy", "VPN-#":45]

hash.each{ key, value ->

 println "{key} : {value}"

}
```

请注意，闭包还允许使用多个参数 — 在这个示例中，上面的代码包含两个参数（key 和 value）。

** 请记住，凡是集合或一系列的内容，都可以使用下面这样的代码进行迭代。**

```groovy
> "amosli".each{

println it.toUpperCase();

}
```

```groovy
def excite = {

word-> return "this is ${word} "

};
 
```

这段代码是名为 excite 的闭包。这个闭包接受一个参数（名为 word），返回的 String 是 word 变量加两个感叹号。请注意在 String 实例中替换 的用法。在 String 中使用 ${value}语法将告诉 Groovy 替换 String 中的某个变量的值。可以将这个语法当成 return word + "!!" 的快捷方式。

//可以通过两种方法调用闭包：直接调用或者通过 call() 方法调用。

```groovy
excite("Java");

excite.call("Groovy")

//输出：this is Groovy 
```

### **7、Groovy 中的类**

我们先从用 Groovy 定义一个简单的 JavaBean 形式的类开始，这个类称为 Song。

第一步自然是用 Groovy 创建名为 Song 的类。这次还要为它创建一个包结构 — 创建一个包名，例如 org.acme.groovy。

创建这个类之后，删除 Groovy 插件自动生成的 main()。

歌曲有一些属性 — 创作歌曲的艺术家、歌曲名称、风格等等。请将这些属性加入新建的 Song 类，如下所示：

```groovy
class Song {
 def name
 def artist
 def genre
}
```

**Groovy 类就是 Java 类**

应该还记得本教程前面说过 Groovy 编译器为用 Groovy 定义的每个类都生成标准的 Java .class。还记得如何用 Groovy 创建 HelloWorld 类、找到 .class 文件并运行它么？也可以用新定义的 Song 类完成同样的操作。如果通过 Groovy 的 groovyc 编译器编译代码（Eclipse Groovy 插件已经这样做了），就会生成一个 Song.class 文件。

这意味着，如果想在另一个 Groovy 类或 Java 类中使用新建的 Song 类，则必须*导入* 它（当然，除非使用 Song 的代码与 Song 在同一个包内）。

接下来创建一个新类，名为 SongExample，将其放在另一个包结构内，假设是 org.thirdparty.lib。

现在应该看到如下所示的代码：

```groovy
package org.thirdparty.lib

class SongExample {

 static void main(args) {}

}
```

**类的关系**

现在是使用 Song 类的时候了。首先导入实例，并将下面的代码添加到 SongExample 的 main() 方法中。 

```groovy
package org.thirdparty.lib

import org.acme.groovy.Song

class SongExample {
 static void main(args) {
  def sng = new Song(name:"Le Freak", 
    artist:"Chic", genre:"Disco")
 }
```

在 Song 实例创建完成了！但是仔细看看以前定义的 Song 类的初始化代码，是否注意到什么特殊之处？您应该注意到自动生成了构造函数。

Groovy 自动提供一个构造函数，构造函数接受一个名称-值对的映射，这些名称-值对与类的属性相对应。这是 Groovy 的一项开箱即用的功能 — 用于类中定义的任何属性，Groovy 允许将存储了大量值的映射传给构造函数。映射的这种用法很有意义，例如，您不用初始化对象的每个属性。

```groovy
//也可以添加下面这样的代码：
def sng2 = new Song(name:"Kung Fu Fighting", genre:"Disco")

//也可以像下面这样直接操纵类的属性:
def sng3 = new Song()
sng3.name = "Funkytown"
sng3.artist = "Lipps Inc."
sng3.setGenre("Disco")

assert sng3.getArtist() == "Lipps Inc."

//在Song类中添加如下代码：
String toString(){

 "{name}, {artist}, ${genre}"

}
```

从这个代码中明显可以看出，Groovy 不仅创建了一个构造函数，允许传入属性及其值的映射，还可以通过 . 语法间接地访问属性。而且，Groovy 还生成了标准的 setter 和 getter 方法。

在进行属性操纵时，非常有 Groovy 特色的是：总是会调用 setter 和 getter 方法 — 即使直接通过 . 语法访问属性也是如此。

**核心的灵活性**

Groovy 是一种本质上就很灵活的语言。例如，看看从前面的代码中将 setGenre() 方法调用的括号删除之后会怎么样，如下所示：

```groovy
sng3.setGenre "Disco"

assert sng3.genre == "Disco"
```

在 Groovy 中，对于接受参数的方法，可以省略括号 — 在某些方面，这样做会让代码更容易阅读。 

**方法覆盖**

迄今为止已经成功地创建了 Song 类的一些实例。但是，它们还没有做什么有趣的事情。可以用以下命令输出一个实例：

在 Java 中这样只会输出所有对象的默认 toString() 实现，也就是类名和它的 hashcode（即 org.acme.groovy.Song@44f787）。下面来看看如何覆盖默认的 toString() 实现，让输出效果更好。

在 Song 类中，添加以下代码： 

```groovy
String toString(){

 "{name}, {artist}, ${genre}"

}
```

根据本教程已经学到的内容，可以省略 toString() 方法上的 public 修改符。仍然需要指定返回类型（String），以便实际地覆盖正确的方法。方法体的定义很简洁 — 但 return 语句在哪？ 

**不需要 return **

您可能已经想到：在 Groovy 中可以省略 return 语句。Groovy 默认返回方法的最后一行。所以在这个示例中，返回包含类属性的 String。

重新运行 SongExample 类，应该会看到更有趣的内容。toString() 方法返回一个描述，而不是 hashcode。

**特殊访问**

Groovy 的自动生成功能对于一些功能来说很方便，但有些时候需要覆盖默认的行为。例如，假设需要覆盖 Song 类中 getGenre() 方法，让返回的 String 全部为大写形式。 

提供这个新行为很容易，只要定义 getGenre() 方法即可。可以让方法的声明返回 String，也可以完全省略它（如果愿意）。下面的操作可能是最简单的： 

```groovy
def getGenre(){

 genre.toUpperCase()

}
```

同以前一样，这个简单方法省略了返回类型和 return 语句。现在再次运行 SongExample 类。应该会看到一些意外的事情 —— 出现了空指针异常。

**空指针安全性**

如果您一直在跟随本教程，那么应该已经在 SongExample 类中加入了下面的代码：

```groovy
assert sng3.genre == "Disco"
```

结果在重新运行 SongExample 时出现了断言错误 — 这正是为什么在 Eclipse 控制台上输出了丑陋的红色文字。（很抱歉使用了这么一个糟糕的技巧） 

幸运的是，可以轻松地修复这个错误：只要在 SongExample 类中添加以下代码： 

```groovy
println sng2.artist.toUpperCase()
```

但是现在控制台上出现了*更多的* 红色文本 — 出什么事了？！

**可恶的 null**

如果回忆一下，就会想起 sng2 实例没有定义 artist 值。所以，在调用 toUpperCase() 方法时就会生成 Nullpointer 异常。幸运的是， Groovy 通过 ? 操作符提供了一个安全网 — 在方法调用前面添加一个 ? 就相当于在调用前面放了一个条件，可以防止在 null 对象上调用方法。

例如，将 sng2.artist.toUpperCase() 行替换成 sng2.artist?.toUpperCase()。请注意，也可以省略后面的括号。（Groovy 实际上也允许在不带参数的方法上省略括号。不过，如果 Groovy 认为您要访问类的属性而不是方法，那么这样做可能会造成问题。）重新运行 SongExample 类，您会发现 ? 操作符很有用。在这个示例中，没有出现可恶的异常。现在将下面的代码放在这个类内，再次运行代码。

```groovy
def sng4 = new Song(name:"Thriller", artist:"Michael Jackson")

println sng4
```

**就是 Java**

您将会注意到，虽然预期可能有异常，但是没有生成异常。即使没有定义 genre，getGenre() 方法也会调用 toUpperCase()。

您还记得 Groovy 就是 Java，对吧？所以在 Song 的 toString() 中，引用了 genre 属性本身，所以不会调用 getGenre()。现在更改 toString() 方法以使用 getGenre()，然后再看看程序运行的结果。

```groovy
 String toString(){

 "{name}, {artist}, ${getGenre()}"

}
```

重新运行 SongExample，出现类似的异常。现在，请自己尝试修复这个问题，看看会发生什么。

**另一个方便的小操作符**

希望您做的修改与我的类似。在下面将会看到，我进一步扩充了 Song 类的 getGenre() 方法，以利用 Groovy 中方便的 ? 操作符。

```groovy
def getGenre(){

 genre?.toUpperCase()

}
```

? 操作符时刻都非常有用，可以极大地减少条件语句。

###  8、Groovy中的单元测试

本教程一直都强调 Groovy 只是 Java 的一个变体。您已经看到可以用 Groovy 编写并使用标准的 Java 程序。为了最后一次证明这点，在结束本教程之前，我们将通过 JUnit *利用 Java* 对 Song 类进行单元测试。

**将 JUnit 加入 Eclipse 项目**

为了跟上本节的示例，需要将 JUnit 加入到 Eclipse 项目中。首先，右键单击项目，选择 **Build Path**，然后选择 **Add Libraries**，如图 14 所示：

**将 JUnit 加入到项目的构建路径**

 ![junit1](D:\我的文档\My Pictures\junit1.png)

会出现 **Add Library** 对话框，如图 15 所示。

**图 15. 从库列表中选择 JUnit**

 ![junit2](D:\我的文档\My Pictures\junit2.png)

选择 JUnit 并单击 **Next** 按钮。应该会看到如图 16 所示的对话框。选择 **JUnit3** 或 **4**— 具体选择哪项全凭自己决定 — 并单击 **Finish** 按钮。 

 ![junit3](D:\我的文档\My Pictures\junit3.png)

**设置新的测试用例**

现在在项目的类路径中加入了 JUnit，所以能够编写 JUnit 测试了。请右键单击 java 源文件夹，选择 **New**，然后选择 **JUnit Test Case**。定义一个包，给测试用例命名（例如 SongTest），在 Class Under Test 部分，单击 **Browse** 按钮。 

请注意，可以选择用 Groovy 定义的 Song 类。图 17 演示了这一步骤：

**图 17.找到 Song 类**

 ![junit4](D:\我的文档\My Pictures\junit4.png)

选择该类并单击 **OK**（应该会看到与图 18 类似的对话框）并在 New JUnit Test Case 对话框中单击 **Finish** 按钮。 

 ![junit5](D:\我的文档\My Pictures\junit5.png)

**定义测试方法**

我选择使用 JUnit 4；所以我定义了一个名为 testToString() 的测试方法，如下所示： 

```java
package org.acme.groovy;
import org.junit.Test;

public class SongTest {
 @Test
 public void testToString(){}
}
```

**测试 toString**

显然，需要验证 toString() 方法是否没有问题，那么第一步该做什么呢？如果想的是 “导入 Song 类”，那么想得就太难了 —Song 类在同一个包内，所以第一步是创建它的实例。

在创建用于测试的 Song 实例时，请注意不能通过传给构造函数的映射完全初始化 — 而且，如果想自动完成实例的 setter 方法，可以看到每个 setter 接受的是 Object 而不是 String（如图 19 所示）。为什么会这样呢？ 

**图 19. 所有的 setter 和 getter**

 ![junit6](D:\我的文档\My Pictures\junit6.png)

**Groovy 的功劳**

如果回忆一下，就会记得我在本教程开始的时候说过： 

因为 Java 中的每个对象都扩展自 java.lang.Object，所以即使在最坏情况下，Groovy 不能确定变量的类型，Groovy 也能将变量的类型设为 Object然后问题就会迎刃而解。 

现在回想一下，在定义 Song 类时，省略了每个属性的类型。Groovy 将自然地将每个属性的类型设为 Object。所以，在标准 Java 代码中使用 Song 类时，看到的 getter 和 setter 的参数类型和返回类型全都是 Object。

**修正返回类型**

为了增添乐趣，请打开 Groovy Song 类，将 artist 属性改为 String 类型，而不是无类型，如下所示： 

```groovy
package org.acme.groovy

class Song {
 def name
 String artist
 def genre
  
 String toString(){
  "{name}, {artist}, ${getGenre()}"
 }
  
 def getGenre(){
  genre?.toUpperCase()
 }
}
```

现在，回到 JUnit 测试，在 Song 实例上使用自动完成功能 — 看到了什么？ 

在图 20 中（以及您自己的代码中，如果一直跟随本教程的话），setArtist() 方法接受一个 String，*而不是*Object。Groovy 再次证明了它就是 Java，而且应用了相同的规则。

**图 20. String，而不是 object**

 ![junit7](D:\我的文档\My Pictures\junit7.png)

**始终是普通的 Java**

返回来编写测试，另外请注意，默认情况下 Groovy 编译的类属性是私有的，所以不能直接在 Java 中访问它们，必须像下面这样使用 setter： 

```groovy
@Test
public void testToString(){
 Song sng = new Song();
 sng.setArtist("Village People");
 sng.setName("Y.M.C.A");
 sng.setGenre("Disco");
		
 Assert.assertEquals("Y.M.C.A, Village People, DISCO", 
   sng.toString());
}
```

编写这个测试用例余下的代码就是小菜一碟了。测试用例很好地演示了这样一点：用 Groovy 所做的一切都可以轻易地在 Java 程序中重用，反之亦然。用 Java 语言执行的一切操作和编写的一切代码，在 Groovy 中也都可以使用

如果说您从本教程获得了一个收获的话（除了初次体验 Groovy 编程之外），那么这个收获应该是深入地认识到 Groovy 就是 Java，只是缺少了您过去使用的许多语法规则。Groovy 是没有类型、没有修改符、没有 return、没有 Iterator、不需要导入集合的 Java。简而言之，Groovy 就是丢掉了许多包袱的 Java，这些包袱可能会压垮 Java 项目。

但是在幕后，Groovy 就是 Java。







