package ar.edu.itba.paw.webapp.i18n;

import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.AuthHelper;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Resolves the request locale from the logged-in user's persisted preferred language,
 * making the stored preference the single source of truth for the website locale.
 *
 * For anonymous visitors it falls back to a wrapped {@link CookieLocaleResolver}, so the
 * language switch still works before logging in. When an authenticated user switches
 * language (via LocaleChangeInterceptor's '?lang='), {@link #setLocale} persists the choice
 * instead of only writing a cookie, so it survives across devices and sessions.
 */
public class DatabaseLocaleResolver implements LocaleResolver {

    private final CookieLocaleResolver cookieLocaleResolver;
    private final UserService userService;
    private final AuthHelper authHelper;

    public DatabaseLocaleResolver(final Locale defaultLocale, final UserService userService, final AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;
        this.cookieLocaleResolver = new CookieLocaleResolver();
        this.cookieLocaleResolver.setDefaultLocale(defaultLocale);
    }

    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        final User user = currentUser();
        if (user != null) {
            return localeOf(user);
        }
        return cookieLocaleResolver.resolveLocale(request);
    }

    @Override
    public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
        // Always keep the cookie coherent (used while anonymous or after logout).
        cookieLocaleResolver.setLocale(request, response, locale);

        final User user = currentUser();
        if (user != null && locale != null) {
            final Language chosen = Language.fromCode(locale.getLanguage());
            if (chosen != user.getPreferredLanguage()) {
                final User updated = userService.updatePreferredLanguage(user, chosen);
                authHelper.update(updated);
            }
        }
    }

    private static Locale localeOf(final User user) {
        final Language language = user.getPreferredLanguage() != null
                ? user.getPreferredLanguage()
                : Language.getDefault();
        return new Locale(language.getCode());
    }

    private static User currentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails details) {
            return details.getDomainUser();
        }
        return null;
    }
}
