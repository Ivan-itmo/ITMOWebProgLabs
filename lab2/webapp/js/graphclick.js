document.addEventListener('DOMContentLoaded', function() {
    const canvas = document.getElementById('areaGraph');
    canvas.addEventListener('click', function(e) {
        const r = document.querySelector('input[name="r"]:checked')?.value;

        if (!r) {
            const errorDiv = document.getElementById('errorMessage');
            if (errorDiv) {
                errorDiv.textContent = "Выберите радиус R!";
                errorDiv.style.display = 'block';
            }
            return;
        }

        const rect = canvas.getBoundingClientRect();
        const clickX = e.clientX - rect.left;
        const clickY = e.clientY - rect.top;

        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        const scale = 50;

        const graphX = ((clickX - centerX) / (scale * 4)) * r;
        const graphY = ((centerY - clickY) / (scale * 4)) * r;

        const x = Math.round(graphX * 100) / 100;
        const y = Math.round(graphY * 100) / 100;


        const errorDiv = document.getElementById('errorMessage');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
        window.location.href = `?x=${x}&y=${y}&r=${r}`;
    });
});