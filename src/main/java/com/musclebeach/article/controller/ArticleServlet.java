package com.musclebeach.article.controller;

import com.musclebeach.article.model.ArticleService;
import com.musclebeach.article.model.ArticleVO;
import com.musclebeach.common.util.ApplicationContextUtil;
import org.springframework.context.ApplicationContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@WebServlet({"/back-end/article/article.do", "/front-end/article/article.do"})
public class ArticleServlet extends HttpServlet {
    private final ApplicationContext ctx = ApplicationContextUtil.getContext();
    private final ArticleService articleService = ctx.getBean(ArticleService.class);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("getOne_For_Display".equals(action)) { // 來自文章列表 list.jsp的請求

            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);

            /***************************1.接收請求參數 - 輸入格式的錯誤處理**********************/
            Integer artID = Integer.valueOf(req.getParameter("artID").trim());

            /***************************2.開始查詢資料*****************************************/
            ArticleVO articleVO = articleService.getOneArticleVO(artID);

            /***************************3.查詢完成,準備轉交(Send the Success view)*************/
            req.setAttribute("articleVO", articleVO); // 資料庫取出的empVO物件,存入req
            String url = "/front-end/article/listOneArticle.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url); // 成功轉交 listOneArticle.jsp
            successView.forward(req, res);
        }

        if ("getOne_For_Display_BY_TypeID".equals(action)) { // 來自文章列表 list.jsp的請求 查類別的文章

            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);

            /***************************1.接收請求參數 - 輸入格式的錯誤處理**********************/
            Integer typeID = Integer.valueOf(req.getParameter("typeID").trim());

            /***************************2.開始查詢資料*****************************************/
            List<ArticleVO> listByTypeID = articleService.getAllByTypeID(typeID);

            /***************************3.查詢完成,準備轉交(Send the Success view)*************/
            req.setAttribute("listByTypeID", listByTypeID); // 資料庫取出的empVO物件,存入req
            String url = "/front-end/article/listAllByArticleType.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url); // 成功轉交 listOneArticle.jsp
            successView.forward(req, res);
        }

        if ("getOne_For_Update".equals(action)) { // 來自listAllEmp.jsp的請求

            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);

            /*************************** 1.接收請求參數 ****************************************/
            Integer art_id = Integer.valueOf(req.getParameter("article"));

            /*************************** 2.開始查詢資料 ****************************************/
            ArticleVO articleVO = articleService.getOneArticleVO(art_id);

            /*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
            req.setAttribute("articleVO", articleVO); // 資料庫取出的empVO物件,存入req
            String url = "/back-end/article/update_article_input.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
            successView.forward(req, res);
        }


        if ("updateStatus".equals(action)) { // 來自update_emp_input.jsp的請求

            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);

            /***************************1.接收請求參數 - 輸入格式的錯誤處理**********************/
            Integer artID = Integer.valueOf(req.getParameter("artID").trim());
            Integer memID = Integer.valueOf(req.getParameter("memID").trim());
            Integer typeID = Integer.valueOf(req.getParameter("typeID").trim());
            String artTitle = req.getParameter("artTitle").trim();
            String artContent = req.getParameter("artContent").trim();
            Integer artStatus = Integer.valueOf(req.getParameter("artStatus").trim());

            java.sql.Timestamp artStime = null;
            try {
                artStime = java.sql.Timestamp.valueOf(req.getParameter("artStime").trim());
            } catch (IllegalArgumentException e) {
                artStime = new java.sql.Timestamp(System.currentTimeMillis());
                errorMsgs.add("請輸入日期!");
            }
            java.sql.Timestamp artLtime = null;
            try {
                artLtime = java.sql.Timestamp.valueOf(req.getParameter("artLtime").trim());
            } catch (IllegalArgumentException e) {
                artLtime = new java.sql.Timestamp(System.currentTimeMillis());
                errorMsgs.add("請輸入日期!");
            }

            ArticleVO articleVO = new ArticleVO();
            articleVO.setArtID(artID);
            articleVO.setMemID(memID);
            articleVO.setTypeID(typeID);
            articleVO.setArtTitle(artTitle);
            articleVO.setArtContent(artContent);
            articleVO.setArtStatus(artStatus);
            articleVO.setArtStime(artStime);
            articleVO.setArtLtime(artLtime);


            // Send the use back to the form, if there were errors
            if (!errorMsgs.isEmpty()) {
                req.setAttribute("articleVO", articleVO); // 含有輸入格式錯誤的empVO物件,也存入req
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/back-end/article/update_article_input.jsp");
                failureView.forward(req, res);
                return; //程式中斷
            }

            /***************************2.開始修改資料*****************************************/
            articleVO = articleService.updateArticle(artID, memID, typeID, artTitle, artContent, artStatus);
            articleVO = articleService.getOneArticleVO(artID);

            /***************************3.修改完成,準備轉交(Send the Success view)*************/
            req.setAttribute("articleVO", articleVO); // 資料庫update成功後,正確的的empVO物件,存入req
            String url = "/back-end/article/listOneArticle.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url); // 修改成功後,轉交listOneArticle.jsp
            successView.forward(req, res);
        }


        if ("insert".equals(action)) { // 來自addEmp.jsp的請求

            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);

