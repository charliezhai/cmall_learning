package com.cmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";//当前用户

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface ProductListOrderBy{
        //Set的contains()时间复杂度为O(1),List的contains()时间复杂度为O(n),所以选择用Set
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户

        int ROLE_ADMIN = 1;//管理员
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        private String value;
        private int code;

        ProductStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
