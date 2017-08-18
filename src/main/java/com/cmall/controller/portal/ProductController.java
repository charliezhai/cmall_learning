package com.cmall.controller.portal;

import com.cmall.common.ServerResponse;
import com.cmall.service.IProductService;
import com.cmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 前台商品详情
     * 与后台商品详情不同之处在于，在前台查看时要判断该产品是不是在线状态
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    /**
     * 前台商品搜索列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required = false)String keyword,
                                         @RequestParam(value="categoryId",required = false)Integer categoryId,
                                         @RequestParam(value="pageNum",defaultValue= "1")int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value="orderBy",defaultValue= "")String orderBy){//false是因为搜索的时候可以不传关键字
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
