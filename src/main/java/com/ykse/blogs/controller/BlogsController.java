package com.ykse.blogs.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ykse.blogs.bean.Blogs;
import com.ykse.blogs.bean.Pagination;
import com.ykse.blogs.bean.User;
import com.ykse.blogs.service.BlogsService;

import net.sf.json.JSONObject;

/**
 *  博客控制器
 * 
 * <li>主要含：展示博客、添加博客、保存博客、获取修改的博客信息、修改博客、删除博客</li>
 * 
 * @author tao.huang
 *@version $Id: BlogsController.java, v 0.1 2016年11月14日 下午5:32:34 tao.huang Exp $
 */
@Controller
@RequestMapping("/listBlogs")
public class BlogsController {

    @Autowired
    private BlogsService blogsService;

    /**
     * 展示博客
     * 
     * @param request
     * @return
     */
    @RequestMapping(value="")
    public ModelAndView getBlogs(HttpServletRequest request ) {
		ModelAndView modelAndView = new ModelAndView("/blogs/listBlogs");
		
		Pagination<Blogs> page = new Pagination<Blogs>();
		String pageNum = (String) request.getParameter("pageNum");
		String numPerPage = (String) request.getParameter("numPerPage");
		Integer pagenum = (pageNum == null || pageNum == "")
				? 1 : Integer.parseInt(pageNum);
		Integer numperpage = (numPerPage == null || numPerPage == "")
				? 10 : Integer.parseInt(numPerPage);
		page.setCurrentPage(pagenum);
		page.setNumPerPage(numperpage);
		page.setTotalCount(blogsService.getBlogsCount());
		page.calcutePage();
		
		int startRow = (page.getCurrentPage() - 1) * page.getNumPerPage();
		int endRow = page.getNumPerPage();
		List<Blogs> blogs = blogsService.getBlogsAll(startRow, endRow);
		page.setContent(blogs);
		request.setAttribute("page", page);
		
		return modelAndView;
    }
    
    /**
     * 添加博客
     * 
     * @return
     */
    @RequestMapping(value="/addBlogs", method=RequestMethod.GET)    
    public ModelAndView getUserInfo(Model model){      
        ModelAndView modelAndView = new ModelAndView("blogs/addBlogs");
        return modelAndView;
    }
    
    /**
     * 保存博客
     * 
     * @param blogsTitle
     * @param blogsContent
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveBlogs",method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    public String addBlogs(String blogsTitle,String blogsContent,HttpSession httpSession) {
        Blogs blogs = new Blogs();
        blogs.setBlogsTitle(blogsTitle);
        blogs.setBlogsContent(blogsContent);
        blogs.setCommentCount(0);
        blogs.setCreateTime(new Timestamp(System.currentTimeMillis()));
        User user = (User)httpSession.getAttribute("User");
        blogs.setUser(user);
        
        JSONObject result = new JSONObject();
        if(blogsService.saveBlogs(blogs)){
            result.put("message", "保存成功！");
            result.put("statusCode", "200");
            result.put("dialog", "true");
            return result.toString();
        }
        result.put("message", "保存失败！");
        result.put("statusCode", "300");
        result.put("dialog", "closeCurrent");
        return result.toString();
    }
    
    /**
     * 获取修改的博客信息
     * 
     * @param blogsId
     * @param request
     * @return
     */
    @RequestMapping(value="/getBlogsInfo", method=RequestMethod.GET)    
    public ModelAndView updateInfo(String blogsId, HttpServletRequest request){  
        Blogs blogs =  blogsService.getBlogsById(Integer.parseInt(blogsId));
        request.setAttribute("blogsId", blogs.getBlogsId());
        request.setAttribute("blogsTitle", blogs.getBlogsTitle());
        request.setAttribute("blogsContent", blogs.getBlogsContent());
        ModelAndView modelAndView = new ModelAndView("/blogs/updateBlogs");      
        return modelAndView;
    }
    
    /**
     * 修改博客
     * 
     * @param blogsTitle
     * @param blogsContent
     * @param blogsId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateBlogs",method = RequestMethod.POST)
    public String updateBlog(String blogsTitle,String blogsContent,int blogsId) {
        Blogs blogs = new Blogs();
        blogs.setBlogsId(blogsId);
        blogs.setBlogsTitle(blogsTitle);
        blogs.setBlogsContent(blogsContent);
        
        JSONObject result = new JSONObject();
        if(blogsService.updateBlogs(blogs)){
            result.put("message", "更改成功！");
            result.put("statusCode", "200");
            result.put("dialog", "true");
            return result.toString();
        }
        result.put("message", "更改失败！");
        result.put("statusCode", "300");
        result.put("dialog", "closeCurrent");
        return result.toString();
    }
}