package com.lh.blog.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.lh.blog.annotation.Check;
import com.lh.blog.bean.*;
import com.lh.blog.dao.ArticleDAO;
import com.lh.blog.util.PageUtil;
import com.lh.blog.util.RestPageImpl;
import com.lh.blog.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "articles")
public class ArticleService {
    @Autowired
    ArticleDAO dao;
    @Autowired
    CategoryService categoryService;
    @Autowired
    TagService tagService;
    @Autowired
    TagArticleService tagArticleService;
    @Autowired
    LikeService likeService;
    @Autowired
    StartService startService;
    @Autowired
    FocusService focusService;
    @Autowired
    UserService userService;

    Sort sort = new Sort(Sort.Direction.DESC, "id");

    @CacheEvict(allEntries = true)
    public void add(Article bean) {
        dao.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        dao.delete(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Article bean) {
        dao.save(bean);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public Article get(int id) {
        return dao.findOne(id);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> list() {
        return dao.findAll(sort);
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> listByComment() {
        return dao.findTop5ByOrderByCommentDesc();
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> listByKey(String key) {
        return dao.findAllByStatusAndTitleLike(0, "%"+key+"%", sort);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> listByIds(List<Integer> ids) {
        return dao.findAllByIdIn(ids);
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByCategory(int cid, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        List<Integer> cids = new ArrayList<>();
        List<Category> categories = categoryService.listByParent(cid);
        for (Category category : categories) {
            cids.add(category.getId());
        }
        page = dao.findAllByStatusAndTypeAndCidIn(0, ArticleDAO.TYPE_PUBLISH, cids, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByStatusAndUserAndCategory(int start, int size, int number, int status, int uid, int cid, String key, boolean has) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        if (StrUtil.isNotEmpty(key)) {
            return articleService.listByStatusAndUserAndCategoryAndKey(start, size, number, status, uid, cid, key, has);
        }
        if (cid == 0){
            return articleService.listByStatusAndUser(start, size, number, status, uid, has);
        }
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        if (has) {
            page = dao.findAllByStatusAndUidAndCid(status, uid, cid, pageable);
        }else {
            page = dao.findAllByStatusAndTypeAndUidAndCid(status, ArticleDAO.TYPE_PUBLISH, uid, cid, pageable);
        }
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }
    public PageUtil<Article> listByStatusAndUserAndCategoryAndKey(int start, int size, int number, int status, int uid, int cid, String key, boolean has) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        if (cid == 0) {
            return articleService.listByStatusAndUserAndKey(start, size, number, status, uid, key, has);
        }
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        if (has) {
            page = dao.findAllByStatusAndUidAndCidAndTitleLike(status, uid, cid, "%"+key+"%", pageable);
        }else {
            page = dao.findAllByStatusAndTypeAndUidAndCidAndTitleLike(status, ArticleDAO.TYPE_PUBLISH, uid, cid, "%"+key+"%", pageable);
        }
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByTag(int tid, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        List<TagArticle> tagArticles = tagArticleService.listByTag(tid);
        List<Integer> aids = new ArrayList<>();
        for (TagArticle tagArticle :
                tagArticles) {
            aids.add(tagArticle.getAid());
        }
        page = dao.findAllByStatusAndTypeAndIdIn(0, ArticleDAO.TYPE_PUBLISH, aids, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByTags(int uid, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        List<Tag> tags = tagService.listByUser(uid);
        List<Integer> aids = new ArrayList<>();
        for (Tag tag : tags) {
            int tid = tag.getId();
            List<TagArticle> tagArticles = tagArticleService.listByTag(tid);
            for (TagArticle tagArticle :
                    tagArticles) {
                aids.add(tagArticle.getAid());
            }
        }
        page = dao.findAllByStatusAndTypeAndIdIn(0, ArticleDAO.TYPE_PUBLISH, aids, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByUser(int uid, boolean follow, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        List<User> users = focusService.listFollow(uid, follow);
        List<Integer> aids = new ArrayList<>();
        for (User user : users) {
            int id = user.getId();
            List<Article> articles = dao.findAllByStatusAndUid(0, id, sort);
            for (Article article :
                    articles) {
                aids.add(article.getId());
            }
        }
        page = dao.findAllByStatusAndTypeAndIdIn(0, ArticleDAO.TYPE_PUBLISH, aids, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByUserAndStart(int uid, int start, int size, int number) {
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = null;
        List<Start> starts = startService.listByUser(uid);
        List<Integer> aids = new ArrayList<>();
        for (Start start1 : starts) {
            int aid = start1.getAid();
            aids.add(aid);
        }
        page = dao.findAllByStatusAndTypeAndIdIn(0, ArticleDAO.TYPE_PUBLISH, aids, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByKey(String key, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = dao.findAllByStatusAndTitleLike(0, "%"+key+"%", pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }
    public PageUtil<Article> listByKeyAndCategory(String key, int cid, int start, int size, int number, String order, boolean s) {
        sort = new Sort(s ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = new PageRequest(start, size, sort);
        Page page;
        if (cid == 0)
        {
            page = dao.findAllByStatusAndTitleLike(0, "%"+key+"%", pageable);
        }else {
            List<Category> categories = categoryService.listByParent(cid);
            List<Integer> ids = categories.stream().map(category -> {return category.getId();}).collect(Collectors.toList());
            page = dao.findAllByStatusAndCidInAndTitleLike(0, ids, "%"+key+"%", pageable);
        }
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        PageUtil<Article> pages = new PageUtil<Article>(page, number);
        return pages;
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public long sum() {
        return dao.count();
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public int viewSum(List<Article> articles) {
        int sum = 0;
        for (Article article : articles) {
            sum += article.getView();
        }
        return sum;
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public int commentSum(List<Article> articles) {
        int sum = 0;
        for (Article article : articles) {
            sum += article.getComment();
        }
        return sum;
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> listByType(String type) {
        return dao.findAllByStatusAndType(0, type, sort);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<Article> list5ByStatusAndUser(int status, int uid, boolean has) {
        if (has) {
            return dao.findTop5ByStatusAndUid(status, uid, sort);
        }else {
            return dao.findTop5ByStatusAndTypeAndUid(status, ArticleDAO.TYPE_PUBLISH, uid, sort);
        }
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> list(int start, int size, int number) {
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = dao.findAll(pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        return new PageUtil<Article>(page, number);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByStatus(int start, int size, int number, int status) {
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = dao.findAllByStatus(status, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        return new PageUtil<Article>(page, number);
    }

    //    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listByStatusAndUser(int start, int size, int number, int status, int uid, boolean has) {
        Pageable pageable = new PageRequest(start, size, sort);
        Page page;
        if (has) {
            page = dao.findAllByStatusAndUid(status, uid, pageable);
        }else {
            page = dao.findAllByStatusAndTypeAndUid(status, ArticleDAO.TYPE_PUBLISH, uid, pageable);
        }
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        return new PageUtil<Article>(page, number);
    }
    public PageUtil<Article> listByStatusAndUserAndKey(int start, int size, int number, int status, int uid, String key, boolean has) {
        Pageable pageable = new PageRequest(start, size, sort);
        Page page;
        if (has){
            page = dao.findAllByStatusAndUidAndTitleLike(status, uid, "%"+key+"%", pageable);
        }else {
            page = dao.findAllByStatusAndTypeAndUidAndTitleLike(status, ArticleDAO.TYPE_PUBLISH, uid, "%"+key+"%", pageable);
        }
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        return new PageUtil<Article>(page, number);
    }

    @Cacheable(keyGenerator = "wiselyKeyGenerator")
    public PageUtil<Article> listForShow(int start, int size, int number, int status) {
        sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page page = dao.findAllByStatusAndType(status, ArticleDAO.TYPE_PUBLISH, pageable);
        page = new RestPageImpl(page.getContent(), pageable, page.getTotalElements());
        return new PageUtil<Article>(page, number);
    }

    public void fillCategory(Article article) {
        int cid = article.getCid();
        Category category = categoryService.get(cid);
        categoryService.fillChild(category);
        article.setCategory(category);
    }

    public void fillCategory(List<Article> articles) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        for (Article article :
                articles) {
            articleService.fillCategory(article);
        }
    }

    public void fillLikeAndStart(Article article, int uid) {
        Like like = likeService.getArticle(article.getId(), uid);
        Start start = startService.get(article.getId(), uid);
        article.setHasLike(like != null);
        article.setHasStart(start != null);
    }

    public void fillLikeAndStart(List<Article> articles, int uid) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        for (Article article :
                articles) {
            articleService.fillLikeAndStart(article, uid);
        }
    }

    public void fillTags(Article article) {
        List<Tag> tags = tagService.listByArticle(article.getId());
        article.setTags(tags);
    }

    public void fillTags(List<Article> articles) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        for (Article article :
                articles) {
            articleService.fillTags(article);
        }
    }

    public void fillUser(Article article) {
        User user = userService.get(article.getUid());
        article.setUser(user);
    }

    public void fillUser(List<Article> articles) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        for (Article article :
                articles) {
            articleService.fillUser(article);
        }
    }


    public void fillArticle(List<Article> articles) {
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        for (Article article : articles) {
            articleService.fillArticle(article);
        }
    }

    public void fillArticle(Article article){
        ArticleService articleService = SpringContextUtils.getBean(ArticleService.class);
        articleService.fillCategory(article);
        articleService.fillTags(article);
        articleService.fillUser(article);
    }
}
