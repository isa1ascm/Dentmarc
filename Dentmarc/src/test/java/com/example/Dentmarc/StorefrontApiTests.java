package com.example.Dentmarc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.repository.ProductoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StorefrontApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void publicCatalogReturnsProductsAsDtos() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").exists())
                .andExpect(jsonPath("$[0].categoria.id").exists())
                .andExpect(jsonPath("$[0].contrasenia").doesNotExist());
    }

    @Test
    void privateAccountRequiresJwtAndReturnsStructuredError() throws Exception {
        mockMvc.perform(get("/api/v1/account"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/v1/account"));
    }

    @Test
    void customerCanLogInAndReadTheirAccountWithJwt() throws Exception {
        String login = """
                {"email":"mariana@clinicadental.mx","contrasenia":"Cliente123!"}
                """;
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);

        mockMvc.perform(get("/api/v1/account")
                        .header("Authorization", "Bearer " + json.get("token").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mariana@clinicadental.mx"))
                .andExpect(jsonPath("$.contrasenia").doesNotExist());
    }

    @Test
    void registrationRejectsInvalidInputWithFieldDetails() throws Exception {
        String registration = """
                {"nombre":"","apellidos":"Prueba","email":"no-es-correo","telefono":"123",
                 "tipoPractica":"CLINICA","contrasenia":"corta"}
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void storefrontPagesRenderTheApiBackedViews() throws Exception {
        for (String path : new String[]{"/inicio", "/categorias", "/productos", "/login", "/registro", "/cuenta", "/carrito"}) {
            mockMvc.perform(get(path)).andExpect(status().isOk());
        }
        mockMvc.perform(get("/storefront-api.js")).andExpect(status().isOk());
        mockMvc.perform(get("/storefront-catalog.js")).andExpect(status().isOk());
    }

    @Test
    void customerCartSupportsAddUpdateAndRemoveWithStockChecks() throws Exception {
        String login = """
                {"email":"mariana@clinicadental.mx","contrasenia":"Cliente123!"}
                """;
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponse).get("token").asText();
        Producto producto = productoRepository.findAll().stream()
                .filter(p -> p.getCantidad() >= 2 && p.getCategoria().isActiva())
                .findFirst().orElseThrow();
        String item = "{\"productId\":" + producto.getId() + ",\"cantidad\":1}";

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(item))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadProductos").value(1));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/v1/cart/items/" + producto.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadProductos").value(2));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/v1/cart/items/" + producto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
