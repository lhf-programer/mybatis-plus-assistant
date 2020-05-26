package com.lvhaifeng.mybatis.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lvhaifeng.mybatis.annotation.ColumnOriginal;
import com.lvhaifeng.mybatis.annotation.IgnoreColumn;
import com.lvhaifeng.mybatis.utils.ConvertUtils;
import com.lvhaifeng.mybatis.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description queryWrapper 扩展
 * @Author haifeng.lv
 * @Date 2020/5/25 16:14
 */
public class QueryWrapperExt<T> extends QueryWrapper<T> {
    /**
     * 返回类
     */
    private Class resultClass;
    private final List<String> resultFields = new ArrayList<>();
    /**
     * 链表
     */
    private final List<String> joinTables = new ArrayList<>();
    /**
     * 去重
     */
    private boolean isDistinct;
    private static final String DISTINCT = "distinct ";
    private static final String LINK_SYMBOL = ".";
    /**
     * 表的别名
     */
    private String tableAlias;

    public QueryWrapper<T> eqIsNotEmpty(String column, Object val) {
        if (StringUtils.isNotEmpty(val)) {
            return super.eq(column, val);
        }
        return this;
    }

    public Joiner createJoiner(String firstTable) {
        this.joinTables.clear();
        return new Joiner(firstTable);
    }

    public QueryWrapperExt() {
        super();
    }

    public QueryWrapperExt(T entity) {
        super(entity);
    }

    public QueryWrapperExt(T entity, String... columns) {
        super(entity, columns);
    }

    public void setDistinct(boolean distinct) {
        isDistinct = distinct;
        build();
    }

    private void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        build();
    }

    public void setResultClass(Class resultClass) {
        this.resultClass = resultClass;

        // 获取所有字段
        Field[] fields = resultClass.getDeclaredFields();

        for (Field field : fields) {
            IgnoreColumn fieldIgnoreColumn = field.getAnnotation(IgnoreColumn.class);
            if (fieldIgnoreColumn != null) {
                continue;
            }

            ColumnOriginal fieldColumnOriginal = field.getAnnotation(ColumnOriginal.class);
            if (null == fieldColumnOriginal) {
                this.resultFields.add(ConvertUtils.camelToUnderline(field.getName()) + "as" + field.getName());
            } else {
                String column = fieldColumnOriginal.value();
                this.resultFields.add(column + " as " + field.getName());
            }
        }

        this.build();
    }

    /**
     * @Description 构建 sql
     * @Author haifeng.lv
     * @Date 2020/5/26 10:13
     */
    private void build() {
        List<String> tableColumn = new ArrayList<>(this.resultFields);

        if (StringUtils.isNotEmpty(this.tableAlias)) {
            tableColumn = tableColumn.stream().map(resultField -> {
                if (resultField.indexOf(LINK_SYMBOL) != -1) {
                    return resultField;
                }
                return this.tableAlias + LINK_SYMBOL + resultField;
            }).collect(Collectors.toList());
        }

        if (!tableColumn.isEmpty() && this.isDistinct) {
            String firstField = tableColumn.get(0);
            if (!firstField.startsWith(DISTINCT)) {
                tableColumn.set(0, DISTINCT + firstField);
            }
        }

        if (!tableColumn.isEmpty()) {
            super.select(tableColumn.toArray(new String[tableColumn.size()]));
        }
    }

    /**
     * @Description 链表
     * @Author haifeng.lv
     * @Date 2020/5/26 11:52
     */
    public class Joiner {
        private String doJoin(String join, String rightTable, String leftColumnName, String symbol, String rightColumnName) {
            return join + " " + rightTable + " on " + leftColumnName + " " + symbol + " " + rightColumnName;
        }

        public Joiner innerJoin(String rightTable, String leftColumnName, String symbol, String rightColumnName) {
            String joinString = doJoin("inner join", rightTable, leftColumnName, symbol, rightColumnName);
            joinTables.add(joinString);
            return this;
        }

        public Joiner innerJoinOnEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, "=", rightColumnName);
        }

        public Joiner innerJoinOnNotEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, "<>", rightColumnName);
        }

        public Joiner innerJoinOnGreaterThan(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, ">", rightColumnName);
        }

        public Joiner innerJoinOnThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, ">=", rightColumnName);
        }

        public Joiner innerJoinOnLessThan(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, "<", rightColumnName);
        }

        public Joiner innerJoinOnLessThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return innerJoin(rightTable, leftColumnName, "<=", rightColumnName);
        }

        public Joiner leftJoin(String rightTable, String leftColumnName, String symbol, String rightColumnName) {
            String joinString = doJoin("left join", rightTable, leftColumnName, symbol, rightColumnName);
            joinTables.add(joinString);
            return this;
        }

        public Joiner leftJoinOnEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, "=", rightColumnName);
        }

        public Joiner leftJoinOnNotEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, "<>", rightColumnName);
        }

        public Joiner leftJoinOnGreaterThan(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, ">", rightColumnName);
        }

        public Joiner leftJoinOnThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, ">=", rightColumnName);
        }

        public Joiner leftJoinOnLessThan(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, "<", rightColumnName);
        }

        public Joiner leftJoinOnLessThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, "<=", rightColumnName);
        }

        public Joiner rightJoin(String rightTable, String leftColumnName, String symbol, String rightColumnName) {
            String joinString = doJoin("right join", rightTable, leftColumnName, symbol, rightColumnName);
            joinTables.add(joinString);
            return this;
        }

        public Joiner rightJoinOnEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return leftJoin(rightTable, leftColumnName, "=", rightColumnName);
        }

        public Joiner rightJoinOnNotEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return rightJoin(rightTable, leftColumnName, "<>", rightColumnName);
        }

        public Joiner rightJoinOnGreaterThan(String rightTable, String leftColumnName, String rightColumnName) {
            return rightJoin(rightTable, leftColumnName, ">", rightColumnName);
        }

        public Joiner rightJoinOnThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return rightJoin(rightTable, leftColumnName, ">=", rightColumnName);
        }

        public Joiner rightJoinOnLessThan(String rightTable, String leftColumnName, String rightColumnName) {
            return rightJoin(rightTable, leftColumnName, "<", rightColumnName);
        }

        public Joiner rightJoinOnLessThanOrEqualTo(String rightTable, String leftColumnName, String rightColumnName) {
            return rightJoin(rightTable, leftColumnName, "<=", rightColumnName);
        }

        public Joiner appendCondition(String conditionValue) {
            joinTables.add(conditionValue);
            return this;
        }

        public Joiner(String firstTable) {
            setTableAlias(firstTable);
            joinTables.add(firstTable);
        }
    }
}
