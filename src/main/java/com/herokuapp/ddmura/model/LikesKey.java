package com.herokuapp.ddspace.model;

public class LikesKey {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column likes.parent_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    private Integer parentId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column likes.type
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    private Integer type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column likes.user_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    private Integer userId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column likes.parent_id
     *
     * @return the value of likes.parent_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column likes.parent_id
     *
     * @param parentId the value for likes.parent_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column likes.type
     *
     * @return the value of likes.type
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column likes.type
     *
     * @param type the value for likes.type
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column likes.user_id
     *
     * @return the value of likes.user_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column likes.user_id
     *
     * @param userId the value for likes.user_id
     *
     * @mbg.generated Sat Feb 22 12:36:24 CST 2020
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}