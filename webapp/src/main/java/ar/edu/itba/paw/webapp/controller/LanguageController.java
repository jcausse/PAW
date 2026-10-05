package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.AuthHelper;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class LanguageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);

    private final UserService userService;
    private final AuthHelper authHelper;

    /*
     * The '?lang=' param is consumed by LocaleChangeInterceptor, which switches the session
     * cookie before this handler runs. Here we persist that choice as the logged-in user's
     * preferred language so the website locale and the email locale stay in sync.
     */
    @GetMapping("/language")
    public ModelAndView changeLanguage(HttpServletRequest request, Locale locale,
                                       @CurrentUser(required = false) User currentUser) {
        if (currentUser != null) {
            final Language chosen = Language.fromCode(locale.getLanguage());
            if (chosen != currentUser.getPreferredLanguage()) {
                final User updated = userService.updatePreferredLanguage(currentUser, chosen);
                authHelper.update(updated);
                LOGGER.debug("Persisted preferred language '{}' for user id={}", chosen.getCode(), currentUser.getId());
            }
        }

        final var referer = request.getHeader("Referer");
        final var target = (referer != null && !referer.isEmpty()) ? referer : "/";
        LOGGER.debug("Changing language, redirecting to {}", target);
        return new ModelAndView("redirect:" + target);
    }
}
