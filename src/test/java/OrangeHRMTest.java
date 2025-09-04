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
    @Order(2)
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
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).press("Enter");

        }
    }

    @Test
    @Order(2)
    @DisplayName("Bajar Documento")
    void bajarDocumento() {
        for (Page page : pages) {
            page.navigate("https://adazzle.github.io/react-data-grid/?utm_source=chatgpt.com#/CommonFeatures");

            Download download = page.waitForDownload(() -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Export to CSV")).click();
            });
            download.saveAs(Paths.get("C:\\Users\\facun\\Downloads", download.suggestedFilename()));
        }
    }

    @Test
    @Order(3)
    @DisplayName("Encontrar primer elemento")
    void encontrarPrimerElemento() {
        for (Page page : pages) {
            page.navigate("https://adazzle.github.io/react-data-grid/?utm_source=chatgpt.com#/CommonFeatures");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Header Filters")).click();
            assertThat(page.locator(".rdg-row.r1upfr80.rdg-row-even .rdg-cell.cj343x0").nth(2)).isVisible();
            assertThat(page.locator(".rdg-row.r1upfr80.rdg-row-even .rdg-cell.cj343x0").nth(2)).containsText("Critical");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Escribir y esperar evento")
    void escribirYEsperarEvento() {
        for (Page page : pages) {
            page.navigate("http://www.uitestingplayground.com/");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Text Input")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Set New Button Name")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Set New Button Name")).fill("hola");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button That Should Change it'")).click();
            assertThat(page.locator("#updatingButton")).containsText("hola");
            assertThat(page.getByLabel("Subscribe to newsletter")).isChecked();


        }
    }

    @Test
    @Order(5)
    @DisplayName("API status")
    void apiStatusTest() {
        for (Page page : pages) {
            APIResponse response = apiContext.get("https://demoqa.com/bad-request");
            assertEquals(200, response.status(), "El GET no devolvió 200 OK");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Entrar a url")
    void entrarUrl() {
        for (Page page : pages) {
            page.navigate("https://demoqa.com/");
            page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Forms$"))).nth(2).click();
            assertThat(page).hasURL("https://demoqa.com/forms");
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
