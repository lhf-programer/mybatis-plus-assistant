# 1. 简介
mybatis-plus wrapper 助手，可以使myabtis-plus 的QueryWrapper 包装类使用更多功能。
**git 源码**
[https://github.com/lhf-programer/mybatis-plus-assistant](https://github.com/lhf-programer/mybatis-plus-assistant)
# 2. 使用
## 2.1 依赖
**2.1.1** **添加仓库源**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
**2.1.2** **添加依赖**
```xml
<dependency>
    <groupId>com.github.lhf-programer</groupId>
    <artifactId>mybatis-plus-assistant</artifactId>
    <version>1.0.2</version>
</dependency>
```
**注意:** **引入mybatis-plus 版本最好高于3.X.0**
## 2.2 wrapper 使用案列
**2.2.1** **创建包装器**
```java
QueryWrapperExt queryWrapper = new QueryWrapperExt();
```
**2.2.2** **增加自定义返回类**
```java
queryWrapperExt.setResultClass(CustomeClass.class);
```
**2.2.3** **增加返回类数据库字段标识注解**
表字段注解 (如有敏感字符将会加上``转义)
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface ColumnOriginal {
    String value();
}
```
忽略字段注解
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface IgnoreColumn {
}
```
使用
```java
@ColumnOriginal(value = TableColumn.NAME)
private String customField;
@IgnoreColumn
private String customField;
```
**2.2.5** **增加去重关键字（仅限于支持 distinct关键字的数据库）**
默认false
```java
queryWrapperExt.setDistinct(true);
```
**2.2.4** **增加基本判空条件语句**
支持条件语句连缀
```java
queryWrapperExt.likeIsNotNull(TableColumn.NAME, request.getName())
               .eqIsNotEmpty(TableColumn.IS_OPEN, request.getName());
```
**2.2.5** **增加链表功能**
使用 （支持连缀）
```java
QueryWrapperExt.Joiner joiner = queryWrapperExt.createJoiner(TableColumn.TABLE);
joiner.leftJoinOnEqualTo(LeftTableColumn.TABLE, TableColumn.LEFT_ID, LeftTableColumn.ID)
      .rightJoinOnEqualTo(RightTableColumn.TABLE, TableColumn.RIGHT_ID, RightTableColumn.ID);
```
**2.2.6** **mapper 部分**
interface
```java
// 普通查询
List<CustomeClass> selectTableList(@Param(Constants.WRAPPER) Wrapper wrapper);
// 分页查询
IPage<CustomeClass> selectTablePageList(@Param("page") Page<Image> page, @Param(Constants.WRAPPER) Wrapper wrapper);
```
mapper
```xml
<!-- 默认sql 推荐生成 -->
<sql id="sqlSelect">${ew.sqlSelect}</sql>
<sql id="joinTables"><foreach collection="ew.joinTables" item="itemTable" separator=" ">${itemTable}</foreach></sql>
<sql id="customSqlSegment">${ew.customSqlSegment}</sql>

<!-- 基本为 sql引用，只需编写正确的表名与返回类即可 -->
<select id="selectTableList" resultType="com.lvhaifeng.mybatis.vo.response.CustomeClass">
    select <include refid="sqlSelect"></include> from table <include refid="joinTables"></include> <include refid="customSqlSegment"></include>
</select>
```
## 2.3 QueryHelper
**2.3.1** **基本条件封装方法**
```java
/** 
 * entity 封装实体
 * sortProp 排序字段
 * sortType 排序类型 (asc, desc)
 */
QueryHelper.initQueryWrapper(entity, sortProp, sortType);
```
**2.3.2** **基本wrapper 排序方法**
```java
/** 
 * queryWrapper 查询 wrapper
 * sortProp 排序字段
 * sortType 排序类型 (asc, desc)
 */
QueryHelper.resolverSort(queryWrapper, sortProp, sortType);
```
**2.3.3** **基本page 排序方法**
```java
/** 
 * sortProp 排序字段
 * sortType 排序类型 (asc, desc)
 */
List<OrderItem> orderItems = QueryHelper.resolverSort(sortProp, sortType);
```
## 2.4 代码生成器
**2.4.1** **git 源码**
[https://github.com/lhf-programer/code-generator](https://github.com/lhf-programer/code-generator) 
**2.4.2** **添加仓库源**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
**2.4.3** **添加依赖**
```xml
<dependency>
    <groupId>com.github.lhf-programer</groupId>
    <artifactId>code-generator</artifactId>
    <version>v1.2.14</version>
</dependency>
```
**2.4.4** **使用（如果其他功能不生成可以取消勾选，主要生成entity，entityColumn，mapper.xml）**
```java
// 是否生成 java
private static final boolean ISJAVA = false;
// 是否生成 vue
private static final boolean ISVUE = true;

public static void main(String[] args) {
    try {
        // 默认构造器则都为 true
        // new GeneratorWindow();
        new GeneratorWindow(ISJAVA, ISVUE).pack();
    } catch (Exception ex) {
        System.out.println(ex.getMessage());
    }
}
```
