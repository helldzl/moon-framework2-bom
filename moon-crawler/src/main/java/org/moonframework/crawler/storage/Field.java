package org.moonframework.crawler.storage;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.moonframework.core.support.Builder;
import org.moonframework.core.util.ScriptUtils;
import org.moonframework.crawler.util.RandomDateUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public class Field implements Serializable {

    private static final long serialVersionUID = -7963220476697909565L;

    // data set

    public static final String DATA_ID = "data-id";
    public static final String DATA_ORIGINAL = "data-original";
    public static final String DATA_MODEL = "data-model";
    public static final String DATA_ORDER = "data-order";

    // meta field

    public static final String ID = "_id";                      // id meta-field : identity
    public static final String NAME = "_name";                  // name meta-field : OBJECT
    public static final String VALUE = "_value";                // value meta-field : OBJECT
    public static final String JSON = "_json_";                 // json meta-field : OBJECT
    public static final String LINK = "_link";                  // link meta-field : 根据该属性指向的链接, 加入link queue队列中给fetcher使用

    // other field

    public static final String ORIGIN = "origin";               // 原始URL链接

    /**
     * [ALL] field name
     */
    private String name;

    /**
     * [ALL] css query
     */
    private String selector;

    /**
     * [ALL] [optional] element index
     */
    private int index;

    /**
     * [ALL] field type
     */
    private FieldType type;

    /**
     * [ATTRIBUTE] attribute name
     */
    private String attr;
    private String attrWhenEmpty;

    /**
     * [DATE] date format
     */
    private String dateFormat;

    /*
    *时间区域
    */
    private Locale locale;
    /**
     * [DATE|DECIMAL|TEXT] regex
     */
    private String regex;
    /*
    * 是否进行替换，true为替换  false为不替换，并且返回正则匹配出来的数据
    * */
    private Boolean regexBoolean;

    /**
     * [DATE|DECIMAL|TEXT] use with regex
     */
    private String replacement;

    /**
     * [DECIMAL] 系数, 和货币类型一起使用
     */
    private String coefficient;

    /**
     * [DATE|DECIMAL|TEXT] delimiter
     */
    private String delimiter;

    /**
     * [SUBTYPE=MEDIA]
     */
    private boolean clone = true;

    /**
     * [ELEMENT] tag name
     */
    private String tag;

    /**
     * [ELEMENT] tag attribute name
     */
    private String tagAttr;

    /**
     * [ELEMENT] 图片模式
     */
    private Model model;

    /**
     * [TEXT|HTML|ATTRIBUTE|ELEMENT] 执行脚本
     */
    private String script;

    /**
     * [ELEMENT] child fields
     */
    private List<Field> fields;

    /**
     * [TEXT|HTML|ATTRIBUTE|ELEMENT] 执行脚本的参数列表params, 可选参数
     */
    private List<Field> params;

    /**
     * 后补配置, 当primary field为空时且alternate field不为空, 使用后补配置
     */
    private Field alternate;

    /**
     * 固定值   有些字段是一个可变的固定值
     */

    private String fixedField;

    /**
     * 跨页面 数据合并 开关
     */
    private boolean stridePageMerge = false;

    /**
     *  whitelist类 tags Attributes
     */
    //private Whitelist whitelist = Whitelist.relaxed().addAttributes("iframe","frameborder","height","width","src","allowfullscreen");

    /**
     * whitelist类 tags Attributes
     */
    private Map<TagsRemoveOrAdd, Set<String>> tagsName;
    private Map<TagsRemoveOrAdd, Map<String, Set<String>>> tagsAtrrName;

    /**
     * 任意标签中 属性，替换 可能因为别名，或者其他，需要替换成 米饭需要的属性 比如 data-src 替换成 src
     * 仅仅适用于标签中的属性  （标签属性名称 属性替换）
     */
    private Map<String, Map<String, String>> tagsAttrNameReplace;

    /**
     * 给任意标签中添加任意的属性，并且 加上值
     */
    private Map<String, Map<String, String>> addTagsAttr;


    /**
     * 图片class替换
     */
    private String imgClass;  //要替换的

    private String replaceImgClass;  //被替换的或者需要需要单独添加的
    /**
     * 图片中没有src,只有一个类似的属性，这个时候需要把这个属性转成src
     */
    private String imgAttrReplace;


    public static FieldBuilder builder() {
        return new FieldBuilder();
    }

    public Field() {
    }

    public Field(String name) {
        this.name = name;
    }

    public Field(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    public Field(FieldBuilder builder) {
        this.name = builder.name;
        this.selector = builder.selector;
        this.index = builder.index;
        this.type = builder.type;
        this.attr = builder.attr;
        this.attrWhenEmpty = builder.attrWhenEmpty;
        this.dateFormat = builder.dateFormat;
        this.locale = builder.locale;
        this.regex = builder.regex;
        this.regexBoolean = builder.regexBoolean;
        this.replacement = builder.replacement;
        this.coefficient = builder.coefficient;
        this.delimiter = builder.delimiter;
        this.clone = builder.clone;
        this.tag = builder.tag;
        this.tagAttr = builder.tagAttr;
        this.model = builder.model;
        this.script = builder.script;
        this.fields = builder.fields;
        this.params = builder.params;
        this.alternate = builder.alternate;
        this.fixedField = builder.fixedField;
        this.stridePageMerge = builder.stridePageMerge;
        this.tagsName = builder.tagsName;
        this.tagsAtrrName = builder.tagsAtrrName;
        this.imgClass = builder.imgClass;
        this.replaceImgClass = builder.replaceImgClass;
        this.imgAttrReplace = builder.imgAttrReplace;
        this.tagsAttrNameReplace = builder.tagsAttrNameReplace;
        this.addTagsAttr = builder.addTagsAttr;

    }

    // get and set

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Boolean getRegexBoolean() {
        return regexBoolean;
    }

    public void setRegexBoolean(Boolean regexBoolean) {
        this.regexBoolean = regexBoolean;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagAttr() {
        return tagAttr;
    }

    public void setTagAttr(String tagAttr) {
        this.tagAttr = tagAttr;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getParams() {
        return params;
    }

    public void setParams(List<Field> params) {
        this.params = params;
    }

    public Field getAlternate() {
        return alternate;
    }

    public void setAlternate(Field alternate) {
        this.alternate = alternate;
    }

    public String getFixedField() {
        return fixedField;
    }

    public void setFixedField(String fixedField) {
        this.fixedField = fixedField;
    }

    public boolean getStridePageMerge() {
        return stridePageMerge;
    }

    public void setStridePageMerge(boolean stridePageMerge) {
        this.stridePageMerge = stridePageMerge;
    }

    public Map<TagsRemoveOrAdd, Set<String>> getTagsName() {
        return tagsName;
    }

    public void setTagsName(Map<TagsRemoveOrAdd, Set<String>> tagsName) {
        this.tagsName = tagsName;
    }

    public Map<TagsRemoveOrAdd, Map<String, Set<String>>> getTagsAtrrName() {
        return tagsAtrrName;
    }

    public void setTagsAtrrName(Map<TagsRemoveOrAdd, Map<String, Set<String>>> tagsAtrrName) {
        this.tagsAtrrName = tagsAtrrName;
    }

    public String getImgClass() {
        return imgClass;
    }

    public void setImgClass(String imgClass) {
        this.imgClass = imgClass;
    }

    public String getReplaceImgClass() {
        return replaceImgClass;
    }

    public void setReplaceImgClass(String replaceImgClass) {
        this.replaceImgClass = replaceImgClass;
    }

    public Map<String, Map<String, String>> getTagsAttrNameReplace() {
        return tagsAttrNameReplace;
    }

    public void setTagsAttrNameReplace(Map<String, Map<String, String>> tagsAttrNameReplace) {
        this.tagsAttrNameReplace = tagsAttrNameReplace;
    }

    public Map<String, Map<String, String>> getAddTagsAttr() {
        return addTagsAttr;
    }

    public void setAddTagsAttr(Map<String, Map<String, String>> addTagsAttr) {
        this.addTagsAttr = addTagsAttr;
    }

    public boolean isStridePageMerge() {
        return stridePageMerge;
    }

    public String getImgAttrReplace() {
        return imgAttrReplace;
    }

    public void setImgAttrReplace(String imgAttrReplace) {
        this.imgAttrReplace = imgAttrReplace;
    }

    public String getAttrWhenEmpty() {
        return attrWhenEmpty;
    }

    public void setAttrWhenEmpty(String attrWhenEmpty) {
        this.attrWhenEmpty = attrWhenEmpty;
    }

    /**
     * <p>check and iteration</p>
     *
     * @param element  element
     * @param fields   fields
     * @param consumer consumer
     */
    public static void iterate(Element element, List<Field> fields, BiConsumer<Elements, Field> consumer) {
        for (Field field : fields) {
            Alternate alternate = iterate(element, field);
            if (alternate == null)
                continue;

//            String selector = field.getSelector();
//            if (selector == null)
//                continue;
//
//            Elements elements = element.select(selector);
//            if (elements.isEmpty())
//                continue;
//
//            if (field.getIndex() >= 0 && (elements.size() <= field.getIndex()))
//                continue;

            consumer.accept(alternate.getElements(), alternate.getField());
        }
    }

    private static Alternate iterate(Element element, Field field) {
        String selector = field.getSelector();
        if (selector == null)
            return null;

        Elements elements = element.select(selector);
        if (elements.isEmpty()) {
            if (field.getAlternate() != null)
                return iterate(element, field.getAlternate());
            else
                return null;
        }

        if (field.getIndex() >= 0 && (elements.size() <= field.getIndex()))
            return null;

        return new Alternate(field, elements);
    }

    private static class Alternate {
        private Field field;
        private Elements elements;

        public Alternate(Field field, Elements elements) {
            this.field = field;
            this.elements = elements;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Elements getElements() {
            return elements;
        }

        public void setElements(Elements elements) {
            this.elements = elements;
        }
    }

    public enum FieldType {

        /**
         * OBJECT类型
         */
        OBJECT(SubType.DATA, SubType.MEDIA) {
            @Override
            public Object get(Element parent, Elements elements, Field field) {
                List<Field> fields = field.getFields();
                if (fields == null)
                    return null;

                List<Map<String, Object>> list = new ArrayList<>();
                for (Element element : elements) {
                    Map<String, Object> map = new HashMap<>();
                    iterate(element, fields, (select, f) -> {
                        String name = f.getName();
                        Object value = f.getType().get(element, select, f);
                        map.put(name, value);
                    });
                    list.add(map);
                }
                return list;
            }

            @Override
            public Elements elements(Elements elements, Field field) {
                return HTML.elements(elements, field);
            }
        },

        /**
         * 日期类型
         */
        DATE(SubType.DATA) {
            @Override
            public Date get(Element parent, Elements elements, Field field) {
                if (field.getDateFormat() == null)
                    //return null;
                    return RandomDateUtils.getRandomDateTime();

                try {
                    String value = value(parent, elements, field);
                    SimpleDateFormat sdf = new SimpleDateFormat(field.getDateFormat(), field.getLocale());
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = sdf1.format(sdf.parse(value));
                    if (dateString.contains("00:00:00")) {
                        dateString = dateString.replace("00:00:00", RandomDateUtils.getHMS());
                    }
                    Date date = sdf1.parse(dateString);
                    //爬取的时间 小于当前时间
                    if (date.getTime() < new Date().getTime()) {
                        return date;
                    } else {
                        //爬取的时间大于当前时间，返回当前时间
                        return new Date();
                    }
                } catch (ParseException e) {
                    return null;
                }
            }
        },

        /**
         * 货币类型
         */
        DECIMAL(SubType.DATA) {
            @Override
            public BigDecimal get(Element parent, Elements elements, Field field) {
                try {
                    String value = value(parent, elements, field);
                    BigDecimal result = new BigDecimal(value);
                    if (field.getCoefficient() != null)
                        return result.multiply(new BigDecimal(field.getCoefficient()));
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
        },

        DOUBLE(SubType.DATA) {
            @Override
            public Double get(Element parent, Elements elements, Field field) {
                try {
                    String value = value(parent, elements, field);
                    double result = Double.parseDouble(value);
                    NumberFormat format = NumberFormat.getNumberInstance();
                    format.setMaximumFractionDigits(2);
                    if (field.getCoefficient() != null)
                        result *= Double.parseDouble(field.getCoefficient());
                    return Double.valueOf(format.format(result));
                } catch (Exception e) {
                    return null;
                }
            }
        },

        INTEGER(SubType.DATA) {
            @Override
            public Integer get(Element parent, Elements elements, Field field) {
                try {
                    String value = value(parent, elements, field);
                    int result = Integer.parseInt(value);
                    if (field.getCoefficient() != null)
                        return result * Integer.parseInt(field.getCoefficient());
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
        },

        LONG(SubType.DATA) {
            @Override
            public Long get(Element parent, Elements elements, Field field) {
                try {
                    String value = value(parent, elements, field);
                    long result = Long.parseLong(value);
                    if (field.getCoefficient() != null)
                        return result * Long.parseLong(field.getCoefficient());
                    return result;
                } catch (Exception e) {
                    return null;
                }
            }
        },

        /**
         * 文本类型
         */
        TEXT(SubType.DATA) {
            @Override
            public Object get(Element parent, Elements elements, Field field) {
                return invoke(parent, field, text(elements, field));
            }
        },

        /**
         * 固定字段
         */
        FIXED_FIELD(SubType.DATA) {
            @Override
            public Object get(Element parent, Elements elements, Field field) {
                return field.getFixedField();
            }
        },


        /**
         * HTML文档类型, 同时属于两种类型, 富文本内容中会引用多媒体类型的内容
         */
        HTML(SubType.DATA, SubType.MEDIA) {

            /**
             * img标签的css query
             */
            private static final String SELECTOR_IMAGE = "img[src],a[href~=(?i)\\.(gif|png|jpe?g)]";

            @Override
            public Object get(Element parent, Elements elements, Field field) {
                int index = field.getIndex();
                String value;
                if (index < 0) {
                    value = elements.outerHtml();
                } else {
                    value = elements.get(index).html();
                }
                Whitelist whitelist = Whitelist.relaxed().addAttributes("iframe", "frameborder", "height", "width", "src", "allowfullscreen");
                if (field.getTagsName() != null && field.getTagsName().size() > 0) {
                    if (field.getTagsName().get(TagsRemoveOrAdd.ADD) != null) {
                        Object[] objects = field.getTagsName().get(TagsRemoveOrAdd.ADD).toArray();
                        whitelist = whitelist.addTags(objectToString(objects));
                    }
                    if (field.getTagsName().get(TagsRemoveOrAdd.REMOVE) != null) {
                        Object[] objects = field.getTagsName().get(TagsRemoveOrAdd.REMOVE).toArray();
                        whitelist = whitelist.removeTags(objectToString(objects));
                    }
                }
                if (field.getTagsAtrrName() != null && field.getTagsAtrrName().size() > 0) {
                    if (field.getTagsAtrrName().get(TagsRemoveOrAdd.ADD) != null) {
                        Map<String, Set<String>> stringSetMap = field.getTagsAtrrName().get(TagsRemoveOrAdd.ADD);
                        for (String tag : stringSetMap.keySet()) {
                            Object[] objects = stringSetMap.get(tag).toArray();
                            whitelist = whitelist.addAttributes(tag, objectToString(objects));
                        }
                    }
                    if (field.getTagsAtrrName().get(TagsRemoveOrAdd.REMOVE) != null) {
                        Map<String, Set<String>> stringSetMap = field.getTagsAtrrName().get(TagsRemoveOrAdd.REMOVE);
                        for (String tag : stringSetMap.keySet()) {
                            Object[] objects = stringSetMap.get(tag).toArray();
                            whitelist = whitelist.removeAttributes(tag, objectToString(objects));
                        }
                    }
                }
                //处理标签中要改变的属性 的名字
                if (field.getTagsAttrNameReplace() != null && field.getTagsAttrNameReplace().size() > 0) {
                    Document document = Jsoup.parse(value);
                    Map<String, Map<String, String>> stringMapMap = field.getTagsAttrNameReplace();
                    for (String tag : stringMapMap.keySet()) {
                        String select_tag;
                        //不含有_
                        if (!tag.contains("_")) {
                            select_tag = tag;
                            //含有_
                        } else {
                            String[] array = tag.split("_");
                            select_tag = array[0];
                        }
                        //Elements elements1 = select(elements, -1, select_tag);
                        Elements elements1 = document.select(select_tag);
                        if (elements1.size() != 0) {
                            for (Element element : elements1) {

                                for (String attr : stringMapMap.get(tag).keySet()) {
                                    element.select(select_tag + "[" + attr + "]").attr(stringMapMap.get(tag).get(attr),
                                            element.select(select_tag + "[" + attr + "]").attr(attr));
                                }
                            }
                        }
                    }
                    value = document.toString();
                }

                //给标签加入相应的属性，如果属性存在 就覆盖掉
                if (field.getAddTagsAttr() != null && field.getAddTagsAttr().size() > 0) {
                    Document document = Jsoup.parse(value);
                    Map<String, Map<String, String>> stringMapMap = field.getAddTagsAttr();
                    for (String tag : stringMapMap.keySet()) {
                        String select_tag;
                        //不含有_
                        if (!tag.contains("_")) {
                            select_tag = tag;
                            //含有_
                        } else {
                            String[] array = tag.split("_");
                            select_tag = array[0];
                        }
                        //Elements elements1 = select(elements, -1, select_tag);
                        Elements elements1 = document.select(select_tag);
                        if (elements1.size() != 0) {
                            for (Element element : elements1) {
                                for (String attr : stringMapMap.get(tag).keySet()) {
                                    element.select(select_tag).removeAttr(attr).attr(attr,stringMapMap.get(tag).get(attr));
                                }
                            }
                        }
                    }
                    value = document.toString();
                }




                String result = Jsoup.clean(replaceAll(value, field), whitelist);
                return invoke(parent, field, result);
            }

            private String[] objectToString(Object[] objects) {
                String[] strings = new String[objects.length];
                for (int i = 0; i < objects.length; i++) {
                    strings[i] = objects[i].toString();
                }
                return strings;
            }


            /**
             * <p>富文本中引用的图片</p>
             * @param elements elements
             * @param field    field
             * @return elements
             */
            @Override
            public Elements elements(Elements elements, Field field) {
                Elements elements1 = select(elements, field.getIndex(), SELECTOR_IMAGE);
                //如果img中没有src,只有一个类似的 属性，这个时候，需要把这个类似的属性变成 src
                if (elements1.size() == 0 && field.imgAttrReplace != null) {
                    elements1 = select(elements, field.getIndex(), "img[" + field.imgAttrReplace + "]");
                    Elements tmp = new Elements();
                    for (Element element : elements1) {
                        element.select("img[" + field.imgAttrReplace + "]").removeAttr("src").attr("src", element.select("img[" + field.imgAttrReplace + "]").attr(field.imgAttrReplace));
                        tmp.add(element);
                    }
                    elements1 = tmp;
                }
                if (field.imgClass == null && field.replaceImgClass == null) {
                    return elements1;
                } //输入的多个class 需要在一个字符串中处理 ，中间使用单个空格 空开
                else if (field.imgClass != null && field.replaceImgClass != null) {
                    Elements elements2 = new Elements();
                    field.imgClass = field.imgClass.trim().replaceAll(" ", ".");
                    String[] strings = field.imgClass.split("\\.");
                    for (Element element : elements1) {
                        element.select("." + field.imgClass).addClass(field.replaceImgClass);
                        if (strings.length == 0) {
                            element.select("." + field.replaceImgClass).removeClass(field.imgClass);
                        } else {
                            for (String str : strings) {
                                element.select("." + str).removeClass(str);
                            }
                        }
                        elements2.add(element);
                    }
                    return elements2;
                } else if (field.imgClass == null && field.replaceImgClass != null) {
                    Elements elements2 = new Elements();
                    for (Element element : elements1) {
                        element.addClass(field.replaceImgClass);
                        elements2.add(element);
                    }
                    return elements2;
                }
                return elements1;
            }

            /**
             * <p>find elements by css query</p>
             *
             * @param elements elements
             * @param index    index
             * @param query    query
             * @return elements
             */
            private Elements select(Elements elements, int index, String query) {
                if (index < 0)
                    return elements.select(query);
                else
                    return elements.get(index).select(query);
            }
        },

        /**
         * 标签属性类型
         */
        ATTRIBUTE(SubType.DATA) {
            @Override
            public Object get(Element parent, Elements elements, Field field) {
                int index = field.getIndex();
                String value;
                if (index < 0) {
                    value = elements.attr(field.getAttr());
                } else {
                    value = elements.get(index).attr(field.getAttr());
                }
                if (field.getAttrWhenEmpty() != null && (value == null || value == "")) {
                    if (index < 0) {
                        value = elements.attr(field.getAttrWhenEmpty());
                    } else {
                        value = elements.get(index).attr(field.getAttrWhenEmpty());
                    }
                }

                String result = replaceAll(value, field);
                return invoke(parent, field, result);
            }
        },

        /**
         * 原始元素类型
         */
        ELEMENT(SubType.MEDIA) {
            /**
             * <p>图片, 目前只处理2级, 而不是递归处理</p>
             * <p>对图片进行分组, e.g: 相同图片的不同尺寸</p>
             *
             * @param elements 通过第一级Field的css query获得的elements
             * @param field root field
             * @return 分组后的图片集合, 为了利用Elements的数据结构, 采用树形结构进行分组. 第一个元素作为 primary element
             */
            @Override
            public Elements elements(Elements elements, Field field) {
                List<Field> fields = field.getFields();
                if (fields == null)
                    return elements;

                Elements all = new Elements();
                for (Element element : elements) {
                    // image group, use the first element as the root element
                    Elements root = new Elements();
                    AtomicInteger counter = new AtomicInteger(0);
                    iterate(element, fields, (select, f) -> {
                        if (f.getTag() != null && f.getTagAttr() != null) {
                            Object result = f.getType().get(element, select, f);
                            if (result instanceof String)
                                add(root, toElement(element, counter, f, (String) result));
                            else if (result instanceof ScriptObjectMirror)
                                ((ScriptObjectMirror) result).forEach((s, v) -> add(root, toElement(element, counter, f, v.toString())));
                        }
                    });

                    if (!root.isEmpty())
                        all.add(root.get(0));
                }
                return all;
            }

            /**
             * <p>build img element</p>
             * @param element element
             * @param counter counter
             * @param field field
             * @param result result
             * @return Element
             */
            private Element toElement(Element element, AtomicInteger counter, Field field, String result) {
                Attributes attributes = new Attributes();
                attributes.put(field.getTagAttr(), result);
                attributes.put(DATA_MODEL, field.getModel().getName());
                attributes.put(DATA_ORDER, String.valueOf(counter.incrementAndGet()));
                return new Element(Tag.valueOf(field.getTag()), element.baseUri(), attributes);
            }

            /**
             * <p>add to root of tree</p>
             * @param root root
             * @param e element
             */
            private void add(Elements root, Element e) {
                if (root.isEmpty())
                    root.add(e);
                else
                    root.get(0).appendChild(e);
            }

        };

        /**
         * <p>分隔符, 默认是<strong>,</strong></p>
         */
        private static final String DELIMITER = ",";

        /**
         * <p>sub types</p>
         */
        private final Set<SubType> set = EnumSet.noneOf(SubType.class);

        FieldType(SubType... types) {
            set.addAll(Arrays.asList(types));
        }

        /**
         * <p>判断field type是否所属某一种子类型</p>
         *
         * @param type type
         * @return true if contained
         */
        public boolean contains(SubType type) {
            return set.contains(type);
        }

        /**
         * <p>获取elements, 通常为多媒体元素 e.g: img</p>
         *
         * @param elements elements
         * @param field    field
         * @return elements
         */
        public Elements elements(Elements elements, Field field) {
            throw new UnsupportedOperationException();
        }

        /**
         * <p>获取数据</p>
         *
         * @param parent   parent
         * @param elements elements
         * @param field    field
         * @return object
         */
        public Object get(Element parent, Elements elements, Field field) {
            throw new UnsupportedOperationException();
        }

        /**
         * @param element current iteration element, field and field's params should use same element.
         * @param field   field
         * @param param   param
         * @return result value
         */
        protected Object invoke(Element element, Field field, String param) {
            if (field.getScript() != null) {
                List<String> list = new ArrayList<>();
                list.add(param);

                if (!CollectionUtils.isEmpty(field.getParams()))
                    params(list, element, field.getParams());

                return ScriptUtils.invokeDefault(field.getScript(), list.toArray());
            } else {
                return param;
            }
        }

        protected void params(List<String> list, Element element, List<Field> params) {
            iterate(element, params, (elements, field) -> {
                String result = (String) field.getType().get(element, elements, field);
                if (result != null) {
                    list.add(result);
                }
            });
        }

        /**
         * <p>VALUE</p>
         *
         * @param parent   parent
         * @param elements elements
         * @param field    field
         * @return text or attribute value
         */
        protected String value(Element parent, Elements elements, Field field) {
            return field.getAttr() == null ? text(elements, field) : (String) ATTRIBUTE.get(parent, elements, field);
        }

        /**
         * <p>TEXT</p>
         *
         * @param elements elements
         * @param field    field
         * @return text
         */
        protected String text(Elements elements, Field field) {
            int index = field.getIndex();
            String value = null;
            if (index < 0) {
                String delimiter = field.getDelimiter() == null ? FieldType.DELIMITER : field.getDelimiter();
                Optional<StringBuilder> optional = elements
                        .stream()
                        .map(element -> new StringBuilder(element.text()))
                        .reduce((left, right) -> left.append(delimiter).append(right));
                if (optional.isPresent())
                    value = optional.get().toString();
            } else {
                value = elements.get(index).text();
            }
            return replaceAll(value, field);
        }

        /**
         * <p>正则替换</p>
         *
         * @param value value
         * @param field field
         * @return string
         */
        protected String replaceAll(String value, Field field) {
            if (field.getRegex() == null)
                return value;
            if (field.getRegexBoolean() == null || field.getRegexBoolean()) {
                return value.replaceAll(field.getRegex(), field.getReplacement() == null ? "" : field.getReplacement()).trim();
            }
            Matcher matcher = Pattern.compile(field.getRegex()).matcher(value);
            if (matcher.find()) {
                return matcher.group();
            }
            return value;
        }

        /**
         * <p>子类型</p>
         */
        public enum SubType {

            /**
             * 普通数据类型
             */
            DATA,

            /**
             * 多媒体标记类型, 如附件、视频、音频等, 需要进行特殊处理的数据
             */
            MEDIA

        }

    }

    public static class FieldBuilder implements Builder<Field> {

        private String name;
        private String selector;
        private int index;
        private FieldType type;
        private String attr;
        private String attrWhenEmpty;
        private String dateFormat;
        private Locale locale;
        private String regex;
        private Boolean regexBoolean;
        private String replacement;
        private String coefficient;
        private String delimiter;
        private boolean clone = true;
        private String tag;
        private String tagAttr;
        private Model model;
        private String script;
        private List<Field> fields;
        private List<Field> params;
        private Field alternate;
        private String fixedField;
        private boolean stridePageMerge = false;
        private Map<TagsRemoveOrAdd, Set<String>> tagsName = new HashMap<>();
        private Map<TagsRemoveOrAdd, Map<String, Set<String>>> tagsAtrrName = new HashMap<>();
        private String imgClass;
        private String replaceImgClass;
        private String imgAttrReplace;
        private Map<String, Map<String, String>> tagsAttrNameReplace = new HashMap<>();
        private Map<String, Map<String, String>> addTagsAttr = new HashMap<>();


        public FieldBuilder link(String selector) {
            return link(selector, "href");
        }

        public FieldBuilder link(String selector, String attr) {
            link(FieldType.ATTRIBUTE, selector, attr);
            return this;
        }

        public FieldBuilder link(FieldType type, String selector) {
            link(type, selector, null);
            return this;
        }

        public FieldBuilder link(FieldType type, String selector, String attr) {
            this.type = type;
            this.name = LINK;
            this.selector = selector;
            this.attr = attr;
            return this;
        }

        public FieldBuilder selector(String name, String selector) {
            this.name = name;
            this.selector = selector;
            return this;
        }

        public FieldBuilder selector(String name, String selector, boolean stridePageMerge) {
            this.name = name;
            this.selector = selector;
            this.stridePageMerge = stridePageMerge;
            return this;
        }


        public FieldBuilder selector(String name, String selector, int index) {
            this.name = name;
            this.selector = selector;
            this.index = index;
            return this;
        }

        public FieldBuilder selector(String name, String selector, int index, boolean stridePageMerge) {
            this.name = name;
            this.selector = selector;
            this.index = index;
            this.stridePageMerge = stridePageMerge;
            return this;
        }


        public FieldBuilder index(int index) {
            this.index = index;
            return this;
        }

        public FieldBuilder type(FieldType type) {
            this.type = type;
            return this;
        }

        public FieldBuilder asObject() {
            this.type = FieldType.OBJECT;
            return this;
        }

        public FieldBuilder asDate(String dateFormat) {
            this.type = FieldType.DATE;
            this.dateFormat = dateFormat;
            this.locale = Locale.CHINESE;
            return this;
        }

        public FieldBuilder asDate(String dateFormat, String attr) {
            this.type = FieldType.DATE;
            this.dateFormat = dateFormat;
            this.attr = attr;
            this.locale = Locale.CHINESE;
            return this;
        }

        public FieldBuilder asDate(String dateFormat, Locale locale) {
            this.type = FieldType.DATE;
            this.dateFormat = dateFormat;
            this.locale = locale;
            return this;
        }

        public FieldBuilder asDate(String dateFormat, String attr, Locale locale) {
            this.type = FieldType.DATE;
            this.dateFormat = dateFormat;
            this.attr = attr;
            this.locale = locale;
            return this;
        }

        public FieldBuilder asDecimal() {
            this.type = FieldType.DECIMAL;
            return this;
        }

        public FieldBuilder asDecimal(String coefficient) {
            this.type = FieldType.DECIMAL;
            this.coefficient = coefficient;
            return this;
        }

        public FieldBuilder asDecimal(String coefficient, String attr) {
            this.type = FieldType.DECIMAL;
            this.coefficient = coefficient;
            this.attr = attr;
            return this;
        }

        public FieldBuilder asDouble() {
            this.type = FieldType.DOUBLE;
            return this;
        }

        public FieldBuilder asDouble(String coefficient) {
            this.type = FieldType.DOUBLE;
            this.coefficient = coefficient;
            return this;
        }

        public FieldBuilder asDouble(String coefficient, String attr) {
            this.type = FieldType.DOUBLE;
            this.coefficient = coefficient;
            this.attr = attr;
            return this;
        }

        public FieldBuilder asInteger() {
            this.type = FieldType.INTEGER;
            return this;
        }

        public FieldBuilder asInteger(String coefficient) {
            this.type = FieldType.INTEGER;
            this.coefficient = coefficient;
            return this;
        }

        public FieldBuilder asInteger(String coefficient, String attr) {
            this.type = FieldType.INTEGER;
            this.coefficient = coefficient;
            this.attr = attr;
            return this;
        }

        public FieldBuilder asLong() {
            this.type = FieldType.LONG;
            return this;
        }

        public FieldBuilder asLong(String coefficient) {
            this.type = FieldType.LONG;
            this.coefficient = coefficient;
            return this;
        }

        public FieldBuilder asLong(String coefficient, String attr) {
            this.type = FieldType.LONG;
            this.coefficient = coefficient;
            this.attr = attr;
            return this;
        }

        public FieldBuilder asText() {
            this.type = FieldType.TEXT;
            return this;
        }

        public FieldBuilder asHtml() {
            this.type = FieldType.HTML;
            return this;
        }

        public FieldBuilder asAttribute(String attr) {
            this.type = FieldType.ATTRIBUTE;
            this.attr = attr;
            return this;
        }

        public FieldBuilder asAttributeWhenEmpty(String attr) {
            this.type = FieldType.ATTRIBUTE;
            this.attrWhenEmpty = attr;
            return this;
        }

        public FieldBuilder asElement() {
            this.type = FieldType.ELEMENT;
            return this;
        }

        public FieldBuilder asFixedField(String name, String fixedField) {
            this.type = FieldType.FIXED_FIELD;
            this.name = name;
            this.fixedField = fixedField;
            return this;
        }

        public FieldBuilder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public FieldBuilder regex(String regex, boolean regexBoolean) {
            this.regex = regex;
            this.regexBoolean = regexBoolean;
            return this;
        }

        public FieldBuilder regex(String regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
            return this;
        }

        public FieldBuilder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public FieldBuilder tag(String tag, String tagAttr) {
            this.tag = tag;
            this.tagAttr = tagAttr;
            return this;
        }

        public FieldBuilder model(Model model) {
            this.model = model;
            return this;
        }

        public FieldBuilder script(String script) {
            this.script = script;
            return this;
        }

        public FieldBuilder fields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public FieldBuilder addField(Field... fields) {
            if (this.fields == null)
                this.fields = new ArrayList<>();
            Collections.addAll(this.fields, fields);
            return this;
        }


        public FieldBuilder params(List<Field> params) {
            this.params = params;
            return this;
        }

        public FieldBuilder addParams(Field... params) {
            if (this.params == null)
                this.params = new ArrayList<>();
            Collections.addAll(this.params, params);
            return this;
        }

        public FieldBuilder whenEmpty(Field field) {
            this.alternate = field;
            return this;
        }


        public FieldBuilder tagsName(TagsRemoveOrAdd tagsRemoveOrAdd, String... tags) {
            Validate.notNull(tags);
            String[] var2 = tags;
            int var3 = tags.length;
            if (this.tagsName.size() == 0) {
                Set<String> tags001 = new HashSet<>();
                for (int var4 = 0; var4 < var3; ++var4) {
                    tags001.add(var2[var4]);
                }
                this.tagsName.put(tagsRemoveOrAdd, tags001);
            } else {
                if (this.tagsName.get(tagsRemoveOrAdd) == null) {
                    Set<String> tags001 = new HashSet<>();
                    for (int var4 = 0; var4 < var3; ++var4) {
                        tags001.add(var2[var4]);
                    }
                    this.tagsName.put(tagsRemoveOrAdd, tags001);
                } else {
                    for (int var4 = 0; var4 < var3; ++var4) {
                        this.tagsName.get(tagsRemoveOrAdd).add(var2[var4]);
                    }
                }
            }
            return this;
        }

        public FieldBuilder tagsAtrrName(TagsRemoveOrAdd tagsRemoveOrAdd, String tag, String... Atrrs) {
            Validate.notNull(tag);
            Validate.notNull(Atrrs);
            String[] var2 = Atrrs;
            int var3 = Atrrs.length;
            if (this.tagsAtrrName.size() == 0) {
                Set<String> atrr = new HashSet<>();
                for (int var4 = 0; var4 < var3; ++var4) {
                    atrr.add(var2[var4]);
                }
                Map<String, Set<String>> tags = new HashMap<>();
                tags.put(tag, atrr);
                this.tagsAtrrName.put(tagsRemoveOrAdd, tags);
            } else {
                if (this.tagsAtrrName.get(tagsRemoveOrAdd) == null) {
                    Set<String> atrr = new HashSet<>();
                    for (int var4 = 0; var4 < var3; ++var4) {
                        atrr.add(var2[var4]);
                    }
                    Map<String, Set<String>> tags = new HashMap<>();
                    tags.put(tag, atrr);
                    this.tagsAtrrName.put(tagsRemoveOrAdd, tags);
                } else {
                    Set<String> atrr = new HashSet<>();
                    for (int var4 = 0; var4 < var3; ++var4) {
                        atrr.add(var2[var4]);
                    }
                    Map<String, Set<String>> tags = new HashMap<>();
                    tags.put(tag, atrr);
                    this.tagsAtrrName.get(tagsRemoveOrAdd).putAll(tags);
                }
            }

            return this;
        }

        public FieldBuilder tagsAttrNameReplace(String tags, String attr, String replaceAttr) {
            Validate.notNull(tags);
            Validate.notNull(attr);
            Validate.notNull(replaceAttr);
            Map<String, String> map = new HashMap<>();
            map.put(attr, replaceAttr);
            if (tagsAttrNameReplace != null && tagsAttrNameReplace.get(tags) != null) {
                tagsAttrNameReplace.put(tags + "_" + tagsAttrNameReplace.size(), map);
            } else {
                tagsAttrNameReplace.put(tags, map);
            }
            return this;
        }


        public FieldBuilder addTagsAttr(String tags, String attr, String attrValue) {
            Validate.notNull(tags);
            Validate.notNull(attr);
            Validate.notNull(attrValue);
            Map<String, String> map = new HashMap<>();
            map.put(attr, attrValue);
            if (addTagsAttr!= null && addTagsAttr.get(tags) != null) {
                addTagsAttr.put(tags + "_" + addTagsAttr.size(), map);
            } else {
                addTagsAttr.put(tags, map);
            }
            return this;
        }


        public FieldBuilder imgAttrReplace(String imgAttrReplace) {
            this.imgAttrReplace = imgAttrReplace;
            return this;
        }

        public FieldBuilder imgClass(String imgClass, String replaceImgClass) {
            this.imgClass = imgClass;
            this.replaceImgClass = replaceImgClass;
            return this;
        }

        @Override
        public Field build() {
            return new Field(this);
        }
    }


}
