(function () {
    const api = window.DentmarcAPI;

    function showAuthError(error) {
        const message = error.fieldErrors && error.fieldErrors.length
            ? error.message + " " + error.fieldErrors.map(function (item) {
                return item.campo + ": " + item.mensaje;
            }).join(" ")
            : error.message;
        api.showMessage(document.getElementById("auth-message"), message, true);
    }

    const loginForm = document.getElementById("login-form");
    if (loginForm) {
        loginForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            api.showMessage(document.getElementById("auth-message"), "", false);
            try {
                const result = await api.request("/auth/login", {
                    method: "POST",
                    body: {
                        email: document.getElementById("correo").value,
                        contrasenia: document.getElementById("contrasenia").value
                    }
                });
                window.location.assign(api.completeAuthentication(result));
            } catch (error) {
                showAuthError(error);
            }
        });
    }

    const registerForm = document.getElementById("register-form");
    if (registerForm) {
        registerForm.addEventListener("submit", async function (event) {
            event.preventDefault();
            api.showMessage(document.getElementById("auth-message"), "", false);
            try {
                const result = await api.request("/auth/register", {
                    method: "POST",
                    body: {
                        nombre: document.getElementById("nombre").value,
                        apellidos: document.getElementById("apellidos").value,
                        email: document.getElementById("correo").value,
                        telefono: document.getElementById("telefono").value,
                        tipoPractica: document.getElementById("practica").value.toUpperCase(),
                        contrasenia: document.getElementById("contrasenia").value
                    }
                });
                window.location.assign(api.completeAuthentication(result));
            } catch (error) {
                showAuthError(error);
            }
        });
    }
})();
