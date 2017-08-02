package com.cmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//token就是令牌，拿着这个令牌就可以改密码，我们将令牌缓存到本地(guava cache)，
// 用户拿着token改密码，只要本地缓存的有这个token，就允许它改密码，
// 因为设置了失效时间，比如5分钟就失效，那本地的缓存中就移除了这个令牌，用户再想拿着这个token改密码就不行了。
public class TokenCache {

    public static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //本地缓存初始化最大值是10000，超过10000会采用LRU算法清除，有效期是12个小时
    private static LoadingCache<String,String> localcache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localcache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localcache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get error",e);
        }
        return null;

    }
}
