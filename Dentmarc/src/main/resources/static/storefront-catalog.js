(function () {
    const api = window.DentmarcAPI;
    let currentProducts = [];
    let emptyResultsMessage = "No hay productos disponibles.";
    function addText(parent, tagName, className, text) {
        const element = document.createElement(tagName);
        if (className) {
            element.className = className;
        }
        element.textContent = text;
        parent.appendChild(element);
        return element;
    }

    function makeProductCard(product) {
        const card = document.createElement("article");
        card.className = "producto-tarjeta";

        const imageArea = document.createElement("div");
        imageArea.className = "tarjeta-imagen product-image-placeholder";
        imageArea.setAttribute("aria-hidden", "true");
        addText(imageArea, "span", "", product.nombre.charAt(0).toUpperCase());
        card.appendChild(imageArea);

        const details = document.createElement("div");
        details.className = "tarjeta-info";
        const brand = document.createElement("div");
        brand.className = "marca-fecha";
        addText(brand, "span", "", product.marca);
        details.appendChild(brand);
        addText(details, "h3", "", product.nombre);
        addText(details, "p", "existencia", product.cantidadDisponible > 0 ? "Disponible" : "Agotado");

        const actions = document.createElement("div");
        actions.className = "precio-accion";
        addText(actions, "span", "precio", api.formatPrice(product.precio));

        const addButton = document.createElement("button");
        addButton.className = "btn-agregar";
        addButton.type = "button";
        addButton.textContent = "+";
        addButton.setAttribute("aria-label", "Agregar " + product.nombre + " al carrito");
        addButton.dataset.addProduct = String(product.id);
        addButton.disabled = product.cantidadDisponible < 1;
        actions.appendChild(addButton);
        details.appendChild(actions);

        const detailButton = document.createElement("button");
        detailButton.className = "link-accion api-product-detail";
        detailButton.type = "button";
        detailButton.textContent = "Ver detalle";
        detailButton.dataset.productDetail = String(product.id);
        details.appendChild(detailButton);
        card.appendChild(details);
        return card;
    }

    function renderProducts(products, container, countElement, emptyText) {
        currentProducts = products.slice();
        emptyResultsMessage = emptyText;
        const sort = document.getElementById("product-sort");
        const sortedProducts = currentProducts.slice();
        if (sort && sort.value === "price-asc") {
            sortedProducts.sort(function (left, right) {
                return left.precio - right.precio;
            });
        } else if (sort && sort.value === "price-desc") {
            sortedProducts.sort(function (left, right) {
                return right.precio - left.precio;
            });
        }

        container.replaceChildren();
        countElement.textContent = String(sortedProducts.length);
        if (!sortedProducts.length) {
            addText(container, "p", "texto-suave", emptyText);
            return;
        }
        sortedProducts.forEach(function (product) {
            container.appendChild(makeProductCard(product));
        });
    }

    function makeFeaturedCard(product) {
        const card = document.createElement("article");
        card.className = "producto-tarjeta";
        const image = document.createElement("div");
        image.className = "producto-imagen-placeholder";
        image.setAttribute("aria-hidden", "true");
        image.textContent = product.nombre.charAt(0).toUpperCase();
        card.appendChild(image);

        const details = document.createElement("div");
        details.className = "producto-datos";
        addText(details, "p", "producto-marca", product.marca);
        addText(details, "h3", "", product.nombre);
        const purchase = document.createElement("div");
        purchase.className = "producto-compra";
        addText(purchase, "p", "precio", api.formatPrice(product.precio));
        const addButton = document.createElement("button");
        addButton.className = "boton-agregar";
        addButton.type = "button";
        addButton.textContent = "+";
        addButton.setAttribute("aria-label", "Agregar " + product.nombre + " al carrito");
        addButton.dataset.addProduct = String(product.id);
        addButton.disabled = product.cantidadDisponible < 1;
        purchase.appendChild(addButton);
        details.appendChild(purchase);
        card.appendChild(details);

        const detailButton = document.createElement("button");
        detailButton.className = "link-accion api-product-detail";
        detailButton.type = "button";
        detailButton.textContent = "Ver detalle";
        detailButton.dataset.productDetail = String(product.id);
        details.appendChild(detailButton);
        return card;
    }

    async function loadFeaturedProducts() {
        const container = document.getElementById("api-featured");
        if (!container) {
            return;
        }
        const products = await api.request("/products");
        container.replaceChildren();
        const featured = products.slice(0, 4);
        if (!featured.length) {
            addText(container, "p", "texto-suave", "No hay productos disponibles.");
            return;
        }
        featured.forEach(function (product) {
            container.appendChild(makeFeaturedCard(product));
        });
    }

    async function loadHomeCategories() {
        const container = document.getElementById("api-home-categories");
        if (!container) {
            return;
        }
        const categories = await api.request("/categories");
        container.replaceChildren();
        categories.forEach(function (category) {
            const link = document.createElement("a");
            link.href = "/categorias?categoria=" + encodeURIComponent(category.id);
            link.className = "categoria-tarjeta";
            addText(link, "h3", "", category.nombre);
            addText(link, "p", "", category.cantidadProductos + " productos");
            container.appendChild(link);
        });
    }

    async function loadCategories() {
        const container = document.getElementById("api-categories");
        if (!container) {
            return;
        }
        const selectedId = new URLSearchParams(window.location.search).get("categoria");
        const categories = await api.request("/categories");
        container.replaceChildren();

        const all = document.createElement("a");
        all.href = "/categorias";
        all.className = "pildora" + (selectedId ? "" : " activa");
        all.textContent = "Todos";
        container.appendChild(all);

        categories.forEach(function (category) {
            const link = document.createElement("a");
            link.href = "/categorias?categoria=" + encodeURIComponent(category.id);
            link.className = "pildora" + (String(category.id) === selectedId ? " activa" : "");
            link.textContent = category.nombre + " (" + category.cantidadProductos + ")";
            container.appendChild(link);
        });

        const selected = categories.find(function (category) {
            return String(category.id) === selectedId;
        });
        const heading = document.getElementById("category-heading");
        if (heading && selected) {
            heading.textContent = selected.nombre;
        }
    }

    async function loadProducts() {
        const container = document.getElementById("api-products");
        if (!container) {
            return;
        }
        const query = new URLSearchParams(window.location.search);
        const search = query.get("buscar");
        const categoryId = query.get("categoria");
        const parameters = new URLSearchParams();
        if (search) {
            parameters.set("search", search);
        }
        if (categoryId) {
            parameters.set("categoryId", categoryId);
        }
        const suffix = parameters.toString() ? "?" + parameters.toString() : "";
        const products = await api.request("/products" + suffix);
        renderProducts(products, container, document.getElementById("product-count"),
            search ? "No encontramos productos para tu búsqueda." : "No hay productos disponibles.");
    }

    async function addProduct(productId) {
        if (!sessionStorage.getItem(api.tokenKey)) {
            sessionStorage.setItem(api.pendingCartItemKey, productId);
            api.requireAuthentication();
            return;
        }
        try {
            await api.request("/cart/items", {
                method: "POST",
                auth: true,
                body: { productId: Number(productId), cantidad: 1 }
            });
            api.showMessage(document.getElementById("catalog-message"),
                "Producto agregado al carrito.", false);
            api.refreshCartCount();
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
                return;
            }
            api.showMessage(document.getElementById("catalog-message"), error.message, true);
        }
    }

    async function showProductDetail(productId) {
        const dialog = document.getElementById("product-detail");
        if (!dialog) {
            return;
        }
        try {
            const product = await api.request("/products/" + encodeURIComponent(productId));
            document.getElementById("product-detail-name").textContent = product.nombre;
            document.getElementById("product-detail-brand").textContent = product.marca;
            document.getElementById("product-detail-category").textContent = product.categoria.nombre;
            document.getElementById("product-detail-sku").textContent = product.sku;
            document.getElementById("product-detail-price").textContent = api.formatPrice(product.precio);
            document.getElementById("product-detail-stock").textContent =
                product.cantidadDisponible > 0 ? "Disponible" : "Agotado";
            dialog.showModal();
        } catch (error) {
            api.showMessage(document.getElementById("catalog-message"), error.message, true);
        }
    }

    async function addPendingItem() {
        const productId = sessionStorage.getItem(api.pendingCartItemKey);
        if (!productId || !sessionStorage.getItem(api.tokenKey)) {
            return;
        }
        sessionStorage.removeItem(api.pendingCartItemKey);
        try {
            await api.request("/cart/items", {
                method: "POST",
                auth: true,
                body: { productId: Number(productId), cantidad: 1 }
            });
            api.showMessage(document.getElementById("catalog-message"),
                "Producto agregado al carrito.", false);
            api.refreshCartCount();
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
            } else {
                api.showMessage(document.getElementById("catalog-message"), error.message, true);
            }
        }
    }

    document.addEventListener("click", function (event) {
        const addButton = event.target.closest("[data-add-product]");
        if (addButton) {
            addProduct(addButton.dataset.addProduct);
            return;
        }
        const detailButton = event.target.closest("[data-product-detail]");
        if (detailButton) {
            showProductDetail(detailButton.dataset.productDetail);
        }
    });

    document.addEventListener("DOMContentLoaded", async function () {
        const status = document.getElementById("catalog-message");
        const sort = document.getElementById("product-sort");
        if (sort) {
            sort.addEventListener("change", function () {
                const container = document.getElementById("api-products");
                const countElement = document.getElementById("product-count");
                if (container && countElement) {
                    renderProducts(currentProducts, container, countElement, emptyResultsMessage);
                }
            });
        }
        try {
            await Promise.all([loadCategories(), loadProducts(), loadFeaturedProducts(), loadHomeCategories()]);
            await addPendingItem();
        } catch (error) {
            api.showMessage(status, error.message, true);
        }

        const closeButton = document.getElementById("product-detail-close");
        const dialog = document.getElementById("product-detail");
        if (closeButton && dialog) {
            closeButton.addEventListener("click", function () {
                dialog.close();
            });
        }
    });
})();
