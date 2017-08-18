package com.cmall.service;

import com.cmall.common.ServerResponse;
import com.cmall.pojo.Product;
import com.cmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageDetail(Integer productId);

    ServerResponse getProductList(int pageNum,int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
