package com.cmall.service.Impl;

import com.cmall.common.Const;
import com.cmall.common.ResponseCode;
import com.cmall.common.ServerResponse;
import com.cmall.dao.CategoryMapper;
import com.cmall.dao.ProductMapper;
import com.cmall.pojo.Category;
import com.cmall.pojo.Product;
import com.cmall.service.ICategoryService;
import com.cmall.service.IProductService;
import com.cmall.util.DateTimeUtil;
import com.cmall.util.PropertiesUtil;
import com.cmall.vo.ProductDetailVo;
import com.cmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    //保存或更新产品
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImage())){
                String[] subImageArray = product.getSubImage().split(",");
                //从子图中把主图赋值上
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            if(product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }else{
                    return ServerResponse.createBySuccess("更新产品失败");
                }
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }else{
                    return ServerResponse.createBySuccess("新增产品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null||status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    //商品详情
    public ServerResponse<ProductDetailVo> manageDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        //vo对象，value object，承载对象各个值
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImage());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.cmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());

        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createtime updatetime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;

    }

    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //pageHelper使用方法：
        //startPage
        //填充自己的sql查询逻辑
        //pageHelper的收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName((product.getName()));
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.cmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        //关键字和分类id都没有
        if(StringUtils.isBlank(keyword)&&categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //当传分类时，传一个大的父分类就要调用之前的递归算法，把子分类和本身加入List中，
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null&&StringUtils.isBlank(keyword)){
                //没有该分类，且还没有关键字，返回一个空的结果集不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                //使分页生效
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //拿到分类以及子分类
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //排序处理
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        //搜索商品
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size() == 0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
