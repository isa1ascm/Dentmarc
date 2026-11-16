(function () {
    const api = window.DentmarcAPI;
    const itemsContainer = document.getElementById("cart-items");

    function addText(parent, tagName, className, text) {
        const element = document.createElement(tagName);
        if (className) {
            element.className = className;
        }
        element.textContent = text;
        parent.appendChild(element);
        return element;
    }

    function renderCart(cart) {
        itemsContainer.replaceChildren();
        document.getElementById("cart-product-count").textContent = String(cart.cantidadProductos);
        document.getElementById("cart-subtotal").textContent = api.formatPrice(cart.subtotal);
        document.getElementById("cart-total").textContent = api.formatPrice(cart.subtotal) + " MXN";
        document.getElementById("cart-empty").hidden = cart.items.length > 0;

        cart.items.forEach(function (item) {
            const product = item.producto;
            const article = document.createElement("article");
            article.className = "carrito-item";

            const image = document.createElement("div");
            image.className = "carrito-product-image";
            image.textContent = product.nombre.charAt(0).toUpperCase();
            image.setAttribute("aria-hidden", "true");
            article.appendChild(image);

            const info = document.createElement("div");
            info.className = "carrito-item-info";
            addText(info, "p", "producto-marca", product.marca);
            addText(info, "h3", "", product.nombre);
            addText(info, "p", "texto-suave", product.categoria.nombre);
            addText(info, "p", "existencia", product.cantidadDisponible > 0 ? "En existencia" : "Agotado");
            article.appendChild(info);
            addText(article, "p", "carrito-precio", api.formatPrice(item.precioUnitario));

            const form = document.createElement("form");
            form.className = "cantidad";
            form.dataset.cartUpdate = String(product.id);
            const input = document.createElement("input");
            input.type = "number";
            input.name = "cantidad";
            input.min = "1";
            input.max = String(product.cantidadDisponible);
            input.value = String(item.cantidad);
            input.setAttribute("aria-label", "Cantidad para " + product.nombre);
            form.appendChild(input);
            const update = document.createElement("button");
            update.type = "submit";
            update.textContent = "Actualizar";
            update.setAttribute("aria-label", "Actualizar cantidad de " + product.nombre);
            form.appendChild(update);
            article.appendChild(form);

            const total = document.createElement("div");
            total.className = "carrito-total";
            addText(total, "p", "", api.formatPrice(item.total));
            const remove = document.createElement("button");
            remove.type = "button";
            remove.className = "quitar";
            remove.textContent = "Quitar";
            remove.dataset.cartRemove = String(product.id);
            total.appendChild(remove);
            article.appendChild(total);
            itemsContainer.appendChild(article);
        });
    }

    async function loadCart() {
        if (!api.requireAuthentication()) {
            return;
        }
        try {
            renderCart(await api.request("/cart", { auth: true }));
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
            } else {
                api.showMessage(document.getElementById("cart-message"), error.message, true);
            }
        }
    }

    itemsContainer.addEventListener("submit", async function (event) {
        const form = event.target.closest("[data-cart-update]");
        if (!form) {
            return;
        }
        event.preventDefault();
        const cantidad = Number(form.elements.cantidad.value);
        try {
            const cart = await api.request("/cart/items/" + encodeURIComponent(form.dataset.cartUpdate), {
                method: "PUT",
                auth: true,
                body: { cantidad: cantidad }
            });
            renderCart(cart);
            api.showMessage(document.getElementById("cart-message"), "Carrito actualizado.", false);
            api.refreshCartCount();
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
            } else {
                api.showMessage(document.getElementById("cart-message"), error.message, true);
            }
        }
    });

    itemsContainer.addEventListener("click", async function (event) {
        const button = event.target.closest("[data-cart-remove]");
        if (!button) {
            return;
        }
        try {
            const cart = await api.request("/cart/items/" + encodeURIComponent(button.dataset.cartRemove), {
                method: "DELETE",
                auth: true
            });
            renderCart(cart);
            api.showMessage(document.getElementById("cart-message"), "Producto retirado del carrito.", false);
            api.refreshCartCount();
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
            } else {
                api.showMessage(document.getElementById("cart-message"), error.message, true);
            }
        }
    });

    document.addEventListener("DOMContentLoaded", loadCart);
})();
