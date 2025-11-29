const { createApp, ref, onMounted } = Vue;

createApp({
    setup() {
        const logout = () => {
            window.location.href = '../start.html';
        };

        const loadHistory = async () => {
            const response = await fetch('/api/check/history');
            if (response.ok) {
                const history = await response.json();
                results.value = history.map(item => ({x: item.x, y: item.y, r: item.r, hit: item.hit, timestamp: new Date(item.timestamp).toLocaleString('ru-RU')}));
                await redrawGraph();
            }
        };

        const formData = ref({x: null, y: '', r: null});
        const xOptions = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
        const rOptions = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
        const results = ref([]);
        const errorMessage = ref('');
        const areaGraph = ref(null);

        const validationY = (yStr) => {
            if (typeof yStr === 'number') {
                return !isNaN(yStr) && yStr > -3 && yStr < 3;
            }
            if (typeof yStr === 'string') {
                if (yStr.trim() === '') return false;
                const y = parseFloat(yStr);
                return !isNaN(y) && y > -3 && y < 3;
            }
            return false;
        };

        const loadScript = (src) => {
            return new Promise((resolve, reject) => {
                const existing = document.querySelector(`script[src="${src}"]`);
                if (existing) {
                    resolve();
                    return;
                }
                const script = document.createElement('script');
                script.src = src;
                script.onload = resolve;
                script.onerror = reject;
                document.head.appendChild(script);
            });
        };

        const redrawGraph = async () => {
            if (!areaGraph.value) return;

            if (results.value.length === 0) {
                await loadScript('/js/graph.js');
                if (typeof drawAreaGraph === 'function') {
                    drawAreaGraph(areaGraph.value);
                }
            } else {
                await loadScript('/js/graph-with-points.js');
                if (typeof drawAreaGraphWithPoints === 'function') {
                    const lastR = formData.value.r || results.value[0]?.r || 1;
                    drawAreaGraphWithPoints(areaGraph.value, lastR, results.value);
                }
            }
        };

        const handleCanvasClick = (e) => {
            if (!areaGraph.value) return;

            const rect = areaGraph.value.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const clickY = e.clientY - rect.top;
            const centerX = areaGraph.value.width / 2;
            const centerY = areaGraph.value.height / 2;
            const scale = 40;

            const xRaw = (clickX - centerX) / scale;
            const yRaw = (centerY - clickY) / scale;

            const xRounded = Math.round(xRaw);
            const yRounded = parseFloat(yRaw.toFixed(3));

            if (xRounded < -5 || xRounded > 3) {
                alert("Клик вне допустимого диапазона X (от -5 до 3)");
                return;
            }

            if (yRounded <= -3 || yRounded >= 3) {
                alert("Клик вне допустимого диапазона Y (от -3 до 3, не включая границы)");
                return;
            }

            let rNum;
            if (formData.value.r !== null) {
                rNum = formData.value.r;
            } else {
                const rStr = prompt("Введите R [от 1 до 3]:", "1");
                if (rStr === null) return;
                const rFloat = parseFloat(rStr.replace(',', '.'));
                if (isNaN(rFloat) || rFloat < 1 || rFloat > 3) {
                    alert("R должен быть числом от 1 до 3");
                    return;
                }

                rNum = Math.round(rFloat);
                if (![1, 2, 3].includes(rNum)) {
                    alert("R должен быть целым числом: 1, 2 или 3");
                    return;
                }

                formData.value.r = rNum;
            }

            formData.value.x = xRounded;
            formData.value.y = yRounded;

            submitForm();
        };

        const submitForm = async () => {
            errorMessage.value = '';
            if (formData.value.x === null) {
                errorMessage.value = 'Выберите X';
                return;
            }
            if (!validationY(formData.value.y)) {
                errorMessage.value = 'Y должен быть числом в диапазоне (-3, 3)';
                return;
            }
            if (formData.value.r === null) {
                errorMessage.value = 'Выберите R';
                return;
            }
            if (formData.value.r <= 0) {
                errorMessage.value = 'R должен быть положительным';
                return;
            }

            const payload = {
                x: formData.value.x,
                y: parseFloat(formData.value.y),
                r: formData.value.r
            };

            try {
                const response = await fetch('/api/check', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = '../start.html';
                        return;
                    }
                    if (response.status === 400) {
                        const errorData = await response.json().catch(() => ({}));
                        const message = errorData.message || "Некорректные данные. Форма очищена.";
                        showError(message);

                        formData.value = {x: null, y: '', r: null};

                        return;
                    }
                    throw new Error(`HTTP ${response.status}`);

                }


                const result = await response.json();
                results.value.unshift({
                    x: payload.x,
                    y: payload.y,
                    r: payload.r,
                    hit: result.hit,
                    timestamp: new Date().toLocaleString('ru-RU')
                });

                await redrawGraph();

            } catch (err) {
                errorMessage.value = 'Ошибка: ' + err.message;
            }
        };

        onMounted(() => {
            redrawGraph();
            loadHistory();
        });

        return {
            formData,
            xOptions,
            rOptions,
            results,
            errorMessage,
            areaGraph,
            submitForm,
            handleCanvasClick,
            logout
        };
    }
}).mount('#app');