import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

// npx playwright codegen https://e-commerceerce.vercel.app/ o cualquier app para levantar la grabacion

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrangeHRMTest {

    Playwright playwright;
    List<Browser> browsers = new ArrayList<>();
    List<BrowserContext> contexts = new ArrayList<>();
    List<Page> pages = new ArrayList<>();
    static APIRequestContext apiContext;
    String[] browserNames = {"chromium", "firefox", "webkit"};

    @BeforeAll
    void setup() {
        playwright = Playwright.create();

        for (String name : browserNames) {
            Browser browser;
            switch (name) {
                case "chromium":
                    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
                    break;
                default:
                    throw new IllegalArgumentException("Browser no soportado: " + name);
            }

            browsers.add(browser);
            BrowserContext context = browser.newContext();
            contexts.add(context);
            Page page = context.newPage();
            pages.add(page);
            apiContext = playwright.request().newContext();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Abrir carrito")
    void abrirCarrito() {
        for (Page page : pages) {
            page.navigate("https://e-commerceerce.vercel.app/");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Shop Now!")).click();
            assertThat(page.locator(".w-full.gap-10.flex.flex-wrap.items-center.justify-center.my-20")).isVisible();
        }
    }

    @Test
    @Order( 1 )
    @DisplayName("login")
    void loginUser(){
        for (Page page : pages) {
            page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
            assertThat(page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"))).isVisible();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("CapsLock");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("A");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("CapsLock");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("Admin");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("Tab");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin123");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
        }
    }

    @Test
    @Order( 2)
    @DisplayName("Dashboard")
    void verifyLogin(){
        for (Page page : pages) {
            page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
            assertThat(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("company-branding"))).isVisible();

            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("CapsLock");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("A");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("CapsLock");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("Admin");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).press("Tab");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin123");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
            assertThat(page.getByRole(AriaRole.NAVIGATION, new Page.GetByRoleOptions().setName("Sidepanel"))).isVisible();

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("")).click();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("client brand logo"))).isVisible();

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("")).click();
        }
    }


    @AfterAll
    void tearDown() {
        for (Page page : pages) page.close();
        for (BrowserContext context : contexts) context.close();
        for (Browser browser : browsers) browser.close();
        if (playwright != null) playwright.close();
    }
}
