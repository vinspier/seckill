package com.vinspier.seckill.handler;

import com.github.pagehelper.PageInfo;
import com.vinspier.seckill.enums.ResultCode;

import java.io.Serializable;

/**
 * @ClassName: ResponseSuccessTemplate
 * @Description: 自定义全局的成功返回异常
 *
 * 使用builder模式 代替 历史多个构造器方式
 *
 * @Author:
 * @Date: 2020/3/19 10:22
 * @Version V1.0
 **/
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = -3189671189061723393L;

    /**
     * 操作成功
     */
    private static final String SUCCESS = "success";

    /**
     * 操作失败
     */
    private static final String ERROR = "error";

    private static boolean TRUE = true;

    private static boolean FALSE = true;

    private int code;

    private String msg;

    private T data;

    /** 当前页 */
    private int current;

    /** 每页大小*/
    private int pageSize;

    /** 总页数*/
    private int totalPage;

    /** 总条数*/
    private long totalCount;

    /** 是否还有下一页*/
    private boolean hasNext;

    /**
     * 构造方法私有化 强制外部使用builder生成对象
     */
    private BaseResult(Builder<T> builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
        this.totalCount = builder.totalCount;
        this.totalPage = builder.totalPage;
        this.pageSize = builder.pageSize;
        this.current = builder.current;
        this.hasNext = builder.hasNext;
        this.data = builder.data;
    }

    public static BaseResult success(){
        return new Builder(200,SUCCESS).build();
    }

    public static BaseResult success(String msg){
        return new Builder(200,msg).build();
    }

    public static BaseResult success(Object data){
        return new Builder(200,SUCCESS).date(data).build();
    }

    public static BaseResult success(PageInfo pageInfo){
        return new Builder(200,SUCCESS).withPageInfo(pageInfo).build();
    }

    public static BaseResult error(){
        return new Builder(500,ERROR).build();
    }

    public static BaseResult error(String msg){
        return new Builder(500,msg).build();
    }

    /**
     * 自定义错误代码
     */
    public static BaseResult error(ResultCode resultCode){
        return new Builder(resultCode.getCode(),resultCode.getMsg()).build();
    }

    /**
     * builder 静态内部类
     */
    public static class Builder<T>{

        private int code;

        private String msg;

        private T data;

        /** 当前页 */
        private int current;

        /** 每页大小*/
        private int pageSize;

        /** 总页数*/
        private int totalPage;

        /** 总条数*/
        private long totalCount;

        /** 是否还有下一页*/
        private boolean hasNext;

        /**
         * 必要的参数放在构造器里 其他的作为动态可加参数
         * @param code
         * @param msg
         */
        private Builder(int code,String msg) {
            this.code = code;
            this.msg = msg;
        }

        /**
         * 向外界提供一个默认的builder建造对象
         */
        public static Builder builder(){
            return new Builder(200,SUCCESS);
        }

        /**
         * 提供给外界的创建方法
         */
        public BaseResult build(){
            return new BaseResult(this);
        }

        public Builder code(int code){
            this.code = code;
            return this;
        }

        public Builder msg(String msg){
            this.msg = msg;
            return this;
        }

        public Builder date(T data){
            this.data = data;
            return this;
        }

        public Builder currentPage(int current){
            this.current = current;
            return this;
        }
        public Builder pageSize(int pageSize){
            this.pageSize = pageSize;
            return this;
        }
        public Builder totalPage(int totalPage){
            this.totalPage = totalPage;
            return this;
        }
        public Builder totalCount(int totalCount){
            this.totalCount = totalCount;
            return this;
        }
        public Builder hasNext(boolean hasNext){
            this.hasNext = hasNext;
            return this;
        }

        public Builder withPageInfo(PageInfo pageInfo){
            this.totalCount = pageInfo.getTotal();
            this.totalPage = pageInfo.getSize();
            this.pageSize = pageInfo.getPageSize();
            this.current = pageInfo.getPageNum();
            this.hasNext = pageInfo.isHasNextPage();
            this.data = (T) pageInfo.getList();
            return this;
        }

    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", current=" + current +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalCount=" + totalCount +
                ", hasNext=" + hasNext +
                '}';
    }
}