//				/***********************1.接收請求參數 - 輸入格式的錯誤處理*************************/

            Integer memID = null;
            try {
                memID = Integer.valueOf(req.getParameter("memID").trim());
            } catch (NumberFormatException e) {
                errorMsgs.add("會員編號請填數字.");
            }


            Integer typeID = null;
            try {
                typeID = Integer.valueOf(req.getParameter("typeID").trim());
            } catch (NumberFormatException e) {
                errorMsgs.add("請選擇類別");
            }


            String artTitle = req.getParameter("artTitle");
            String artTitleReg = "^.{1,40}$";
            if (artTitle == null || artTitle.trim().length() == 0) {
                errorMsgs.add("文章標題: 請勿空白");
            } else if (!artTitle.trim().matches(artTitleReg)) { //以下練習正則(規)表示式(regular-expression)
                errorMsgs.add("長度必需在1到40之間");
            }

            String artContent = req.getParameter("artContent");
            String artContentReg = "^.{1,2000}$";
            if (artContent == null || artContent.trim().length() == 0) {
                errorMsgs.add("文章內容: 請勿空白");
            } else if (!artContent.trim().matches(artContentReg)) { //以下練習正則(規)表示式(regular-expression)
                errorMsgs.add("長度必需在1到2000之間");
            }
//				Integer artStatus = Integer.valueOf(req.getParameter("artStatus").trim());
//				
//				java.sql.Timestamp artStime = null;
//				try {
//					artStime = java.sql.Timestamp.valueOf(req.getParameter("artStime").trim());
//				} catch (IllegalArgumentException e) {
//					artStime=new java.sql.Timestamp(System.currentTimeMillis());
//					errorMsgs.add("請輸入日期!");
//				}
//				java.sql.Timestamp artLtime = null;
//				try {
//					artLtime = java.sql.Timestamp.valueOf(req.getParameter("artLtime").trim());
//				} catch (IllegalArgumentException e) {
//					artLtime=new java.sql.Timestamp(System.currentTimeMillis());
//					errorMsgs.add("請輸入日期!");
//				}

            ArticleVO articleVO = new ArticleVO();
            articleVO.setMemID(memID);
            articleVO.setTypeID(typeID);
            articleVO.setArtTitle(artTitle);
            articleVO.setArtContent(artContent);
//				articleVO.setArtStatus(artStatus);
//				articleVO.setArtStime(artStime);
//				articleVO.setArtLtime(artLtime);

            // Send the use back to the form, if there were errors
            if (!errorMsgs.isEmpty()) {
                req.setAttribute("articleVO", articleVO); // 含有輸入格式錯誤的empVO物件,也存入req
                RequestDispatcher failureView = req
                        .getRequestDispatcher("/front-end/article/addArticle.jsp");
                failureView.forward(req, res);
                return; //程式中斷
            }

            /***************************2.開始新增資料***************************************/
            articleVO = articleService.addArticle(memID, typeID, artTitle, artContent);

            /***************************3.新增完成,準備轉交(Send the Success view)***********/

            String url = "/front-end/article/listAllArticle.jsp";
            RequestDispatcher successView = req.getRequestDispatcher(url); // 修改成功後,轉交listOneArticle.jsp
            successView.forward(req, res);

        }


        if ("listArticles_ByCompositeQuery".equals(action)) { // 來自select_page.jsp的複合查詢請求
            List<String> errorMsgs = new LinkedList<String>();
            // Store this set in the request scope, in case we need to
            // send the ErrorPage view.
            req.setAttribute("errorMsgs", errorMsgs);


            /***************************1.將輸入資料轉為Map**********************************/
            //採用Map<String,String[]> getParameterMap()的方法
            //注意:an immutable java.util.Map
            //Map<String, String[]> map = req.getParameterMap();
            HttpSession session = req.getSession();
            Map<String, String[]> map = (Map<String, String[]>) session.getAttribute("map");

            // 以下的 if 區塊只對第一次執行時有效
            if (req.getParameter("whichPage") == null) {
                Map<String, String[]> map1 = new HashMap<String, String[]>(req.getParameterMap());
                session.setAttribute("map", map1);
                map = map1;
            }


            /***************************2.開始複合查詢***************************************/
            List<ArticleVO> list = articleService.getAll(map);


            /***************************3.查詢完成,準備轉交(Send the Success view)************/
            req.setAttribute("listArticles_ByCompositeQuery", list); // 資料庫取出的list物件,存入request
            RequestDispatcher successView = req.getRequestDispatcher("/back-end/article/listArticles_ByCompositeQuery.jsp"); // 成功轉交listEmps_ByCompositeQuery.jsp
            successView.forward(req, res);
        }
    }
}