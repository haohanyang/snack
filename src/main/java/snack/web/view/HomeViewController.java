package snack.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import snack.service.UserService;

@Controller
public class HomeViewController {

    private final Logger logger = LoggerFactory.getLogger(HomeViewController.class);
    private final UserService userService;

    public HomeViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OidcUser principal) {
        return "index";
    }
}
