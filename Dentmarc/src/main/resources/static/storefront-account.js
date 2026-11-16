(function () {
    const api = window.DentmarcAPI;

    document.addEventListener("DOMContentLoaded", async function () {
        if (!api.requireAuthentication()) {
            return;
        }
        try {
            const user = await api.request("/account", { auth: true });
            document.getElementById("account-welcome").textContent = "Hola, " + user.nombre;
            document.getElementById("account-name").textContent = user.nombre + " " + user.apellidos;
            document.getElementById("account-email").textContent = user.email;
            document.getElementById("account-phone").textContent = user.telefono;
            document.getElementById("account-practice").textContent =
                "Tipo de práctica: " + user.tipoPractica.toLowerCase();
            document.getElementById("account-initials").textContent =
                (user.nombre.charAt(0) + user.apellidos.charAt(0)).toUpperCase();
        } catch (error) {
            if (error.status === 401) {
                api.handleUnauthorized();
            } else {
                api.showMessage(document.getElementById("account-message"), error.message, true);
            }
        }

        document.getElementById("logout-button").addEventListener("click", function () {
            sessionStorage.removeItem(api.tokenKey);
            window.location.assign("/inicio");
        });
    });
})();
