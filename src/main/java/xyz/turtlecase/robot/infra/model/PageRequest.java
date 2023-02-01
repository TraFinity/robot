package xyz.turtlecase.robot.infra.model;

/**
 * 页面请示分页基类
 *
 * @param <T>
 */
public class PageRequest<T> {
    private Integer pageNum = Integer.valueOf(1);
    private Integer pageSize = Integer.valueOf(20);
    private T condition;

    public Integer getPageNum() {

        return this.pageNum;
    }

    public void setPageNum(Integer pageNum) {

        if (pageNum == null || pageNum.intValue() == 0) {

            pageNum = Integer.valueOf(1);
        }
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {

        if (pageSize == null || pageSize.intValue() == 0) {
            pageSize = Integer.valueOf(20);
        }
        this.pageSize = pageSize;
    }

    public T getCondition() {
        return this.condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }
}
