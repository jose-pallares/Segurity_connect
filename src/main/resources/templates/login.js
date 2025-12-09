// Mostrar/ocultar contraseña
const togglePassword = document.getElementById("togglePassword");
const passwordInput = document.getElementById("password");

togglePassword.addEventListener("click", () => {
  const type = passwordInput.type === "password" ? "text" : "password";
  passwordInput.type = type;
  togglePassword.classList.toggle("fa-eye-slash");
});

// Validación de email
const emailInput = document.getElementById("email");
const emailError = document.getElementById("emailError");

emailInput.addEventListener("input", () => {
  const emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;
  if (!emailInput.value.match(emailPattern)) {
    emailError.textContent = "Correo no válido";
  } else {
    emailError.textContent = "";
  }
});
