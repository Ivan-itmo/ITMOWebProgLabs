document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('checkForm');
    const xInput = document.getElementById('xInput');
    const ySelect = document.getElementById('ySelect');
    const rRadios = document.querySelectorAll('input[name="r"]');
    const errorMessageDiv = document.getElementById('errorMessage');

    function showErrorMessage(message) {
        const errorMessageDiv = document.getElementById('errorMessage');
        errorMessageDiv.textContent = message;
        errorMessageDiv.style.display = 'block';
    }

    function hideErrorMessage() {
        const errorMessageDiv = document.getElementById('errorMessage');
        errorMessageDiv.style.display = 'none';
    }

    form.addEventListener('submit', function (e) {
        let isValid = true;
        let errorMessage = "";

        const x = xInput.value.trim();
        if (x === "") {
            errorMessage = "Поле X не должно быть пустым.\n";
            isValid = false;
        } else if (x.length > 8) {
            errorMessage += "X не должен содержать более 8 символов.\n";
            isValid = false;
        } else {
            const numberRegex = /^-?\d*\.?\d+$/;
            if (!numberRegex.test(x)) {
                errorMessage += "X должен быть числом.\n";
                isValid = false;
            } else {
                const xNum = parseFloat(x);
                if (xNum < -5 || xNum > 5) {
                    errorMessage += "X должен быть в диапазоне от -5 до 5.\n";
                    isValid = false;
                }
            }
        }

        if (ySelect.value === "") {
            errorMessage += "Выберите значение Y.\n";
            isValid = false;
        }

        let rSelected = false;
        rRadios.forEach(radio => {
            if (radio.checked) rSelected = true;
        });
        if (!rSelected) {
            errorMessage += "Выберите радиус R.";
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
            showErrorMessage(errorMessage.trim());
        } else {
            hideErrorMessage();
        }
    });

    xInput.addEventListener('input', hideErrorMessage);
    ySelect.addEventListener('change', hideErrorMessage);
    rRadios.forEach(radio => {
        radio.addEventListener('change', hideErrorMessage);
    });

    xInput.addEventListener('input', function () {
        const x = xInput.value.trim();
        if (x === "") {
            xInput.style.borderColor = "#ddd";
        } else {
            const xNum = parseFloat(x);
            if (!isNaN(xNum) && xNum >= -5 && xNum <= 5) {
                xInput.style.borderColor = "#28a745";
            } else {
                xInput.style.borderColor = "#dc3545";
            }
        }
    });
});

