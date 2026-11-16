(function () {
    const tokenKey = "dentmarc.jwt";
    const returnToKey = "dentmarc.returnTo";
    const pendingCartItemKey = "dentmarc.pendingCartItem";

    async function request(path, options) {
        const settings = options || {};
        const headers = new Headers(settings.headers || {});
        let body = settings.body;

        if (settings.auth) {
            const token = sessionStorage.getItem(tokenKey);
            if (token) {
                headers.set("Authorization", "Bearer " + token);
            }
        }
        if (body !== undefined && typeof body !== "string") {
            headers.set("Content-Type", "application/json");
            body = JSON.stringify(body);
        }

        const response = await fetch("/api/v1" + path, {
            method: settings.method || "GET",
            headers: headers,
            body: body,
            credentials: "same-origin"
        });
        if (response.status === 204) {
            return null;
        }

        const result = await response.json();
        if (!response.ok) {
            const error = new Error(result.message || "No se pudo completar la solicitud.");
            error.status = response.status;
            error.fieldErrors = result.fieldErrors || [];
            throw error;
        }
        return result;
    }

    function showMessage(element, message, isError) {
        if (!element) {
            return;
        }
        element.textContent = message || "";
        element.hidden = !message;
        element.classList.toggle("api-error", Boolean(message && isError));
    }

    function storeReturnToCurrentPage() {
        const path = window.location.pathname + window.location.search;
        sessionStorage.setItem(returnToKey, path.startsWith("/") && !path.startsWith("//") ? path : "/cuenta");
    }

    function requireAuthentication() {
        if (sessionStorage.getItem(tokenKey)) {
            return true;
        }
        storeReturnToCurrentPage();
        window.location.assign("/login");
        return false;
    }

    function handleUnauthorized() {
        sessionStorage.removeItem(tokenKey);
        storeReturnToCurrentPage();
        window.location.assign("/login");
    }

    function completeAuthentication(authResponse) {
        sessionStorage.setItem(tokenKey, authResponse.token);
        const returnTo = sessionStorage.getItem(returnToKey);
        sessionStorage.removeItem(returnToKey);
        return returnTo && returnTo.startsWith("/") && !returnTo.startsWith("//")
            ? returnTo
            : "/cuenta";
    }

    function formatPrice(value) {
        return new Intl.NumberFormat("es-MX", {
            style: "currency",
            currency: "MXN"
        }).format(value);
    }

    async function refreshCartCount() {
        const counter = document.querySelector("[data-cart-count]");
        if (!counter || !sessionStorage.getItem(tokenKey)) {
            return;
        }
        try {
            const cart = await request("/cart", { auth: true });
            counter.textContent = String(cart.cantidadProductos);
        } catch (error) {
            if (error.status === 401) {
                sessionStorage.removeItem(tokenKey);
                counter.textContent = "0";
            } else {
                console.error("No se pudo actualizar el contador del carrito.", error);
            }
        }
    }

    window.DentmarcAPI = {
        request: request,
        showMessage: showMessage,
        requireAuthentication: requireAuthentication,
        handleUnauthorized: handleUnauthorized,
        completeAuthentication: completeAuthentication,
        formatPrice: formatPrice,
        refreshCartCount: refreshCartCount,
        tokenKey: tokenKey,
        pendingCartItemKey: pendingCartItemKey
    };

    document.addEventListener("DOMContentLoaded", refreshCartCount);
})();
