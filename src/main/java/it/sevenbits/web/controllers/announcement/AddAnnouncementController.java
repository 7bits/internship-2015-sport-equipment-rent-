package it.sevenbits.web.controllers.announcement;

import it.sevenbits.domain.Goods;
import it.sevenbits.domain.User;
import it.sevenbits.service.exceptions.UserServiceException;
import it.sevenbits.web.forms.GoodsForm;
import it.sevenbits.service.exceptions.GoodsException;
import it.sevenbits.service.GoodsService;
import it.sevenbits.service.UserService;
import it.sevenbits.web.validators.AddNewGoodsFormValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by awemath on 7/23/15.
 */
@Controller
public class AddAnnouncementController {

    @Value("${resources.path}")
    private String resourcesPath;
    @Value("${resources.images}")
    private String imagesPath;
    @Value("${resources.default-announcement-image}")
    private String defaultImage;

    @Autowired
    private AddNewGoodsFormValidator validator;

    @Autowired
    private GoodsService service;

    @Autowired
    private UserService userService;

    private Logger LOG = Logger.getLogger(AddAnnouncementController.class);


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String index(final Model model) {
        // В модель добавим новый объект формы подписки
        model.addAttribute("goods", new GoodsForm());
        model.addAttribute("isAuth",
                SecurityContextHolder.getContext().getAuthentication().getName() != "anonymousUser");
        // Так как нет аннотации @ResponseBody, то spring будет искать шаблон по адресу home/index
        // Если шаблона не будет найдено, то вернется 404 ошибка
        return "home/add_announcement";
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String submit(@ModelAttribute final GoodsForm form,
                         final Model model,
                         @RequestParam("firstImage") final MultipartFile firstImage,
                         @RequestParam("secondImage") final MultipartFile secondImage,
                         @RequestParam("thirdImage") final MultipartFile thirdImage,
                         HttpSession session) {

        model.addAttribute("isAuth",
                SecurityContextHolder.getContext().getAuthentication().getName() != "anonymousUser");
        Map<String, String> errors;

        boolean isAuth = SecurityContextHolder.getContext().getAuthentication().getName() != "anonymousUser";
        //create list of images
        List<MultipartFile> images = new LinkedList<MultipartFile>();
        images.add(firstImage);
        images.add(secondImage);
        images.add(thirdImage);


        //form validation
        errors = validator.validate(form);
        if (errors.size() != 0) {
            // Если есть ошибки в форме, то снова рендерим главную страницу
            model.addAttribute("goods", form);
            model.addAttribute("errors", errors);
            model.addAttribute("isAuth", isAuth);
            LOG.info("Adding form contains errors.");
            return "home/add_announcement";
        }
        //feature: add announcement without auth
        if (!isAuth) {
            session.setAttribute("addNewGoods", form);
        }

        //adding announcement
        long goodsId = 0;
        User user = null;
        try {
            user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (UserServiceException e) {
            //exception
        }
        Goods goods = form.toGoods(user);
        try {
            goodsId = service.submitGoods(goods, images);
        } catch (GoodsException e) {
            LOG.error(e.getMessage());
            //exception
        } catch (UserServiceException e) {
            e.printStackTrace();
        }
        if (!isAuth) {
            for(String i:goods.getImageUrl()) {
                form.addImageUrl(i);
            }
            return "redirect:/login";
        }

        return "redirect:/see_announcement?announcement_id=" + goodsId;
    }


}
