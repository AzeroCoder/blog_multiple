package com.lh.blog.controller.back;

import com.lh.blog.bean.Category;
import com.lh.blog.enums.PathEnum;
import com.lh.blog.service.CategoryService;
import com.lh.blog.util.ImageUtil;
import com.lh.blog.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping(value = "/admin/categories")
    public Map<String,Object> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "7") int size) throws Exception
    {
        Map<String,Object> map = new HashMap<>();
        start = start < 0 ? 0 : start;
        PageUtil<Category> page =categoryService.list(start, size, 5);
        List<Category> categories = page.getContent();
        categoryService.fillChild(categories);
        map.put("page",page);
//        map.put("all",categoryService.list());
        return map;
    }
    @GetMapping(value = "/admin/categories/{id}")
    public Category get(@PathVariable("id")int id) throws Exception
    {
        Category category =  categoryService.get(id);
        return category;
    }

    @PostMapping(value = "/admin/categories")
    public void add(MultipartFile image, Category category, HttpServletRequest request) throws Exception
    {
        categoryService.add(category);
        if(image!=null)
        {
            int id = category.getId();
//            String path = request.getServletContext().getRealPath("static/image/category");
            ImageUtil.uploadImg(String.valueOf(id), 0, image, PathEnum.Category);
        }
    }

    @DeleteMapping(value = "/admin/categories/{id}")
    public String delete(@PathVariable("id") int id,HttpServletRequest request) throws Exception
    {
        categoryService.delete(id);
        ImageUtil.deleteImg(String.valueOf(id), 0, PathEnum.Category);
        return null;
    }


    @PutMapping(value = "/admin/categories/{id}")
    public void update(MultipartFile image, Category category,HttpServletRequest request) throws Exception
    {
        categoryService.update(category);
        if(image!=null)
        {
//            String path = request.getServletContext().getRealPath("static/image/category");
//            ImageUtil.uploadCate(category.getId(), path, image);
            ImageUtil.uploadImg(String.valueOf(category.getId()), category.getUid(), image, PathEnum.Category);
        }
    }


    @PostMapping(value = "/admin/categories/search")
    public List<Category> searchCategory(@RequestParam(value = "key") String key) throws Exception
    {
        List<Category> categories =  categoryService.listByKey(key);
        categoryService.fillParent(categories);
        return categories;
    }
}
