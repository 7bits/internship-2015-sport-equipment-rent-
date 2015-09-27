package it.sevenbits.web.controllers.announcement;

import it.sevenbits.web.domain.GoodsForm;
import it.sevenbits.web.service.goods.AddNewGoodsFormValidator;
import it.sevenbits.web.service.goods.GoodsException;
import it.sevenbits.web.service.goods.GoodsService;
import it.sevenbits.web.service.users.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by awemath on 7/23/15.
 */
@Controller
@RequestMapping(value = "/confirm")
public class ConfirmController {

    Logger LOG = Logger.getLogger(ConfirmController.class);

    @Autowired
    GoodsService service;

    @Autowired
    AddNewGoodsFormValidator validator;

    @Autowired
    UserService userService;


    @RequestMapping(method = RequestMethod.GET)
    public String confirm(final Model model, HttpSession session){
        GoodsForm form = (GoodsForm) session.getAttribute("addNewGoods");
        session.removeAttribute("addNewGoods");

        model.addAttribute("isAuth", SecurityContextHolder.getContext().getAuthentication().getName()!="anonymousUser");
        if(form==null) {
            return "redirect:/";
        }else{
            session.setAttribute("images", form.getImageUrl());
            model.addAttribute("goods", form);
        }
        return "home/confirm_announcement";
    }
    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute GoodsForm form, final Model model, HttpSession session) {
        final Map<String, String> errors = validator.validate(form);


        long goodsId = 0;
        try {
            goodsId = service.submitGoods(form, new LinkedList<MultipartFile>(), errors, session);
        } catch (GoodsException e) {
            LOG.error(e.getMessage());
            //error
        }
        boolean isAuth = SecurityContextHolder.getContext().getAuthentication().getName() != "anonymousUser";
        if (errors.size() != 0) {
            // Если есть ошибки в форме, то снова рендерим главную страницу
            model.addAttribute("goods", form);
            model.addAttribute("errors", errors);
            model.addAttribute("isAuth", isAuth);
            return "home/confirm_announcement";
        }
<<<<<<< Updated upstream

        /*if(form.getFirstImageUrl()!=null)
            service.addImage(goods.getId(), form.getFirstImageUrl());
        if(form.getSecondImageUrl()!=null)
            service.addImage(goods.getId(), form.getSecondImageUrl());
        if(form.getThirdImageUrl()!=null)
            service.addImage(goods.getId(), form.getThirdImageUrl());
        */
=======
        try {
            User user = null;
            try {
                user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
            } catch (UserServiceException e) {
                LOG.error("An error appeared on getting user from repository "+e.getMessage());
            }
            Goods goods = form.toGoods(user);
            goods.setImageUrl((List<String>) session.getAttribute("images"));
            goodsId = service.submitGoods(goods, new LinkedList<MultipartFile>());
        } catch (GoodsException e) {
            LOG.error("An error appeared on submitting goods " + e.getMessage());
            //exception

        }
>>>>>>> Stashed changes

        return "redirect:/see_announcement?announcement_id="+goodsId;
    }
}
